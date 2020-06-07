package information_grep.backend;

import com.huaban.analysis.jieba.JiebaSegmenter;
import information_grep.backend.utils.JsonUtils;
import information_grep.backend.utils.WebResultUtil;
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
import java.util.stream.Collectors;

/**
 * @author LingChen <lingchen@kuaishou.com>
 * Created on 2020-06-07
 */
@RestController
@RequestMapping("/search")
public class Controller {

    private  FilesMapper filesMapper;

    private static List<Files> filesList;

    private static BM25 bm25;

    private static Set<String> stopWords = new HashSet<>();

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
    public ResponseEntity<String> search(@RequestParam String keyWord){

        JiebaSegmenter jiebaSegmenter = new JiebaSegmenter();
        List<String> words =
                jiebaSegmenter.process(keyWord, JiebaSegmenter.SegMode.INDEX).stream().map(segToken -> segToken.word).collect(Collectors.toList());
        System.out.println("full size: "+words.size());
        words.removeIf(next -> stopWords.contains(next));
        System.out.println("remove size: "+words.size());
        for (String word : words) {
            System.out.println(word);
        }

//        List<String> testStrings = new ArrayList<>();
//        testStrings.add("中国");
//        testStrings.add("总统");
        List<BM25Scores> bm25ScoresList = bm25.simAll(words).stream().limit(20).collect(Collectors.toList());
//        System.out.println(JsonUtils.toJson(bm25ScoresList));
        List<ArticleVo> resList = new ArrayList<>();

        for (BM25Scores bm25Scores : bm25ScoresList) {
            Integer index = bm25Scores.getIndex();
            ArticleVo articleVo = new ArticleVo();
            articleVo.setDoc(bm25.completeDocs.get(index));
            articleVo.setTitle(bm25.titles.get(index));
            articleVo.setScore(bm25Scores.getScore());
            resList.add(articleVo);
        }

        return WebResultUtil.buildResult(new ResponseVo(200,"OK",resList), HttpStatus.OK);
    }

    public static void main(String[] args) {

        String test="停用词过滤主要是自己构造停用词表文本文件，并将文本中的内容读入list，对分词后的结果逐个检查是否在停用词列表中，如果在，就过滤掉，最后得到过滤后的结果";

    }
}
