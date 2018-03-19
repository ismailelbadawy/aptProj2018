package CrawlerIndexer;

import java.util.HashSet;

public class Test {
	
	public static void main(String[] args) {

		CrawlerThreadManager ctm = CrawlerThreadManager.getInstance(3);
		HashSet<String> links = new HashSet<>();
		links.add("http://www.google.com");
		ctm.setLinksToVisit(links);
		ctm.runCrawlerThreads();

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
		}
		ArrayList<String> words = new ArrayList<>();
		words.add("ANa");
		words.add("Som3a");
		String url = "http://www.ismail.com.eg";
		DbManager db = DbManager.getInstance();
		db.insertLinkIntoWords(url, words);
		*/
	}
}
