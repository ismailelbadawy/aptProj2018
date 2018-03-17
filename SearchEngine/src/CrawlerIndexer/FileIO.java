package CrawlerIndexer;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileIO {

    public static ArrayList<String> readStopWords(){
        try {
            Scanner s = new Scanner(new File("C:\\Users\\Ismail\\Documents\\GitHub\\aptProj2018\\aptProj2018\\SearchEngine\\bin\\Stopwords.txt"));
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
}
