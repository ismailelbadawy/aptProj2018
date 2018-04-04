package CrawlerIndexer;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileIO {

    public static ArrayList<String> readStopWords(){
        try {
            Scanner s = new Scanner(new File("I:\\Programming\\IdeaJ\\aptProj2018\\SearchEngine\\bin\\Stopwords.txt"));
            ArrayList<String> list = new ArrayList<String>();
            while (s.hasNext()){
                list.add(s.next());
            }
            s.close();
            return list;
        }catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        return null;

    }

    public static ArrayList<String> getStartingLinks(){
        try{
            Scanner scanner = new Scanner(new File("I:\\Programming\\IdeaJ\\aptProj2018\\SearchEngine\\bin\\StarterLinks.txt"));
            ArrayList<String> links = new ArrayList<>();
            while(scanner.hasNext()){
                links.add(scanner.nextLine());
            }
            scanner.close();
            return links;
        }catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        return null;
    }
}
