package CrawlerIndexer;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.*;

import java.util.Arrays;

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
		IndexOptions indexOptions = new IndexOptions().unique(true);
		words.createIndex(Indexes.hashed("_id"), indexOptions);
	}
	
	public void addWord(Word wordToAdd) {
		Document document = new Document("text", wordToAdd.getText())
				.append("rank", wordToAdd.getRank());
	}
	
	
	public void closeConnection() {
		mongoClient.close();
	}
}
