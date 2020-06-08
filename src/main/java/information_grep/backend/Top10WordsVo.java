package information_grep.backend;

/**
 * @author LingChen <lingchen@kuaishou.com>
 * Created on 2020-06-08
 */
public class Top10WordsVo {

    private String word;

    private Integer count;

    public Top10WordsVo() {
    }

    public Top10WordsVo(String word, Integer count) {
        this.word = word;
        this.count = count;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
