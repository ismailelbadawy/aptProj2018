package CrawlerIndexer;

import java.util.ArrayList;
import java.util.Scanner;

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
		ArrayList<Word> wordsInDB = db.getAllWords();
		System.out.println(wordsInDB.size());
		for(Word word : wordsInDB) {
			System.out.println(word.getText());
		}
	}
}
