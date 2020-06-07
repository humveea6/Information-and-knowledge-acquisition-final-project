package information_grep.backend;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author LingChen <lingchen@kuaishou.com>
 * Created on 2020-06-07
 */
public class mapperTest extends BackendApplicationTests {

    @Autowired
    private FilesMapper filesMapper;

    @Test
    public  void test() {
        Files files = filesMapper.selectByPrimaryKey("1000");
        String cleanedWords = files.getCleanedWords();
        cleanedWords = cleanedWords.substring(2,cleanedWords.length()-2);
        String[] temp;
        String delimeter = "', '";
        temp = cleanedWords.split(delimeter);
        List<String> list = Arrays.asList(temp);
        for (String s : list) {
            System.out.println(s);
        }
    }
}
