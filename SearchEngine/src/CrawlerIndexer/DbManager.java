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

import java.util.ArrayList;
import java.util.Iterator;

import static com.mongodb.client.model.Filters.eq;

public class DbManager {	
	
	//Static DbManager reference.
	public static DbManager single;
	//The client that connects to the database.
	MongoClient mongoClient;
	//The actual databse.
	MongoDatabase database;
	//Collection of words.
	MongoCollection<Document> wordCollection;
	//Collection of htmldocs.
	MongoCollection<Document> htmls;
	//Collection for the output of the indexer, and input to the search query.
	MongoCollection<Document> index;
	
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
		IndexOptions options = new IndexOptions().unique(false);
		htmls.createIndex(Indexes.text("url"), options);
		wordCollection.createIndex(Indexes.hashed("_id"));
		index.createIndex(Indexes.text("url"));
	}
	
	public void addWord(Word wordToAdd) {
		Document document = new Document("text", wordToAdd.getText())
				.append("rank", wordToAdd.getRank());
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
			org.jsoup.nodes.Document htmlDoc;
			try {
				htmlDoc = Jsoup.connect(url).get();
			}catch(Exception e){
				System.out.println("An error occured lel asaf.");
				return actualDocuments;
			}
			actualDocuments.add(htmlDoc);
			htmls.deleteOne(document);
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

	public void insertLinkIntoWords(String link, ArrayList<String> words){
		for(String word : words){
			Iterator iterator = wordCollection.find(new Document("word",word)).iterator();
			if(!iterator.hasNext()){
				//Insert the word.
				ArrayList<String> urls = new ArrayList<>();
				urls.add(link);
				BasicDBObject dbObject = new BasicDBObject();
				dbObject.put("word",word);
				dbObject.put("urls",urls);
				wordCollection.insertOne(new Document(dbObject));
			}else{
				//Update the list of urls.
				Document dbObject = (Document) iterator.next();
				Document oldOBject = new Document(dbObject);
				ArrayList<String> urls = new ArrayList<>((ArrayList<String>) dbObject.get("urls")) ;
				urls.add(link);
				dbObject.replace("urls",urls);
				UpdateResult up = wordCollection.updateOne(eq(""), dbObject);
				System.out.println(up.toString());
			}
		}
	}
	
	
	public void closeConnection() {
		mongoClient.close();
	}
}
