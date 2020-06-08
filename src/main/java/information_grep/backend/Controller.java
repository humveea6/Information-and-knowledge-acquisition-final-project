package information_grep.backend;

import com.huaban.analysis.jieba.JiebaSegmenter;
import information_grep.backend.utils.JsonUtils;
import information_grep.backend.utils.WebResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author LingChen <lingchen@kuaishou.com>
 * Created on 2020-06-07
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class Controller {

    private  FilesMapper filesMapper;

    private static List<Files> filesList;

    private static BM25 bm25;

    private static Set<String> stopWords = new HashSet<>();

    private static ExecutorService executorService =
            new ThreadPoolExecutor(10,20,10, TimeUnit.SECONDS,new ArrayBlockingQueue<>(500));

    private static ReentrantLock resListLock = new ReentrantLock();

    static {
        File file=new File("src/main/resources/stop_words.txt");
        BufferedReader reader=null;
        String temp;
        try{
            reader=new BufferedReader(new FileReader(file));
            while((temp=reader.readLine())!=null){
                stopWords.add(temp);
//                System.out.println("line"+line+":"+temp);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            if(reader!=null){
                try{
                    reader.close();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Autowired
    private Controller(FilesMapper filesMapper1){
        this.filesMapper=filesMapper1;

        FilesExample filesExample = new FilesExample();
        filesList= this.filesMapper.selectByExampleWithBLOBs(filesExample);
        System.out.println(JsonUtils.toJson(filesList));
        List<List<String>> docs = new ArrayList<>();
        List<String> completeDocs = new ArrayList<>();
        List<String> titles = new ArrayList<>();
//        int i=0;
        for (Files files : filesList) {
            String cleanedWords = files.getCleanedWords();
            if(StringUtils.isEmpty(cleanedWords)){
                System.out.println("null string!!!!");
            }
            cleanedWords = cleanedWords.substring(2, cleanedWords.length() - 2);
            String[] temp;
            String delimeter = "', '";
            temp = cleanedWords.split(delimeter);
            List<String> list = Arrays.asList(temp);

//            if(i++<10){
//                for (String s : list) {
//                    System.out.println(i+"th docs,word is: "+s);
//                }
//            }
            docs.add(list);
            completeDocs.add(files.getTotalWords());
            titles.add(files.getTitle());
        }

        bm25 = new BM25(docs,completeDocs,titles);
    }

    @PostMapping("/key")
    public ResponseEntity<String> search(@RequestParam String keyWord) throws InterruptedException {

        JiebaSegmenter jiebaSegmenter = new JiebaSegmenter();
        List<String> words =
                jiebaSegmenter.process(keyWord, JiebaSegmenter.SegMode.INDEX).stream().map(segToken -> segToken.word).collect(Collectors.toList());
        System.out.println("full size: "+words.size());
        words.removeIf(next -> stopWords.contains(next));
        System.out.println("remove size: "+words.size());
//        for (String word : words) {
//            System.out.println(word);
//        }

        Set<String> wordSet = new HashSet<>(words);

//        List<String> testStrings = new ArrayList<>();
//        testStrings.add("中国");
//        testStrings.add("总统");
        List<BM25Scores> bm25ScoresList = bm25.simAll(words).stream().limit(20).collect(Collectors.toList());
//        System.out.println(JsonUtils.toJson(bm25ScoresList));
        List<ArticleVo> resList = new ArrayList<>();

        CountDownLatch countDownLatch = new CountDownLatch(bm25ScoresList.size());
        for (BM25Scores bm25Scores : bm25ScoresList) {
            Integer index = bm25Scores.getIndex();

            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    ArticleVo articleVo = new ArticleVo();
                    articleVo.setDoc(bm25.completeDocs.get(index));
                    articleVo.setTitle(bm25.titles.get(index));
                    articleVo.setScore(bm25Scores.getScore());

                    Map<String, Integer> wordsCountMap = bm25.f[index];
                    Map<String,Integer> existWords = new HashMap<>();
                    log.info("{}, wordsCountMap: "+JsonUtils.toJson(wordsCountMap),Thread.currentThread().getName());

                    for(Map.Entry<String,Integer> entry:wordsCountMap.entrySet()){
                        String key = entry.getKey();
                        if(wordSet.contains(key)){
                            existWords.put(entry.getKey(),entry.getValue());
                        }
                    }

                    log.info("{}, existwords: "+JsonUtils.toJson(existWords),Thread.currentThread().getName());

                    List<Map.Entry<String,Integer>> wordList = new ArrayList<>(existWords.entrySet());
                    Collections.sort(wordList, new Comparator<Map.Entry<String, Integer>>() {
                        @Override
                        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                            return o2.getValue()-o1.getValue();
                        }
                    });
                    List<Map.Entry<String, Integer>> collect = wordList.stream().limit(10).collect(Collectors.toList());

                    log.info("{}, top10 words:"+JsonUtils.toJson(collect),Thread.currentThread().getName());
                    List<Top10WordsVo> top10WordsVos = new ArrayList<>();
                    collect.forEach(map->top10WordsVos.add(new Top10WordsVo(map.getKey(),map.getValue())));
                    articleVo.setTop10Words(top10WordsVos);

                    log.info("{}, articleVo: "+JsonUtils.toJson(articleVo),Thread.currentThread().getName());

                    try {
                        resListLock.lock();
                        resList.add(articleVo);
                    }
                    finally {
                        resListLock.unlock();
                    }
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await(3000,TimeUnit.MILLISECONDS);
        Collections.sort(resList, new Comparator<ArticleVo>() {
            @Override
            public int compare(ArticleVo o1, ArticleVo o2) {
                if(o1.getScore()>o2.getScore()){
                    return -1;
                }
                else if(o2.getScore()>o1.getScore()){
                    return 1;
                }
                else{
                    return 0;
                }
            }
        });

        return WebResultUtil.buildResult(new ResponseVo(200,"OK",resList), HttpStatus.OK);
    }

    public static void main(String[] args) {

        String test="停用词过滤主要是自己构造停用词表文本文件，并将文本中的内容读入list，对分词后的结果逐个检查是否在停用词列表中，如果在，就过滤掉，最后得到过滤后的结果";

    }
}
