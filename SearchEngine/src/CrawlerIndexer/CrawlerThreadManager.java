package CrawlerIndexer;

import java.util.ArrayList;

public class CrawlerThreadManager {
    private int numThreads;  //number of threads to run
    private ArrayList<String> linksVisited;
    private static final int MAX_THREADS = 100;
    private ArrayList<Crawler> crawlerList; //create crawler objects depending on numThreads
    private ArrayList<String> linksToVisit;
    private ArrayList<String> linksNotToVisit;

    //static CrawlerThreadManager reference
    public static CrawlerThreadManager single;

    /*
    singleton CrawlerThreadManager object.
     */
    public static CrawlerThreadManager getInstance(ArrayList<String> linksToVisit, int numThreads){
        if(single == null){
            single = new CrawlerThreadManager(linksToVisit, numThreads);
        }
        return single;
    }

    public void setLinksToVisit(ArrayList<String> linksToVisit){
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

    private CrawlerThreadManager(ArrayList<String> linksToVisit, int numThreads) {
        if(numThreads > MAX_THREADS) {
            System.out.println("\nError! cannot start more than " + MAX_THREADS + " threads\n");
            return;
        }
        else if (numThreads <= 0) {
            System.out.println("\nError! please enter a valid number 1-5\n");
            return;
        }
        this.numThreads = numThreads;
        linksVisited = new ArrayList<>();
        crawlerList = new ArrayList<>();
        this.linksToVisit = linksToVisit;
    }

    /*
    iterate numThreads times, create crawler, add it to crawlerList,
    start, then sleep for a time(not necessary just for testing).
     */
    public void runCrawlerThreads() {
        for(int i = 0; i < this.numThreads; i++) {
            crawlerList.add(new Crawler(linksToVisit, linksVisited,i + 1));
            Crawler crawler = crawlerList.get(i);
            crawler.start();
            try{
                crawler.sleep(2500);
            }catch(InterruptedException e){
                System.out.println(e.getMessage());
            }
        }
    }

    public void printLinksToVisit() {
        for(String link:linksToVisit){
            System.out.println(link);
        }
    }

    public ArrayList<Crawler> getCrawlerList() {
        return crawlerList;
    }

    public void printLinksVisited() {
        for(String link:linksVisited){
            System.out.println(link);
        }
    }

    public int getNumberOfCrawledPages() {
        int totalCrawledPages = 0;
        Crawler crawler;
        for(int i = 0; i < numThreads; i++) {
            crawler = crawlerList.get(i);
            if(crawler != null) {
                totalCrawledPages += crawler.getNumCrawledPages();
            }
        }
        return totalCrawledPages;
    }

    public void killAllThreads() {
        Crawler crawler;
        for(int i = crawlerList.size() - 1; i >= 0; i--) {
           crawler = crawlerList.get(i);
           if(crawler != null) {
               crawler.exit();
               crawlerList.remove(i);
           }
        }
    }
}
