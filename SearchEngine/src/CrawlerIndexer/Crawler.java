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

        private HashSet<String> linksVisited;
        private HashSet<String> linksToVisit;

        public Crawler(HashSet<String> linksToVisit, HashSet<String> linksVisited) {
            dbManager = DbManager.getInstance();
            this.linksToVisit = linksToVisit;
            this.linksVisited = linksVisited;
        }

        public synchronized boolean crawl(String URL) {
            //store the HTML code in this variable
            Document doc = null;
            try {
                System.out.println("Crawling " + URL.toString());
                //Fetch the HTML
                doc = Jsoup.connect(URL).get();

                //Insert this document into the database.
                dbManager.insertHtmlDoc(doc);
                } catch (IOException e) {
                    e.getMessage();
                    System.out.println("invalid URL\n");
                    return false;
                }
            
            try {
                //Parse the HTML to extract links to other URLs.
                Elements pageHyperlinks = doc.select("a[href]"); //throws NullPointerException
                for(Element link : pageHyperlinks){
                    if(!linksToVisit.contains(link.toString())) {
                        linksToVisit.add(link.toString());
                    }
                }
            }catch(NullPointerException e) {
                System.out.println("Error! something went wrong while fetching links from HTML doc\n");
                e.getMessage();
            }
            return true;
        }


        //function under construction
        @Override
        public void run() {
            Iterator<String> itr = linksToVisit.iterator();
            int crawledPages = 0;
            String URL = null;
            while(crawledPages <= 16){
                if(itr.hasNext()) {
                   URL = itr.next();
                   if(crawl(URL)){
                       crawledPages += 4;
                       linksVisited.add(URL);
                       linksToVisit.remove(URL);
                   }

                }
            }
        }
}
