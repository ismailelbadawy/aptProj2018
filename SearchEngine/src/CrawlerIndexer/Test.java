package CrawlerIndexer;

import java.util.ArrayList;

public class Test {
	
	public static void main(String[] args) {

		ArrayList<String> links = FileIO.getStartingLinks();
		if(links == null){
			System.out.println("Error in file.");
			return;
		}
		CrawlerThreadManager ctm = CrawlerThreadManager.getInstance(links,12);
		ctm.runCrawlerThreads();

		new Indexer();

		//wait for all crawlers to finish to print the final linksToVisit List
		for(Crawler cwl : ctm.getCrawlerList()){
			try {
				cwl.join();
			}catch(InterruptedException e){
				System.out.println(e.getMessage());
			}
		}

		ctm.printLinksVisited();

		/*DbManager db = DbManager.getInstance();

		while(true) {
			System.out.print("Enter a word:");
			Scanner sc = new Scanner(System.in);
			String input = sc.nextLine();
			if(input.equalsIgnoreCase("get")) {
				break;
			}
			db.addWord(new Word(input));
		}
		ArrayList<Document> htmlsInDb = db.getHTMLDocs(10);
		if(htmlsInDb == null) {
			System.out.println("No items in database.");
		}
		System.out.println(htmlsInDb.size());
		for(Document word : htmlsInDb) {
			System.out.println(word.toString());
		}*/
		//new IndexerThread().start();
	}
}
