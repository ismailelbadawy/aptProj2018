package CrawlerIndexer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class RobotHandler{
    //the start index of the disallowed route.
    private static final int DISALLOW_INDEX = "Disallow".length() + 1;

    /**
     *
     * @param url always a string: "http://" + hostName + "/robots.txt"
     * @return rules: ArrayList<String> if size = 0 => all hyperlinks are disallowed
     * obey any disallow in robots.txt
     */
    private Vector<String> getWebSiteRules(String url) {
        List<String> rules = new ArrayList<>();
        url += "/robots.txt";
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(new URL(url).openStream()));
            String line = null;
            //process each line in robots.txt till eof.
            while((line = in.readLine()) != null) {
                if(line.matches("Disallow(.*)")) {
                    if(line.substring(DISALLOW_INDEX).trim().equals("/")){
                        return new Vector<>();
                    }
                    //extract disallowed route
                    rules.add(line.substring(DISALLOW_INDEX).trim());
                }
            }
        }catch (Exception e) {
            return null;
        }
        return (Vector<String>) rules;
    }

    /**
     *takes hostName List, extract a hostName, checks for rules, remove disallowed links from linksToVisit
     * @param host the hostName of the webPage
     * @return Vector String array of disallowedLinks
     */
    public void getDisallowedLinks(Host host, Vector<String> linksNotToVisit) {
        Vector<String> rules;
        Vector<String> disallowedUrls = new Vector<>();

            rules = getWebSiteRules("http://" + host.getHostName());

            if(rules == null) {
                disallowedUrls = null;
                return;
            }

            else {
                for (int j = 0; j < rules.size(); j++) {
                    //replace the rule with a full disallowed url
                    disallowedUrls.add(j, "http://" + host + rules.get(j));
                }
            }

            for(int i = 0; i < disallowedUrls.size(); i++) {
                linksNotToVisit.add(disallowedUrls.get(i));
            }
    }
}
