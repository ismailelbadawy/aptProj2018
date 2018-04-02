package CrawlerIndexer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.net.URL;


public class Crawler extends Thread
{
        //The Database manager.
        private DbManager dbManager;

        private int numCrawledPages;
        private ArrayList<String> linksVisited;
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
        obeys robots.txt for every link in linksToVisit
         */
        public void respectWebsitePersonalSpace(String hostName) {
            //synchronize to make sure threads work consistently
            synchronized (linksToVisit) {
                robotHandler.setAllowedLinks(hostName, linksToVisit);
            }
        }

        public boolean isCrawled(String URL) {
            //store the HTML code in this variable
            Document doc = null;
            try {
                System.out.println("Crawling " + URL);
                //Fetch the HTML
                try {
                    doc = Jsoup.connect(URL).get();
                } catch (Exception iException) {
                    System.out.println("Cannot crawl this website.");
                    return false;
                }
            }catch (Exception e){
                System.out.println("Invalid url");
            }
            //Insert this document into the database.
            synchronized (dbManager) {
                dbManager.insertHtmlDoc(doc);
                dbManager.notifyAll();
            }
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
            for(int i = 0;i < 1000; i++) {

                synchronized (linksToVisit) {

                    URL = linksToVisit.remove(0);

                }

                if(URL != null && !linksVisited.contains(URL)) {
                    if (isCrawled(URL)) {
                        numCrawledPages++;
                        synchronized (linksVisited) {
                            if(!linksVisited.contains(URL)) {
                                linksVisited.add(URL);
                            }
                        }

                    }
                }
                URL url = null;
                try {
                    url = new URL(URL);
                } catch (MalformedURLException e) {
                    return;
                }
                //calls RobotHandler.setAllowedLinks to obey robots.txt of URL in linksToVisit.
                robotHandler.setAllowedLinks(url.getHost(),linksToVisit );
            }
        }

}
