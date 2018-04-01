package CrawlerIndexer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RobotHandler {
    //HB = kawetch Hamada w Badawy kawetsh begadd la yo3la 3aleh
    private static final String USER_AGENT = "HB";
    //the start index of the disallowed query.
    private static final int DISALLOW_INDEX = "Disallow".length() + 1;

    /**
     *
     * @param url always a string: "http://" + hostName + "/robots.txt"
     * @return rules: ArrayList<String> if size = 0 => all hyperlinks are disallowed
     * obey any disallow in robots.txt
     */
    public ArrayList<String> getWebSiteRules(String url) {
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
                        return new ArrayList<String>();
                    }
                    //extract disallowed route
                    rules.add(line.substring(DISALLOW_INDEX).trim());
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return (ArrayList<String>) rules;
    }

    /**
     *takes hostName List, extract a hostName, checks for rules, remove disallowed links from linksToVisit
     * @param hostNames list of websites before obeying robots.txt
     * @param linksToVisit list of urls extracted from the list
     */
    public void setAllowedLinks(ArrayList<String> hostNames, ArrayList<String> linksToVisit) {
        List<String> rules;
        List<String> disallowedUrls = new ArrayList<String>();
        String hostName;
        for(int i = 0; i < hostNames.size(); i++) {

            hostName = hostNames.get(i);
            rules = getWebSiteRules("http://" + hostName);

            if(rules.size() == 0) {
                for(i = linksToVisit.size() -1; i >= 0; i--) {
                    if(linksToVisit.get(i).matches("http://" + hostName + "(.*)")) {
                        linksToVisit.remove(i);
                    }
                }
                linksToVisit.add("http://" + hostName);
                continue;
            }
            else {
                for (int j = 0; j < rules.size(); j++) {
                    //replace the rule with a full disallowed url
                    disallowedUrls.add(j, "http://" + hostName + rules.get(j));
                }
            }

            //remove disallowed links from linksToVisit
            for( String link : disallowedUrls) {
                if(linksToVisit.contains(link)) {
                    linksToVisit.remove(link);
                }
            }
        }
    }
}
