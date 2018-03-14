package CrawlerIndexer;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import CrawlerIndexer.Word;

import com.mongodb.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

public class DbManager {	
	
	//Static DbManager reference.
	public static DbManager single;
	//The client that connects to the database.
	MongoClient mongoClient;
	//The actual databse.
	MongoDatabase database;
	//Collection of words.
	MongoCollection<Document> words;
	//Collection of htmldocs.
	MongoCollection<Document> htmls;
	
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
		
		words = database.getCollection("words");
		htmls = database.getCollection("htmls");
		words.createIndex(Indexes.hashed("_id"));
	
	}
	
	public void addWord(Word wordToAdd) {
		Document document = new Document("text", wordToAdd.getText())
				.append("rank", wordToAdd.getRank());
		words.insertOne(document);
	}
	
	public ArrayList<Word> getAllWords(){
		ArrayList<Word> returnedWords = new ArrayList<>();
		Iterator iterator = words.find().iterator();
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
			htmls.deleteOne(document);
			actualDocuments.add((org.jsoup.nodes.Document)document.get("html"));
		}
		return actualDocuments;
	}
	
	public void insertHtmlDoc(org.jsoup.nodes.Document htmlDoc) {
		Document document = new Document("html", htmlDoc);
		htmls.insertOne(document);
	}
	
	
	
	public void closeConnection() {
		mongoClient.close();
	}
}
