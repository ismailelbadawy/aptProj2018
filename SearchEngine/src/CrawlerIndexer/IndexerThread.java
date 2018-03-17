package CrawlerIndexer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.mongodb.BasicDBObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class IndexerThread extends Thread{
	
	ArrayList<Document> htmldocs;
	private final int MAX_HTML_DOCS = 4;
	private final String[] commonEnglishWords = { "is", "this", "the", " " };

	
	private DbManager database;

	public IndexerThread() {
		//Insatntiate the arraylist.
		htmldocs = new ArrayList<>();
		database = DbManager.getInstance();
	}
	
	@Override
	public void run() {
		int i = 0;
		while(true){
			getHTMLs();
			if(htmldocs.size() == 0){
				break;
			}
			for(Document document : htmldocs) {
				// Get the whole text into string.
				String text = document.body().text();
				//Extract the words in a word array.
				String[] wordsArray = text.split(" ");
				ArrayList<String> words = new ArrayList<>(Arrays.asList(wordsArray));
				System.out.println(words.size());
				for(String commonWord : commonEnglishWords){
					words.removeAll(Collections.singleton(commonWord));
				}
				removeNonEnglish(words);
				System.out.println(words.size());
				String url = document.baseUri();
				System.out.println(url + words.toString());
				insertPageIntoIndex(url, words);
			}
			htmldocs.clear();
		}
		System.out.println("No more htmls to index.");
	}
	
	private synchronized void getHTMLs() {
		if(htmldocs.isEmpty()) {
			htmldocs = database.getHTMLDocs(MAX_HTML_DOCS);
		}
		if(htmldocs.size() != 0)
			System.out.println(htmldocs.get(0).text());
	}

	private synchronized void removeNonEnglish(ArrayList<String> words){
		for(int i = words.size() - 1; i >= 0; i--){
			String word = words.remove(i);
			String newWord = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
			if(!newWord.equals("")){
				words.add(newWord);
			}
		}
	}

	private synchronized void insertPageIntoIndex(String url, ArrayList<String> words){
		ArrayList<BasicDBObject> dbWords = new ArrayList<>();
		for (String word: words) {
			BasicDBObject dbObject = new BasicDBObject();
			dbObject.put("word", word);
			dbObject.put("rank", 0);
			dbWords.add(dbObject);
		}
		if(database.insertLink(url, dbWords)){
			System.out.println("Inserted an entry to the index successfully.");
		}else{
			System.out.println("Something went wrong while inserting into the index.");
		}
	}
}
