package CrawlerIndexer;

import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class IndexerThread extends Thread{
	
	ArrayList<Document> htmldocs;
	private final int MAX_HTML_DOCS = 1;
	
	private DbManager database;
	
	public IndexerThread() {
		//Insatntiate the arraylist.
		htmldocs = new ArrayList<>();
		database = DbManager.getInstance();
	}
	
	@Override
	public void run() {
		getHTMLs();
	}
	
	private synchronized void getHTMLs() {
		if(htmldocs.isEmpty()) {
			htmldocs = database.getHTMLDocs(MAX_HTML_DOCS);
		}
		if(htmldocs.size() != 0)
			System.out.println(htmldocs.get(0).text());
	}
}
