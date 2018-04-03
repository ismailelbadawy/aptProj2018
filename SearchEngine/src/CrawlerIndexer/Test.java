package CrawlerIndexer;

import java.util.ArrayList;
import java.util.Scanner;

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

            Scanner sc = new Scanner(System.in);
		    sc.next();
		    System.out.println("Number of crawled pages till now: " + ctm.getNumberOfCrawledPages());
        }

        ctm.killAllThreads();
		indexer.stopAllThreads();
	}
}
