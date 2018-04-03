package CrawlerIndexer;

import java.util.ArrayList;

public class Test {
	
	public static void main(String[] args) {
		DbManager db = DbManager.getInstance();
		ArrayList<String> links = FileIO.getStartingLinks();
		if(links == null){
			System.out.println("Error in file.");
			return;
		}
		CrawlerThreadManager ctm = CrawlerThreadManager.getInstance(links,20);
		ctm.runCrawlerThreads();

		Indexer indexer = new Indexer();

		while(true) {
		    if(ctm.getNumberOfCrawledPages() >= 5000) {
		    	break;
            }
        }

        ctm.killAllThreads();
		indexer.stopAllThreads();
	}
}
