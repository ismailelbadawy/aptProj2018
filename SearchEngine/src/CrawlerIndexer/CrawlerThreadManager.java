package CrawlerIndexer;

import java.util.HashSet;
import java.util.List;

public class CrawlerThreadManager {
    private int numThreads;  //number of threads to run
    private HashSet<String> linksVisited;
    private static final int MAX_THREADS = 5;
    private List<Crawler> crawlerList; //create crawler objects depending on numThreads
    private static HashSet<String> linksToVisit;

    private CrawlerThreadManager(int numThreads) {
        if(numThreads > MAX_THREADS) {
            System.out.println("\nError! cannot start more than " + MAX_THREADS + " threads\n");
            return;
        }
        else if (numThreads <= 0) {
            System.out.println("\nError! please enter a valid number 1-5\n");
            return;
        }
        this.numThreads = numThreads;
        linksVisited = new HashSet<>();
    }

    //function under construction
    public void runCrawlerThreads() {
        for(int i = 0; i < this.numThreads; i++) {
            crawlerList.add(new Crawler(linksToVisit, linksVisited));
            Crawler cwl = crawlerList.get(i);
            cwl.start();
        }
    }
}
