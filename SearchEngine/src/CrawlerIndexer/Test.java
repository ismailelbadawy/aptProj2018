package CrawlerIndexer;

import java.util.Scanner;
import java.util.Vector;

public class Test {
	
	public static void main(String[] args) {
		DbManager db = DbManager.getInstance();
		Vector<String> links = db.getLinksToVisit();
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
		CrawlerThreadManager ctm = CrawlerThreadManager.getInstance(links,7);
		ctm.runCrawlerThreads();

		//Indexer indexer = new Indexer();

		while(true) {
		    if(ctm.getNumberOfCrawledPages() >= 25) {
		        break;
            }
            Scanner sc = new Scanner(System.in);
		    sc.next();
		    System.out.println("Number of crawled pages till now: " + ctm.getNumberOfCrawledPages());
        }
        ctm.killAllThreads();

		Vector<WebPage> pages = ctm.getWebPages();

		PageRanker pageRanker = PageRanker.getInstance();
		pageRanker.setWebPages(pages);
		pageRanker.calculatePopularity(100);
		for(WebPage p : pages) {
			p.printPagesPopularity();
		}
	}
}
