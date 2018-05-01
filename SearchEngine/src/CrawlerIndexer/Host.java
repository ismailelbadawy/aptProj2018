package CrawlerIndexer;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/*
This class models the hostname
 */

public class Host {
    private int numVisits;
    private static final int MAX_VISITS = 250;
    private String hostName;
    private boolean isValidHostName;
    private boolean isObeyed;
    private static DbManager dbManager = DbManager.getInstance();

    public Host(String hostName) {
        isValidHostName = true;
        this.hostName = hostName;
        if(!hasValidHostName()) {
            isValidHostName = false;
        }
        this.numVisits = 0;
        this.isObeyed = false;
    }

    public int getNumVisits() {
        return numVisits;
    }

    public String getHostName() {
        return hostName;
    }

    public boolean isValidHostName() {
        return isValidHostName;
    }



    public void setObeyed(boolean obeyed) {
        isObeyed = obeyed;
    }

    public String getNormalizedURL() {
        String normalizedUrl;
        try {
            URI uri = new URI(hostName);
            normalizedUrl = uri.normalize().toString();
        }catch (URISyntaxException e) {
            normalizedUrl =  null;
        }
        return normalizedUrl;
    }

    public boolean isObeyed() {
        return isObeyed;
    }

    public static int getMaxVisits() {
        return MAX_VISITS;
    }

    public void setNumVisits(int numVisits) {
        this.numVisits = numVisits;
    }

    public void incrementNumVisit() {
        this.numVisits++;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public boolean hasValidHostName() {
        try {
            URL url = new URL(this.hostName);
        }catch (MalformedURLException e) {
            return false;
        }
        return true;
    }

    public boolean visitedEnough() {
        if(numVisits >= MAX_VISITS) {
            return true;
        }
        return false;
    }
}
