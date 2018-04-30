package CrawlerIndexer;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.UncheckedIOException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.ArrayList;


public class Crawler extends Thread
{
        //The Database manager.
        private DbManager dbManager;

        private int numCrawledPages;
        private ArrayList<String> linksVisited;
        private ArrayList<String> linksToVisit;

        private ArrayList<Host> hostNames;

        private boolean isRunning;

        private RobotHandler robotHandler = new RobotHandler();

        //every crawler has an ID
        private int ID;

        public Crawler(ArrayList<String> linksToVisit, ArrayList<String> linksVisited, int ID) {
            dbManager = DbManager.getInstance();
            this.linksToVisit = linksToVisit;
            this.linksVisited = linksVisited;
            this.ID = ID;
            isRunning = true;
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
            Connection connection;

            System.out.println("Crawling " + URL);
            //Fetch the HTML
            try {
                connection = Jsoup.connect(URL);

            } catch (Exception iException) {
                System.out.println("Cannot crawl this website.");
                return false;
            }
            try {
                doc = connection.get();
            } catch (UncheckedIOException exception){
                return false;
            }catch (SocketTimeoutException se) {
                return false;
            }
            catch (Exception e){
                return false;
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
                            synchronized (dbManager) {
                                dbManager.insertLinkToVisit(link.attr("abs:href"));
                            }
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
        }

        public void exit() {
            isRunning = false;
        }


}
