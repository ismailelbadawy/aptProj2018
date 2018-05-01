package CrawlerIndexer;

import java.util.HashMap;
import java.util.Map;

public class SearchResult {

    private String link;
    private String description;
    private Integer rank;
    private String title;

    /**
     *
     * @param title
     * @param rank
     * @param description
     * @param link
     */
    public SearchResult(String link, String description, Integer rank, String title) {
        this.link = link;
        this.description = description;
        this.rank = rank;
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}