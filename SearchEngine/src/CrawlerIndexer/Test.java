package CrawlerIndexer;

import org.tartarus.snowball.ext.PorterStemmer;

import java.util.ArrayList;
import java.util.Scanner;

public class Test {
	
	public static void main(String[] args) {
		/*DbManager db = DbManager.getInstance();
		ArrayList<String> links = db.getLinksToVisit();
		if(links == null){
            links = FileIO.getStartingLinks();
			System.out.println("Error in file.");
		}
		else {
		    if(links.size() == 0) {
		        //start from the beginning
                links = FileIO.getStartingLinks();
            }
        }
		CrawlerThreadManager ctm = CrawlerThreadManager.getInstance(links,20);
		ctm.runCrawlerThreads();

		Indexer indexer = new Indexer();


		while(true) {
		    if(ctm.getNumberOfCrawledPages() >= 5000) {
		        break;
            }
            Scanner sc = new Scanner(System.in);
		    sc.next();
		    System.out.println("Number of crawled pages till now: " + ctm.getNumberOfCrawledPages());
        }


        ctm.killAllThreads();

	}*/
	ArrayList<SearchResult> searchResults	= new QueryProcessor().search("first method us amazing");
	}

}
