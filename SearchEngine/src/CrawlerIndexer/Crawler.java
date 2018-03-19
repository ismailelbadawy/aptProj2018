package CrawlerIndexer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashSet;


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

        //to be revised
        public synchronized void crawl(String URL) {
            //store the HTML code in this variable
            Document doc = null;

            //first check if the page was already visited before
            if (!linksVisited.contains(URL)) {
                //if URL doesn't exists
                try {
                	System.out.println("Crawling " + URL.toString());
                    //Fetch the HTML
                    doc = Jsoup.connect(URL).get();
                    
                    //Insert this document into the database.
                    dbManager.insertHtmlDoc(doc);

                } catch (IOException e) {
                    //System.out.println("please enter an HTTP URL\n");
                    e.getMessage();
                }
                //add URL to list of links
                linksVisited.add(URL);
                if(linksToVisit.contains(URL)) {
                    linksToVisit.remove(URL);
                }
            } else {
                System.out.println("Page already visited");
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
                e.getMessage();
            }
        }


        //function under construction
        @Override
        public void run() {
            //
        }
}
