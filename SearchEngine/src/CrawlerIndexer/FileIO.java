package CrawlerIndexer;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

public class FileIO {

    public static Vector<String> readStopWords(){
        try {
            Scanner s = new Scanner(new File("/home/engineer/Desktop/CCE/CMPN306/Project/aptProj2018/SearchEngine/bin/Stopwords.txt"));
            Vector<String> list = new Vector<>();
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

    public static Vector<String> getStartingLinks(){
        try{
            Scanner scanner = new Scanner(new File("/home/engineer/Desktop/CCE/CMPN306/Project/aptProj2018/SearchEngine/bin/StarterLinks.txt"));
            Vector<String> links = new Vector<>();
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

    public static Vector<String> getBlackListedHosts() {
        Vector<String> list = new Vector<>();
        try {
            Scanner scanner = new Scanner(new File("/home/engineer/Desktop/CCE/CMPN306/Project/aptProj2018/SearchEngine/bin/pagesData.txt"));

            while (scanner.hasNext()) {
                list.add(scanner.next());
            }
            scanner.close();
        }catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }

        return list;
    }

    public static Vector<WebPage> getPages() {
        Vector<WebPage> webPages = new Vector<>();
        try {
            Scanner scanner = new Scanner(new File("/home/engineer/Desktop/CCE/CMPN306/Project/aptProj2018/SearchEngine/bin/pagesData.txt"));
            Vector<String> list = new Vector<>();
            while(scanner.hasNext()) {
                list.add(scanner.next());
            }
            scanner.close();

            Vector<String> links = new Vector<>();
            Vector<Integer> pageRanks = new Vector<>();
            Vector<Double> previousScores = new Vector<>();

            setPagesParameters(list, links, pageRanks, previousScores);

            for(int i = 0; i < links.size(); i++) {
                webPages.add(new WebPage(links.get(i), previousScores.get(i)));
            }

            return webPages;

        }catch(FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    private static void setPagesParameters(Vector<String> stringArr, Vector<String> links,
                                          Vector<Integer> pageRanks, Vector<Double> previousScores) {
        for(int i = 0; i < stringArr.size(); i+= 3) {
            links.add(stringArr.get(i));
            pageRanks.add(Integer.parseInt(stringArr.get(i + 1)));
            previousScores.add(Double.parseDouble(stringArr.get(i + 2)));
        }
    }
}
