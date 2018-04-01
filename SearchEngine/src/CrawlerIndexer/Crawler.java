package CrawlerIndexer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;


public class Crawler extends Thread
{
        //The Database manager.
        private DbManager dbManager;

        private int numCrawledPages;
        private ArrayList<String> linksVisited;
        private ArrayList<String> DomainNameList;
        private ArrayList<String> linksToVisit;

        private RobotHandler robotHandler = new RobotHandler();

        //every crawler has an ID
        private int ID;

        public Crawler(ArrayList<String> linksToVisit, ArrayList<String> linksVisited, int ID) {
            dbManager = DbManager.getInstance();
            this.linksToVisit = linksToVisit;
            this.linksVisited = linksVisited;
            this.ID = ID;
        }

        public void setID(int ID){
            this.ID = ID;
        }

        public int getID(){
            return ID;
        }

        /*
        get the domainNames of linksToVisit
         */
        public void updateDomainNameList() throws MalformedURLException {
            //use a set to guarantee neglecting duplicates
            HashSet<String> hostNames = new HashSet<>();
            for(String hyperlink : linksToVisit) {
                //if a host was already in hostNames, it will not be inserted
                hostNames.add(new URL(hyperlink).getHost());
            }
            DomainNameList = new ArrayList<>(hostNames);
        }

        /*
        obeys robots.txt for every link in linksToVisit
         */
        public void respectWebsitePersonalSpace() {
            //synchronize to make sure threads work consistently
            synchronized (DomainNameList) {
                synchronized (linksToVisit) {
                    robotHandler.setAllowedLinks(DomainNameList, linksToVisit);
                }
            }
        }

        public boolean isCrawled(String URL) {
            //store the HTML code in this variable
            Document doc = null;
            try {
                System.out.println("Crawling " + URL);
                //Fetch the HTML
                doc = Jsoup.connect(URL).get();

                } catch (IOException e) {
                    System.out.println("invalid URL " + e.getMessage() + '\n');
                    return false;
                }
            //Insert this document into the database.
            dbManager.insertHtmlDoc(doc);
            try {
                //Parse the HTML to extract links to other URLs.
                Elements pageHyperlinks = doc.select("a[href]"); //throws NullPointerException
                for(Element link : pageHyperlinks){
                    //lock links to visit to prevent overwriting
                    synchronized (this){
                        if(!linksToVisit.contains(link.attr("abs:href"))) {
                            linksToVisit.add(link.attr("abs:href"));
                        }
                    }
                }
            }catch(NullPointerException e) {
                System.out.println("Error! something went wrong while fetching links from HTML doc\n");
                System.out.println(e.getMessage());
            }
            System.out.println('\n' + URL + " Crawled successfully\n");
            return true;
        }

        public int getNumCrawledPages() {
            return numCrawledPages;
        }

        /*
        //needs to be modified to obey robots.txt
        get URL from linksToVisitList, crawl, add hyperlinks to linksToVisit
        removes the visited link from linksToVisit, then add it to linksVisited.
         */
        @Override
        public void run() {
            System.out.println("\nCrawler #" + ID + " started\n");
            numCrawledPages = 0;
            String URL = null;
            for(int i = 0;i < 10; i++) {

                try {
                    updateDomainNameList();
                }catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                URL = linksToVisit.get(0);
                if(URL != null && !linksVisited.contains(URL)) {
                    if (isCrawled(URL)) {
                        numCrawledPages++;
                        synchronized (linksVisited) {
                            if(!linksVisited.contains(URL)) {
                                linksVisited.add(URL);
                            }
                        }
                        synchronized (linksToVisit) {
                            if(URL != null) {
                                linksToVisit.remove(URL);
                            }
                        }
                    }
                }

                //calls RobotHandler.setAllowedLinks to obey robots.txt of URL in linksToVisit.

            }
        }

}
