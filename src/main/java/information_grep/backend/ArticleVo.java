package information_grep.backend;

import java.util.List;
import java.util.Map;

/**
 * @author LingChen <lingchen@kuaishou.com>
 * Created on 2020-06-07
 */
public class ArticleVo {

    private String title;

    private String doc;

    private Double score;

    private String time;

    private String URL;

    private InformationExtractionVo informationExtractionVo;

    public InformationExtractionVo getInformationExtractionVo() {
        return informationExtractionVo;
    }

    public void setInformationExtractionVo(InformationExtractionVo informationExtractionVo) {
        this.informationExtractionVo = informationExtractionVo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    private List<Top10WordsVo> top10Words;

    public List<Top10WordsVo> getTop10Words() {
        return top10Words;
    }

    public void setTop10Words(List<Top10WordsVo> top10Words) {
        this.top10Words = top10Words;
    }

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
