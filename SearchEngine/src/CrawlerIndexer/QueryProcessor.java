package CrawlerIndexer;

import org.tartarus.snowball.ext.PorterStemmer;

import java.util.ArrayList;
import java.util.HashSet;

public class QueryProcessor {
    private DbManager dbManager;

    public QueryProcessor(){
        dbManager = DbManager.getInstance();
    }

    public ArrayList<SearchResult> search(String query){
        if(query == null){
            return null;
        }
        if(query.length() == 0){
            return null;
        }
        if(query.contains(" ")){
            //More than one word.
            HashSet<SearchResult> searchResults = new HashSet<>();
            String[] words = query.split(" ");
            searchResults.addAll(dbManager.searchWords(words[0]));
            for(int i = 1 ; i < words.length; i++){
                searchResults.retainAll(dbManager.searchWords(words[i]));
            }
            if(searchResults.isEmpty()){

            }
            return new ArrayList<>(searchResults);
        }else{
            String word = stemWord(query);
            return dbManager.searchWords(word);
        }

    }

    public String stemWord(String query){
        PorterStemmer stemmer = new PorterStemmer();
        stemmer.setCurrent(query);
        stemmer.stem();
        String word = stemmer.getCurrent();
        return word;
    }
}
