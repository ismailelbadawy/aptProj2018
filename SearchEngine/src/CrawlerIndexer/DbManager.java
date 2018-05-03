package CrawlerIndexer;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.UncheckedIOException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import static com.mongodb.client.model.Filters.eq;

public class DbManager {	
	
	//Static DbManager reference.
	public static DbManager single;
	//The client that connects to the database.
	private MongoClient mongoClient;
	//The actual databse.
    private MongoDatabase database;
	//Collection of words.
	private MongoCollection<Document> wordCollection;
	//Collection of htmldocs.
	private MongoCollection<Document> htmls;
	//Collection for the output of the indexer, and input to the search query.
	private MongoCollection<Document> index;
	//Collection
	private MongoCollection<Document> linksToVisit;

	private MongoCollection<Document> crawledLinks;

	/**
	 * Tries to create a database object, new one is created only if no database objects exist.
	 * @return --The reference to the database manager.
	 */
	public static DbManager getInstance() {
		if(single == null) {
			single = new DbManager();
		}
		return single;
	}
	
	/**
	 * The constructor.
	 */
	private DbManager() {
		//Initiate connection.
		mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		
		//Get the database of words.
		database = mongoClient.getDatabase("searchEngine");
		
		wordCollection = database.getCollection("words");
		htmls = database.getCollection("htmls");
		index = database.getCollection("index");
		linksToVisit = database.getCollection("linksToVisit");
		IndexOptions options = new IndexOptions();
		htmls.createIndex(Indexes.text("url"), options.unique(true));
		wordCollection.createIndex(Indexes.text("word"));
		index.createIndex(Indexes.text("url"));
		linksToVisit.createIndex(Indexes.text("link"));
		crawledLinks = database.getCollection("getCrawledLinks");
	}
	
	public void addWord(Word wordToAdd) {
		Document document = new Document("text", wordToAdd.getText())
				.append("rank", wordToAdd.getTf());
		wordCollection.insertOne(document);
	}
	
	public ArrayList<Word> getAllWords(){
		ArrayList<Word> returnedWords = new ArrayList<>();
		Iterator iterator = wordCollection.find().iterator();
		while(iterator.hasNext()) {
			Document document = (Document)iterator.next();
			document.remove("_id");
			returnedWords.add(new Word((String)document.get("text")));
		}
		return returnedWords;
	}

	public Vector<String> getLinksVisited() {
		Vector<String> linksVisited = new Vector<>();
		Iterator iterator = crawledLinks.find().iterator();
		while(iterator.hasNext()) {
			Document document = (Document) iterator.next();
			linksVisited.add(document.getString("getCrawledLinks"));
		}
		return linksVisited;
	}

	public Vector<String> getCrawledLinks() {
		Vector<String> linksVisited = new Vector<>();
		Iterator iterator = htmls.find().iterator();
		while(iterator.hasNext()) {
			Document document = (Document) iterator.next();
			linksVisited.add(document.getString("url"));
		}
		return linksVisited;
	}

	public ArrayList<org.jsoup.nodes.Document> getHTMLDocs(int numberOfHtmlDocs){
		Iterator iterator = htmls.find().limit(numberOfHtmlDocs).iterator();
		if(iterator == null) {
			return null;
		}
		ArrayList<org.jsoup.nodes.Document> actualDocuments = new ArrayList<>();
		while(iterator.hasNext()) {
			Document document = (Document) iterator.next();
			document.remove("_id");
			String url = document.get("url").toString();
			org.jsoup.nodes.Document htmlDoc = null;
			try {
				htmlDoc = Jsoup.connect(url).get();
			}catch (UncheckedIOException ue){

			}catch (IOException io){

			}
			catch(Exception e){
				System.out.println("Cannot read this link.");
				return actualDocuments;
			}
			if(htmlDoc != null) {
				actualDocuments.add(htmlDoc);
				htmls.deleteOne(document);
			}
		}
		return actualDocuments;
	}
	
	public void insertHtmlDoc(org.jsoup.nodes.Document htmlDoc) {
		Document document = new Document("url", htmlDoc.baseUri());
		try {
			htmls.insertOne(document);
		}catch(Exception e) {
			System.out.println("An error occured while inserting" + e);
		}
	}

