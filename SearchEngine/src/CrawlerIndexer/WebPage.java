package CrawlerIndexer;

import java.net.MalformedURLException;
import java.net.URL;

/*
This class models the web page by it's link, pageRank
 */

public class WebPage {
    private double pageRank;
    private boolean isVisited;
    private boolean hasValidLink;
    private String link;
    private static DbManager dbManager = DbManager.getInstance();

    public WebPage(String link) {
        this.hasValidLink = true;
        this.link = link;
        if(!hasValidUrl()) {
            this.link = null;
        }
        this.isVisited = false;
        this.pageRank = 0.0;
    }

    public double getPageRank() {
        return pageRank;
    }

    public String getLink() {
        return link;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public boolean isHasValidLink() {
        return hasValidLink;
    }

    public String getHost() {
       URL url = null;
        try {
            url = new URL(link);
        }catch(MalformedURLException e) {
            this.hasValidLink = false;
            return null;
        }
        return url.getHost();
    }

    public boolean hasValidUrl() {
        try {
            URL url = new URL(link);
        }catch(MalformedURLException e) {
            this.hasValidLink = false;
            return false;
        }
        return true;
    }
}
