package information_grep.backend;


import java.util.List;

/**
 * @author LingChen <lingchen@kuaishou.com>
 * Created on 2020-06-07
 */
public class ResponseVo {

    private Integer status;

    private String msg;

    private List<ArticleVo> data;

    public ResponseVo(Integer status, String msg, List<ArticleVo> data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<ArticleVo> getData() {
        return data;
    }

    public void setData(List<ArticleVo> data) {
        this.data = data;
    }
}
