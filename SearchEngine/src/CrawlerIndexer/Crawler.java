package CrawlerIndexer;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.UncheckedIOException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Vector;


public class Crawler extends Thread
{
        //The Database manager.
        private DbManager dbManager;

        private int numCrawledPages;
        private Vector<String> linksVisited;
        private Vector<String> linksToVisit;
        private Vector<String> linksNotToVisit;
        private Vector<WebPage> webPages;

        private int numThreads;

        private Vector<Host> hostNames;

        private boolean isRunning;

        private RobotHandler robotHandler = new RobotHandler();

        //every crawler has an ID
        private int ID;

        Crawler(Vector<String> linksToVisit, Vector<String> linksVisited,
                       Vector<Host> hostNames, Vector<String> linksNotToVisit,
                        Vector<WebPage> webPages, int numThreads, int ID) {
            dbManager = DbManager.getInstance();
            this.linksToVisit = linksToVisit;
            this.linksVisited = linksVisited;
            this.hostNames = hostNames;
            this.linksNotToVisit = linksNotToVisit;
            this.ID = ID;
            this.isRunning = false;
            this.numThreads = numThreads;
            this.webPages = webPages;
            numCrawledPages = 0;
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

        public boolean isCrawled(String URL) {
            //store the HTML code in this variable
            Document doc;
            Connection connection;

            System.out.println("Crawler # " + ID + " Crawling " + URL);
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
                WebPage webPage = new WebPage(URL);
                webPage.setVisited(true);
                for(Element link : pageHyperlinks){
                    String link1 = link.attr("abs:href");
                    webPage.addLink(link.attr("abs:href"));
                    //lock links to visit to prevent overwriting
                        if(!linksToVisit.contains(link.attr("abs:href"))) {
                            linksToVisit.add(link.attr("abs:href"));
                            synchronized (dbManager) {
                                dbManager.insertLinkToVisit(link.attr("abs:href"));
                            }
                        }
                    }
                webPages.add(webPage);
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
        get disallowed list of links starting from starterLinks.
        get URL from linksToVisitList, crawl, add hyperlinks to linksToVisit
        removes the visited link from linksToVisit, then add it to linksVisited.
         */
        @Override
        public void run() {
            boolean firstLoop = true;
            isRunning = true;
            int hostVisitStatusCode;
            String link;
            System.out.println("\nCrawler #" + ID + " started\n");
            int myCollectionStarterIndex = (hostNames.size() / numThreads) * (ID - 1);
            while(true) {
                if(!isRunning) {
                    return;
                }
                if(firstLoop) {
                    for (int i = myCollectionStarterIndex; i < hostNames.size(); i++) {
                        if (!hostNames.get(i).isObeyed()) {
                            robotHandler.getDisallowedLinks(hostNames.get(i), linksNotToVisit);
                            hostNames.get(i).setObeyed(true);
                        }
                    }
                }
                else {
                    for(int i = 0; i < hostNames.size(); i++) {
                        if(!hostNames.get(i).isObeyed()) {
                            robotHandler.getDisallowedLinks(hostNames.get(i), linksNotToVisit);
                            hostNames.get(i).setObeyed(true);
                        }
                    }
                }

                for(int i = 0; i < linksToVisit.size(); i++) {
                    if(!isRunning) {
                        return;
                    }
                    if(!linksNotToVisit.contains(linksToVisit.get(i))
                            && !linksVisited.contains(linksToVisit.get(i))) {
                        link = linksToVisit.get(i);
                        linksToVisit.remove(i);
                        hostVisitStatusCode = hostVisitedEnough(link);
                        if (hostVisitStatusCode == 0) {
                            if (isCrawled(link)) {
                                numCrawledPages++;
                                linksVisited.add(linksToVisit.get(i));
                                    try {
                                        String hostName = new URL(link).getHost();
                                        for (int k = 0; k < hostNames.size(); k++) {
                                            if (hostNames.get(k).getHostName() == hostName) {
                                                hostNames.get(k).incrementNumVisit();
                                                break;
                                            }
                                            if (k == hostNames.size() - 1) {
                                                hostNames.add(new Host(hostName));
                                            }
                                        }
                                    } catch (MalformedURLException e) {
                                        System.out.println(e.getMessage());
                                    }
                            }

                        }
                    }
                }

                firstLoop = false;
            }
        }

        public void exit() {
            isRunning = false;
        }

        private int hostVisitedEnough(String link) {
            synchronized (hostNames) {
                try {
                    URL url = new URL(link);
                    String hostName = url.getHost();
                    Host host = new Host(hostName);
                    for (int i = 0; i < hostNames.size(); i++) {
                        if (hostNames.get(i).getHostName() == host.getHostName()) {
                            if (hostNames.get(i).visitedEnough()) {
                                return 1; // host is visited enough
                            }
                        }
                    }
                } catch (MalformedURLException e) {
                    return -1; // invalid url
                }
                return 0; // host is not visited enough
            }
        }

}
