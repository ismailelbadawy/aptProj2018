package CrawlerIndexer;

import java.net.MalformedURLException;
import java.net.URL;

/*
This class models the hostname
 */

public class Host {
    private int numVisits;
    private static final int MAX_VISITS = 500;
    private String hostName;
    private boolean isValidHostName;
    private static DbManager dbManager = DbManager.getInstance();

    public Host(String hostName) {
        isValidHostName = true;
        this.hostName = hostName;
    }

    public int getNumVisits() {
        return numVisits;
    }

    public boolean isHasValidHostName() {
        return isValidHostName;
    }

    public String getHostName() {
        return hostName;
    }

    public static int getMaxVisits() {
        return MAX_VISITS;
    }

    public void setNumVisits(int numVisits) {
        this.numVisits = numVisits;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void hasValidHostName() {
        try {
            URL url = new URL(this.hostName);
        }catch (MalformedURLException e) {
            isValidHostName = false;
        }
    }

    public boolean visitedEnough() {
        if(numVisits >= MAX_VISITS) {
            return true;
        }
        return false;
    }
}
