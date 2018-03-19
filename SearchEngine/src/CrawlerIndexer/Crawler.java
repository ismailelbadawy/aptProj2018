package CrawlerIndexer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;


public class Crawler extends Thread
{
        //The Database manager.
        private DbManager dbManager;

        private int numCrawledPages;
        private HashSet<String> linksVisited;
        private HashSet<String> linksToVisit;
        private Iterator<String> itr;

        //every crawler has an ID
        private int ID;

        public Crawler(HashSet<String> linksToVisit, HashSet<String> linksVisited, int ID) {
            dbManager = DbManager.getInstance();
            this.linksToVisit = linksToVisit;
            this.linksVisited = linksVisited;
            itr = linksToVisit.iterator();
            this.ID = ID;
        }

        public void setID(int ID){
            this.ID = ID;
        }

        public int getID(){
            return ID;
        }

        public boolean isCrawled(String URL) {
            //store the HTML code in this variable
            Document doc = null;
            try {
                System.out.println("Crawling " + URL);
                //Fetch the HTML
                doc = Jsoup.connect(URL).get();

                } catch (IOException e) {
                    System.out.println("invalid URL\n" + e.getMessage() + '\n');
                    return false;
                }
            //Insert this document into the database.
            dbManager.insertHtmlDoc(doc);
            try {
                //Parse the HTML to extract links to other URLs.
                Elements pageHyperlinks = doc.select("a[href]"); //throws NullPointerException
                for(Element link : pageHyperlinks){
                    //lock links to visit to prevent overwriting
                    synchronized (linksToVisit){
                        if(!linksToVisit.contains(link.toString())) {
                            linksToVisit.add(link.toString());
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


        //function under construction
        @Override
        public void run() {
            System.out.println("\nCrawler #" + ID + " started\n");
            numCrawledPages = 0;
            String URL = null;
            synchronized (itr) {
                if (itr.hasNext()) {
                    URL = itr.next();
                }
            }
            if(isCrawled(URL)){
                numCrawledPages++;
                synchronized (linksVisited){
                    linksVisited.add(URL);
                }
                synchronized (linksToVisit){
                    linksToVisit.remove(URL);
                }
            }
        }

}
