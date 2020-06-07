package information_grep.backend;

/**
 * @author LingChen <lingchen@kuaishou.com>
 * Created on 2020-06-07
 */
public class ArticleVo {

    private String title;

    private String doc;

    private Double score;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
