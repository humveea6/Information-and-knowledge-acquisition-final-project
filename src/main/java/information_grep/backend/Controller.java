package information_grep.backend;

import com.huaban.analysis.jieba.JiebaSegmenter;
import information_grep.backend.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Autowired
    private Controller(FilesMapper filesMapper1){
        this.filesMapper=filesMapper1;

        FilesExample filesExample = new FilesExample();
        filesList= this.filesMapper.selectByExampleWithBLOBs(filesExample);
        System.out.println(JsonUtils.toJson(filesList));
        List<List<String>> docs = new ArrayList<>();
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
        }

        bm25 = new BM25(docs);
    }

    @PostMapping("/key")
    public ResponseEntity<String> search(@RequestParam String keyWord){
        List<String> testStrings = new ArrayList<>();
        testStrings.add("中国");
        testStrings.add("总统");
        List<BM25Scores> bm25ScoresList = bm25.simAll(testStrings);
        System.out.println("bm25size: "+bm25ScoresList.size());
        for(int i=0;i<10&&i<bm25ScoresList.size();i++){
            BM25Scores bm25Scores = bm25ScoresList.get(i);
            System.out.println(i+"th score: "+bm25Scores.getScore()+" index is :"+bm25Scores.getIndex());
        }

        return null;
    }

    public static void main(String[] args) {
        String test="停用词过滤主要是自己构造停用词表文本文件，并将文本中的内容读入list，对分词后的结果逐个检查是否在停用词列表中，如果在，就过滤掉，最后得到过滤后的结果";
        JiebaSegmenter jiebaSegmenter = new JiebaSegmenter();
        System.out.println(jiebaSegmenter.process(test, JiebaSegmenter.SegMode.INDEX).toString());
    }
}
