package information_grep.backend;

/**
 * @author LingChen <lingchen@kuaishou.com>
 * Created on 2020-06-07
 */
public class BM25Scores {

    Double score;

    Integer index;

    public BM25Scores(Double score, Integer index) {
        this.score = score;
        this.index = index;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
