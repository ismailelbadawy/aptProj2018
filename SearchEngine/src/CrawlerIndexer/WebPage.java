package CrawlerIndexer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

/*
This class models the web page by it's link, currentScore
 */

public class WebPage {
    //current pageRank popularity score
    private double currentScore;
    //previous pageRank popularity score
    private double previousScore;
    private boolean isVisited;
    private boolean hasValidLink;
    private String link;
    private Vector<String> links;
    //private static DbManager dbManager = DbManager.getInstance();

    WebPage(String link) {
        this.hasValidLink = true;
        this.link = link;
        if(!hasValidUrl()) {
            this.link = null;
        }
        this.isVisited = false;
        this.currentScore = 0.0;
        this.previousScore = 0.0;
        this.links = new Vector<>();
    }

    WebPage(String link, double previousScore) {
        this.link = link;
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

    public boolean hasValidLink() {
        return hasValidLink;
    }

    public void addLink(String link) {
        links.add(link);
    }


    public Vector<String> getLinks() {
        return links;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }

    public void setLinks(Vector<String> links) {
        this.links = links;
    }

    public void setCurrentScore(double currentScore) {
        this.currentScore = currentScore;
    }

    public void setPreviousScore(double previousScore) {
        this.previousScore = previousScore;
    }

    public String getHostName() {
       URL url;
        try {
            url = new URL(link);
        }catch(MalformedURLException e) {
            this.hasValidLink = false;
            return null;
        }
        return url.getHost();
    }

    public Host getHost() {
        return new Host(getHostName());
    }



    private boolean hasValidUrl() {
        try {
            new URL(link);
        }catch(MalformedURLException e) {
            this.hasValidLink = false;
            return false;
        }
        return true;
    }
}
