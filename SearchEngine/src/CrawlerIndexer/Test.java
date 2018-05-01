package CrawlerIndexer;

public class Test {
	
	public static void main(String[] args) {
		DbManager db = DbManager.getInstance();

		String url = "https://www.google.com/search?source=hp&ei=q4XoWrXHLoGRkwXetJBg&q=madras&oq=madras&gs_l=psy-ab.3.0.0l4j0i46k1j46l2j0l5.458.1224.0.3384.7.6.0.0.0.0.198.670.0j4.4.0....0...1.1.64.psy-ab..3.4.666.0..0i131k1j0i10k1.0.GoWVBBGkDUo";
		url = URLNormalizer.normalize(url);
		System.out.println(url);
		/*String normalizedUrl;
		for(int i = 0; i < 100; i += 4) {
			try {
				URI uri = new URI(links.get(i));
				normalizedUrl = uri.normalize().toString();
			}catch (URISyntaxException e) {
				continue;
			}
			System.out.println(links.get(i));
			System.out.println(normalizedUrl);
		}*/
		/*if(links == null){
            links = FileIO.getStartingLinks();
			System.out.println("Error in file.");
		}
		else {
		    if(links.size() == 0) {
		        //start from the beginning
                links = FileIO.getStartingLinks();
            }
        }*/
		/*Vector<String> links = FileIO.getStartingLinks();
		CrawlerThreadManager ctm = CrawlerThreadManager.getInstance(links,10);
		ctm.runCrawlerThreads();

		//Indexer indexer = new Indexer();

		while(true) {
		    if(ctm.getNumberOfCrawledPages() >= 5000) {
		        break;
            }
            Scanner sc = new Scanner(System.in);
		    sc.next();
		    System.out.println("Number of crawled pages till now: " + ctm.getNumberOfCrawledPages());
		    //
        }

        ctm.killAllThreads();*/

	}
}
