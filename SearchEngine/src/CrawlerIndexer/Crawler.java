package CrawlerIndexer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Crawler
{
        private Set<String> links;
        private boolean state; //didn't use yet
        private static final int MAX_CRAWLED_PAGES = 10; //to be 5000

        public Crawler()
        {
            links = new HashSet<String>();
        }

        //function under construction
        public void crawl(String URL, int depth)
        {
            //store the HTML code in this variable
            Document doc = null;

            //first check if the page was already visited before
            if(!links.contains(URL))
            {
                //if URL doesn't exists
                try {

                    //add URL to list of links
                    links.add(URL);

                    //Fetch the HTML
                    doc = Jsoup.connect(URL).get();

                }catch(IOException e){
                    //System.out.println("please enter an HTTP URL\n");
                    e.getMessage();
                }
            }
            else
            {
                System.out.println("Page already visited");
            }

            //Fetch hyperlinks from HTML document
            Elements pageHyperlinks = doc.select("a[href]");

        }
}
