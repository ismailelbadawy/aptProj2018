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

public class DbManager {	
	
	public static DbManager single;
	
	
	MongoClient mongoClient;
	//The actual databse.
	MongoDatabase database;
	
	MongoCollection<Document> words;
	
	public static DbManager getInstance() {
		if(single == null) {
			single = new DbManager();
		}
		return single;
	}
	
	private DbManager() {
		//Initiate connection.
		mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		
		database = mongoClient.getDatabase("searchEngine");
		words = database.getCollection("words");
		words.createIndex(Indexes.hashed("_id"));
	}
	
	public void addWord(Word wordToAdd) {
		Document document = new Document("text", wordToAdd.getText())
				.append("rank", wordToAdd.getRank());
		words.insertOne(document);
	}
	
	public ArrayList<Word> getAllWords(){
		FindIterable<Document> allWords = words.find();
		ArrayList<Word> returnedWords = new ArrayList<>();
		Iterator iterator = allWords.iterator();
		while(iterator.hasNext()) {
			Document document = (Document)iterator.next();
			document.remove("_id");
			returnedWords.add(new Word((String)document.get("text")));
		}
		return returnedWords;
	}
	
	
	
	
	public void closeConnection() {
		mongoClient.close();
	}
}
