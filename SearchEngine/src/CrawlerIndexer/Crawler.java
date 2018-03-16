package CrawlerIndexer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Crawler implements Runnable
{
        private Set<String> links;
        private boolean state; //didn't use yet
        private static final int MAX_CRAWLED_PAGES = 10; //to be 5000
        private Set<String> linksToVisit;
        
        //The Database manager.
        private DbManager dbManager;

        //default constructor
        public Crawler()
        {
            links = new HashSet<String>();
            dbManager = DbManager.getInstance();
            linksToVisit = new HashSet<String>();
        }

        //to be revised
        public void crawl(String URL) {
            //store the HTML code in this variable
            Document doc = null;

            //first check if the page was already visited before
            if (!links.contains(URL)) {
                //if URL doesn't exists
                try {
                	System.out.println("Crawling " + URL.toString());
                    //add URL to list of links
                    links.add(URL);

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
                    if(!linksToVisit.contains(link.attr("abs:href")) && !links.contains(link.attr("abs:href")))
                    {
                        //add link to set of links to be visited
                        linksToVisit.add(link.attr("abs:href"));
                    }
                }
            }catch(NullPointerException e) {
                e.getMessage();
            }

        }

    //function under construction
    @Override
    public void run() {
        //code
    }
}
