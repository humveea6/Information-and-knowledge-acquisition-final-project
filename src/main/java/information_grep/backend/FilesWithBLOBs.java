package information_grep.backend;

public class FilesWithBLOBs extends Files {
    private String title;

    private String cleanedWords;

    private String totalWords;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getCleanedWords() {
        return cleanedWords;
    }

    public void setCleanedWords(String cleanedWords) {
        this.cleanedWords = cleanedWords == null ? null : cleanedWords.trim();
    }

    public String getTotalWords() {
        return totalWords;
    }

    public void setTotalWords(String totalWords) {
        this.totalWords = totalWords == null ? null : totalWords.trim();
    }
}