	public boolean insertLink(String url, ArrayList<String> words){
		ArrayList<BasicDBObject> dbWords = new ArrayList<>();
		for (String word: words) {
			BasicDBObject dbObject = new BasicDBObject();
			dbObject.put("word", word);
			dbObject.put("rank", 0);
			dbWords.add(dbObject);
		}
		Document document = new Document("url" , url);
		document.put("words", dbWords);
		try{
			index.insertOne(document);
			return true;
		}catch (Exception e){
			return false;
		}
	}

	public Long getTotalNumberOfDocuments(){
        return crawledLinks.count();
    }

	public void insertLinkIntoWords(String link, Vector<Word> words, String title, String description){
		for(Word word : words){
			Iterator iterator = wordCollection.find(new Document("word",word.getText())).iterator();
			if(!iterator.hasNext()){
				//Insert the word.
				ArrayList<BasicDBObject> links = new ArrayList<>();
				BasicDBObject linkDbObject = new BasicDBObject();
				linkDbObject.put("link", link);
				linkDbObject.put("title", title);
				linkDbObject.put("desc", description);
				linkDbObject.put("rank", word.getTf());
				links.add(linkDbObject);
				BasicDBObject dbObject = new BasicDBObject();
				dbObject.put("word", word.getText());
				dbObject.put("urls", links);
				wordCollection.insertOne(new Document(dbObject));
			}else{
				//Update the list of urls.
				Document dbObject = (Document) iterator.next();
				Document oldOBject = new Document(dbObject);
				//Extract the url.
				ArrayList<Document> urls = new ArrayList<>((ArrayList<Document>) dbObject.get("urls"));
				Document linkToAdd = new Document();
				linkToAdd.put("link", link);
				linkToAdd.put("title", title);
				linkToAdd.put("desc", description);
				linkToAdd.put("rank", word.getTf());
				if(!linkAddedToIndex(urls, link)){
					urls.add(linkToAdd);
				}else{
                    Document doc = urls.get(indexOfLink(urls, link));
                    doc.replace("rank", word.getTf());
                }
				dbObject.replace("urls",urls);
				try {
					UpdateResult up = wordCollection.updateOne(eq("word", oldOBject.get("word")), new Document("$set", dbObject));
				}catch (Exception e){
					System.out.println(e.getMessage());
				}
			}
		}
		crawledLinks.insertOne(new Document("link", link).append("rank", 0.0));
	}

	private boolean linkAddedToIndex(ArrayList<Document> vector, String url){
        for(Document dbObject : vector){
            if(dbObject.get("link").equals(url)){
                return true;
            }
        }
        return false;
    }

    private int indexOfLink(ArrayList<Document> vector, String url){
        for(Document dbObject : vector){
            if(dbObject.get("link").equals(url)){
                return vector.indexOf(dbObject);
            }
        }
        return -1;
    }

	public ArrayList<SearchResult> searchWords(String word){
		Iterator iterator = wordCollection.find(new Document("word", word)).iterator();
		ArrayList<SearchResult> words = new ArrayList<>();
		while(iterator.hasNext()){
			Document doc = (Document) iterator.next();
			ArrayList<Document> links = (ArrayList<Document>) doc.get("urls");
			for(Document link : links){
				words.add(new SearchResult((String)link.get("link"),"", 0.0, (String) link.get("title")));
			}
		}
		return words;
	}

	public void insertLinkToVisit(String link){
		try {
			linksToVisit.insertOne(new Document("link", link));
		} catch (Exception e){
			System.out.println(e.getMessage());
		}
	}

	public void insertCrawledLink(String link) {
		try {
			crawledLinks.insertOne(new Document("getCrawledLinks", link));
		} catch (Exception e){
			System.out.println(e.getMessage());
		}
	}

	public Vector<String> getLinksToVisit(){
		Iterator iterator = linksToVisit.find().limit(1000).iterator();
		Vector<String> returning = new Vector<>();
		while(iterator.hasNext()){
			Document doc = (Document) iterator.next();
			returning.add((String)doc.get("link"));
		}
		return returning;
	}

	public void removeLinkToVisit(String url){
		linksToVisit.deleteOne(new Document("link", url));
	}

	public void closeConnection() {
		mongoClient.close();
	}
}
