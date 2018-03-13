package CrawlerIndexer;

public class Word {
	private String text;
	private int rank;
	
	public Word(String text) {
		this.text = text;
		this.rank = 0;
	}
	
	public String getText() {
		return text;
	}
	
	public int getRank() {
		return rank;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setRank(int rank) {
		this.rank = rank;
	}
	
}
