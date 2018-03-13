package CrawlerIndexer;

import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class IndexerThread extends Thread{
	
	ArrayList<Document> htmldocs;
	private final int MAX_HTML_DOCS = 10;
	
	
	public IndexerThread() {
		//Insatntiate the arraylist.
		htmldocs = new ArrayList<>();
		
	}
	
	@Override
	public void run() {
		
	}
	
	private synchronized void getHTMLs() {
		if(htmldocs.isEmpty()) {
			
		}
	}
}
