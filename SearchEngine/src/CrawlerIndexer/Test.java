package CrawlerIndexer;

import java.util.ArrayList;

public class Test {
	
	public static void main(String[] args) {
		DbManager db = DbManager.getInstance();
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
		CrawlerThreadManager ctm = CrawlerThreadManager.getInstance(links,12);
		ctm.runCrawlerThreads();

		Indexer indexer = new Indexer();

		while(true) {
		    if(ctm.getNumberOfCrawledPages() >= 5000) {
		        break;
            }
        }


        ctm.killAllThreads();

	}
}
