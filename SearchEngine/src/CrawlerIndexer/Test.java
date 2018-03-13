package CrawlerIndexer;

import java.util.Scanner;

public class Test {
	
	public static void main(String[] args) {
		DbManager db = DbManager.getInstance();
		
		while(true) {
			Scanner sc = new Scanner(System.in);
			db.addWord(new Word(sc.nextLine()));
		}
	}
}
