package CrawlerIndexer;

import java.util.ArrayList;

public class Indexer {
	ArrayList<IndexerThread> indexers;
    public Indexer(){
        indexers = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            IndexerThread indexer = new IndexerThread();
            indexer.start();
            indexers.add(indexer);
        }
    }

    public void stopAllThreads(){
        for(IndexerThread thread : indexers){
            if(thread.getState() == Thread.State.WAITING){
                thread.exit();
                thread.notify();
            }
        }
    }

}
