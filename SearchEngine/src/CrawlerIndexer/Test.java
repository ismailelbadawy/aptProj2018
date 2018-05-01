package CrawlerIndexer;

import java.util.Scanner;
import java.util.Vector;

public class Test {
	
	public static void main(String[] args) {
		DbManager db = DbManager.getInstance();

		/*String normalizedUrl;
		for(int i = 0; i < 100; i += 4) {
			try {
				URI uri = new URI(links.get(i));
				normalizedUrl = uri.normalize().toString();
			}catch (URISyntaxException e) {
				continue;
			}
			System.out.println(links.get(i));
			System.out.println(normalizedUrl);
		}*/
		/*if(links == null){
            links = FileIO.getStartingLinks();
			System.out.println("Error in file.");
		}
		else {
		    if(links.size() == 0) {
		        //start from the beginning
                links = FileIO.getStartingLinks();
            }
        }*/
		Vector<String> links = FileIO.getStartingLinks();
		CrawlerThreadManager ctm = CrawlerThreadManager.getInstance(links,10);
		ctm.runCrawlerThreads();

		//Indexer indexer = new Indexer();

		while(true) {
		    if(ctm.getNumberOfCrawledPages() >= 5000) {
		        break;
            }
            Scanner sc = new Scanner(System.in);
		    sc.next();
		    System.out.println("Number of crawled pages till now: " + ctm.getNumberOfCrawledPages());
		    //
        }

        ctm.killAllThreads();

	}
}
