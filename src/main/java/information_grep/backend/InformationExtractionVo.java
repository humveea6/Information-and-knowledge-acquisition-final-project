package information_grep.backend;

import java.util.List;

/**
 * @author LingChen <lingchen@kuaishou.com>
 * Created on 2020-06-25
 */
public class InformationExtractionVo {

    private String name;

    private String[] locations;

    private String[] cellphones;

    private String[] emails;

    private String[] ids;

    private String[] key_words;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getLocations() {
        return locations;
    }

    public void setLocations(String[] locations) {
        this.locations = locations;
    }

    public String[] getCellphones() {
        return cellphones;
    }

    public void setCellphones(String[] cellphones) {
        this.cellphones = cellphones;
    }

    public String[] getEmails() {
        return emails;
    }

    public void setEmails(String[] emails) {
        this.emails = emails;
    }

    public String[] getIds() {
        return ids;
    }

    public void setIds(String[] ids) {
        this.ids = ids;
    }

    public String[] getKey_words() {
        return key_words;
    }

    public void setKey_words(String[] key_words) {
        this.key_words = key_words;
    }
}
