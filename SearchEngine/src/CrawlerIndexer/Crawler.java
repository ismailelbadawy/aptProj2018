package CrawlerIndexer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;

public class Crawler extends Thread
{
        private HashSet<String> linksToVisit;
        
        //The Database manager.
        private DbManager dbManager;

        //default constructor
        public Crawler(HashSet<String> linksToVisit) {
            dbManager = DbManager.getInstance();
            this.linksToVisit = linksToVisit;
        }

        //to be revised
        public synchronized void crawl(String URL, HashSet<String> linksVisited) {
            //store the HTML code in this variable
            Document doc = null;

            //first check if the page was already visited before
            if (!linksVisited.contains(URL)) {
                //if URL doesn't exists
                try {
                	System.out.println("Crawling " + URL.toString());
                    //add URL to list of links
                    linksVisited.add(URL);

                    //Fetch the HTML
                    doc = Jsoup.connect(URL).get();
                    
                    //Insert this document into the database.
                    dbManager.insertHtmlDoc(doc);

                } catch (IOException e) {
                    //System.out.println("please enter an HTTP URL\n");
                    e.getMessage();
                }
            } else {
                System.out.println("Page already visited");
            }
            
            try {
                //Parse the HTML to extract links to other URLs.
                Elements pageHyperlinks = doc.select("a[href]"); //throws NullPointerException

                for (Element link : pageHyperlinks) {

                    //check if link isn't already visited or added
                    if(!linksToVisit.contains(link.attr("abs:href")))
                    {
                        //add link to set of links to be visited
                        linksToVisit.add(link.attr("abs:href"));

                    }
                }
            }catch(NullPointerException e) {
                e.getMessage();
            }
        }

        public HashSet<String> getLinksToVisit() {
            return this.linksToVisit;
        }

        public boolean isLinkVisited(String URL) {
            if(linksToVisit.contains(URL)) {
                return false;
            }
            return true;
        }


        //function under construction
        @Override
        public void run() {

        }
}
