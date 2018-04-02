package CrawlerIndexer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.tartarus.snowball.ext.PorterStemmer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class IndexerThread extends Thread{
	
	ArrayList<Document> htmldocs;
	private final int MAX_HTML_DOCS = 1;
	private ArrayList<String> commonEnglishWords;

	
	private DbManager database;

	public IndexerThread() {
		//Insatntiate the arraylist.
		htmldocs = new ArrayList<>();
		database = DbManager.getInstance();
		commonEnglishWords = FileIO.readStopWords();
	}
	
	@Override
	public void run(){
		while(true){
			//Update the list of html urls to index.
			synchronized (database) {
				getHTMLs();
				//Break the loop if no html urls exist in the list.
				while (htmldocs.size() == 0) {
					try {
						database.wait();
						getHTMLs();
					} catch (Exception e) {
						System.out.println("Exception : " + e.getMessage());
					}
				}
			}
			//For each document retrieved by the indexer.
			for(Document document : htmldocs) {
				// Get the whole text into string.
				String text = document.body().text();
				//Get the page title.
				String title = document.title();
				//Extract the words in a word array.
				String[] wordsArray = text.split(" ");
				ArrayList<String> words = new ArrayList<>(Arrays.asList(wordsArray));
				System.out.println(words.size());
				//Remove the stop words from the list.
				for(String commonWord : commonEnglishWords){
					words.removeAll(Collections.singleton(commonWord));
				}
				//Remove the non-english words from the list.
				removeNonEnglish(words);
				stemList(words);
				System.out.println(words.size());
				String url = document.baseUri();
				System.out.println(url + words.toString());
				insertPageIntoIndex(url, words, title);
			}
			htmldocs.clear();
		}

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

	private void insertPageIntoIndex(String url, ArrayList<String> words, String linkTitle){
		synchronized (database) {
			database.insertLinkIntoWords(url, words, linkTitle);
		}
	}

	private synchronized ArrayList<String> stemList(ArrayList<String> inputWords){
		for(int i = inputWords.size() - 1; i >= 0; i--){
			String rawWord = inputWords.remove(i);
			PorterStemmer stemmer = new PorterStemmer();
			stemmer.setCurrent(rawWord);
			stemmer.stem();
			String stemmed = stemmer.getCurrent();
			if(!inputWords.contains(stemmed)){
				inputWords.add(stemmed);
			}
		}
		return inputWords;
	}
}
