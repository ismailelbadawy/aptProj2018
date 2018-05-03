package CrawlerIndexer;

import org.jsoup.nodes.Document;
import org.tartarus.snowball.ext.PorterStemmer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

public class IndexerThread extends Thread{
	
	ArrayList<Document> htmldocs;
	private final int MAX_HTML_DOCS = 1;
	private Vector<String> commonEnglishWords;

	private boolean isRunning;

	private DbManager database;

	public IndexerThread() {
		//Insatntiate the arraylist.
		htmldocs = new ArrayList<>();
		database = DbManager.getInstance();
		commonEnglishWords = FileIO.readStopWords();
		isRunning = true;
	}
	
	@Override
	public void run(){
		runLoop:
		while(true){
			//Update the list of html urls to index.
			synchronized (database) {
				getHTMLs();
				//Break the loop if no html urls exist in the list.
				while (htmldocs.size() == 0) {
					try {
						database.wait();
						getHTMLs();
						if (!isRunning) {
							break runLoop;

						}
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
				Vector<Word> stemmed = stemList(words);
				System.out.println(words.size());
				String url = document.baseUri();
				String info = document.body().text();
				String description = "";
				if(info.length() > 270) {
					description= info.substring(0, 270) + "....";
				}else{
					description = info;
				}
				System.out.println("Description " + description);
				System.out.println(url + words.toString());
				insertPageIntoIndex(url, stemmed, title, description);
			}
			htmldocs.clear();
		}
		System.out.println("Done indexing.");
	}
	
	private void getHTMLs() {
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

	private void insertPageIntoIndex(String url, Vector<Word> words, String linkTitle, String description){
		synchronized (database) {
			database.insertLinkIntoWords(url, words, linkTitle, description);
		}
	}

	private Vector<Word> stemList(ArrayList<String> inputWords) {
		Vector<Word> vector = new Vector<>();
		for (int i = inputWords.size() - 1; i >= 0; i--) {
			String rawWord = inputWords.remove(i);
			PorterStemmer stemmer = new PorterStemmer();
			stemmer.setCurrent(rawWord);
			stemmer.stem();
			String stemmed = stemmer.getCurrent();
			if (!inputWords.contains(stemmed)) {
				Word wordToAdd = getWord(vector, stemmed);
				if(wordToAdd == null) {
					vector.add(new Word(stemmed));
				}else{
					wordToAdd.setTf(wordToAdd.getTf() + 1.0);
				}
				inputWords.add(stemmed);
			}else{
				Word wordObject = getWord(vector, stemmed);
				if(wordObject == null){
					wordObject = new Word(stemmed);
					Word wordToAdd = getWord(vector, stemmed);
					if(wordToAdd == null) {
						vector.add(wordObject);
					}else{
						wordToAdd.setTf(wordToAdd.getTf() + 1.0);
					}
				}else{
					wordObject.setTf(wordObject.getTf() + 1.0);
				}
			}

		}
		finalizeTfIDF(vector);
		return vector;
	}

	public Word getWord(Vector<Word> words,String word){
		for(Word w : words){
			if(w.getText().equals(word)){
				return w;
			}
		}
		return null;
	}

	public void finalizeTfIDF(Vector<Word> vector){
		for(Word word : vector){
			word.setTf(word.getTf() / vector.size());
		}
	}

	public void exit(){
		isRunning = false;

	}
}
