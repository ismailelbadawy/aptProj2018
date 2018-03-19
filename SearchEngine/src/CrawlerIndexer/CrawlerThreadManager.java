package CrawlerIndexer;

import java.util.HashSet;
import java.util.ArrayList;

public class CrawlerThreadManager {
    private int numThreads;  //number of threads to run
    private HashSet<String> linksVisited;
    private static final int MAX_THREADS = 5;
    private ArrayList<Crawler> crawlerList; //create crawler objects depending on numThreads
    private HashSet<String> linksToVisit;

    //static CrawlerThreadManager reference
    public static CrawlerThreadManager single;

    /*
    singleton CrawlerThreadManager object.
     */
    public static CrawlerThreadManager getInstance(int numThreads){
        if(single == null){
            single = new CrawlerThreadManager(numThreads);
        }
        return single;
    }

    public void setLinksToVisit(HashSet<String> linksToVisit){
        this.linksToVisit = linksToVisit;
    }

    /*
    sets the number of threads to run:
    maximum 5 threads.
     */
    public void setNumThreads(int numThreads){
        if(numThreads <= 0 || numThreads > MAX_THREADS){
            System.out.println("invalid value");
        }
        this.numThreads = numThreads;
    }

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
        crawlerList = new ArrayList<>();
    }

    //function under construction
    public void runCrawlerThreads() {
        for(int i = 0; i < this.numThreads; i++) {
                crawlerList.add(new Crawler(linksToVisit, linksVisited, i + 1));
            Crawler crawler = crawlerList.get(i);
            crawler.start();
        }

    }
}
