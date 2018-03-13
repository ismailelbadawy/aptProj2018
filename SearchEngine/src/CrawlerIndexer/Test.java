package CrawlerIndexer;

import java.util.ArrayList;
import java.util.Scanner;
import org.jsoup.nodes.Document;

public class Test {
	
	public static void main(String[] args) {
		DbManager db = DbManager.getInstance();
		
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
	}
}
