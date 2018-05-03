package CrawlerIndexer;

public class Word {
	private String text;
	private Double tf;
	
	public Word(String text) {
		this.text = text;
		this.setTf(1.0);
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}


	public Double getTf() {
		return tf;
	}

	public void setTf(Double tf) {
		this.tf = tf;
	}
}
