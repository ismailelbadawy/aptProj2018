package CrawlerIndexer;

import java.util.Vector;

public class PageRanker extends Thread {
    private Vector<WebPage> webPages;
    private static PageRanker pageRanker;
    private static final double DAMPING_FACTOR = 0.85;
    private DbManager dbManager = DbManager.getInstance();

    public static PageRanker getInstance() {
        if(pageRanker == null) {
            pageRanker = new PageRanker();
        }
        return pageRanker;
    }

    public void setWebPages(Vector<WebPage> webPages) {
        this.webPages = webPages;
    }

    //using damping factor
    public void setPagesPopularity() {
        double score;
        //calculate page popularity rank
        for(int i = 0; i < webPages.size(); i++) {
            score = 0.0;
            for(int j = 0; j < webPages.size(); j++) {
                if(webPages.get(j).getLinks().contains(webPages.get(i))) {
                    score += webPages.get(j).getPreviousScore() /
                            (double)webPages.get(j).getLinks().size();
                }
                webPages.get(i).setCurrentScore( (1 - DAMPING_FACTOR)/webPages.size() + DAMPING_FACTOR * score);
            }
        }

        for(int i = 0; i < webPages.size(); i++) {
            webPages.get(i).setPreviousScore(webPages.get(i).getCurrentScore());
        }
    }

    public void calculatePopularity(int numIterations) {
        double initialScore = 1.0 / (double)webPages.size();
        for(int i = 0; i < webPages.size(); i++) {
            webPages.get(i).setCurrentScore(initialScore);
            webPages.get(i).setPreviousScore(initialScore);
        }
        for(int i = 0; i < numIterations; i++) {
            setPagesPopularity();
        }
    }

    @Override
    public void run() {
        setPagesPopularity();
    }
}
