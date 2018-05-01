package CrawlerIndexer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

public class CrawlerThreadManager {
    private int numThreads;  //number of threads to run
    private Vector<String> linksVisited;
    private static final int MAX_THREADS = 30;
    private Vector<Crawler> crawlerList; //create crawler objects depending on numThreads
    private Vector<String> linksToVisit;
    private Vector<Host> hostNames;
    private Vector<String> linksNotToVisit;
    private Vector<WebPage> webPages;

    //static CrawlerThreadManager reference
    private static CrawlerThreadManager single;

    /*
    singleton CrawlerThreadManager object.
     */
    public static CrawlerThreadManager getInstance(Vector<String> linksToVisit, int numThreads){
        if(single == null){
            single = new CrawlerThreadManager(linksToVisit, numThreads);
        }
        return single;
    }

    private void setHostNames() {
        Vector<String> crawledLinks;
        crawledLinks = FileIO.getStartingLinks();
        if(crawledLinks == null) {
            return;
        }
        if(crawledLinks.size() == 0) {
            return;
        }

        for (String crawledLink : crawledLinks) {
            try {
                hostNames.add(new Host(new URL(crawledLink).getHost()));
            } catch (MalformedURLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void setWebPages() {
        Vector<String> links = FileIO.getStartingLinks();
        if(links == null) {
            return;
        }
        if(links.size() == 0) {
            return;
        }
        for(int i = 0; i < links.size(); i++) {
            webPages.add(new WebPage(links.get(i)));
            if(!webPages.get(i).hasValidLink()) {
                webPages.remove(i);
            }
        }
    }

    public Vector<Host> getHostNames() {
        return hostNames;
    }

    public Vector<String> getLinksNotToVisit() {
        return linksNotToVisit;
    }

    public Vector<WebPage> getWebPages() {
        return webPages;
    }

    public void setLinksToVisit(Vector<String> linksToVisit){
        this.linksToVisit = linksToVisit;
    }

    /*
        sets the number of threads to run:
        limited to MAX_THREADS
         */
    public void setNumThreads(int numThreads){
        if(numThreads <= 0 || numThreads > MAX_THREADS){
            System.out.println("invalid value");
        }
        this.numThreads = numThreads;
    }

    private CrawlerThreadManager(Vector<String> linksToVisit, int numThreads) {
        if(numThreads > MAX_THREADS) {
            System.out.println("\nError! cannot start more than " + MAX_THREADS + " threads\n");
            numThreads = MAX_THREADS;
        }
        else if (numThreads <= 0) {
            System.out.println("\nError! please enter a valid number \n");
            return;
        }
        this.numThreads = numThreads;
        linksVisited = new Vector<>();
        crawlerList = new Vector<>();
        this.linksToVisit = linksToVisit;
        setHostNames();
        setWebPages();
        webPages = new Vector<>();
        linksNotToVisit = new Vector<>();
        hostNames = new Vector<>();
    }

    /*
    iterate numThreads times, create crawler, add it to crawlerList,
    start, then sleep for a time(not necessary just for testing).
     */
    public void runCrawlerThreads() {
        for(int i = 0; i < this.numThreads; i++) {
            crawlerList.add(new Crawler(linksToVisit, linksVisited,
                    hostNames, linksNotToVisit, webPages, numThreads,i + 1));
            Crawler crawler = crawlerList.get(i);
            crawler.start();
        }
    }

    public void printLinksToVisit() {
        for(String link:linksToVisit){
            System.out.println(link);
        }
    }

    public Vector<Crawler> getCrawlerList() {
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
