package CrawlerIndexer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/*
This class models the web page by it's link, currentScore
 */

public class WebPage {
    private double currentScore;
    private double previousScore;
    private int pageRank;
    private boolean isVisited;
    private boolean hasValidLink;
    private String link;
    private ArrayList<String> links;
    private static DbManager dbManager = DbManager.getInstance();

    public WebPage(String link) {
        this.hasValidLink = true;
        this.link = link;
        if(!hasValidUrl()) {
            this.link = null;
        }
        this.isVisited = false;
        this.currentScore = 0.0;
        this.previousScore = 0.0;
        this.links = null;
        this.pageRank = 0;
    }

    public WebPage(String link, int pageRank, double previousScore) {
        this.link = link;
        this.pageRank = pageRank;
        this.previousScore = previousScore;
    }

    public double getCurrentScore() {
        return currentScore;
    }

    public double getPreviousScore() {
        return previousScore;
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

    public int getPageRank() {
        return pageRank;
    }

    public ArrayList<String> getLinks() {
        return links;
    }

    public void setPageRank(int pageRank) {
        this.pageRank = pageRank;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setLinks(ArrayList<String> links) {
        this.links = links;
    }

    public void setCurrentScore(double currentScore) {
        this.currentScore = currentScore;
    }

    public void setPreviousScore(double previousScore) {
        this.previousScore = previousScore;
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
