package CrawlerIndexer;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileIO {

    public static ArrayList<String> readStopWords(){
        try {
            Scanner s = new Scanner(new File("/home/engineer/Desktop/CCE/CMPN306/Project/aptProj2018/SearchEngine/bin/Stopwords.txt"));
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
            Scanner scanner = new Scanner(new File("/home/engineer/Desktop/CCE/CMPN306/Project/aptProj2018/SearchEngine/bin/StarterLinks.txt"));
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

    public static ArrayList<String> getBlackListedHosts() {
        ArrayList<String> list = new ArrayList<>();
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

    public static ArrayList<WebPage> getPages() {
        ArrayList<WebPage> webPages = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File("/home/engineer/Desktop/CCE/CMPN306/Project/aptProj2018/SearchEngine/bin/pagesData.txt"));
            ArrayList<String> list = new ArrayList<>();
            while(scanner.hasNext()) {
                list.add(scanner.next());
            }
            scanner.close();

            ArrayList<String> links = new ArrayList<>();
            ArrayList<Integer> pageRanks = new ArrayList<>();
            ArrayList<Double> previousScores = new ArrayList<>();

            setPagesParameters(list, links, pageRanks, previousScores);

            for(int i = 0; i < links.size(); i++) {
                webPages.add(new WebPage(links.get(i), pageRanks.get(i), previousScores.get(i)));
            }

            return webPages;

        }catch(FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public static void setPagesParameters(ArrayList<String> stringArr, ArrayList<String> links,
                                          ArrayList<Integer> pageRanks, ArrayList<Double> previousScores) {
        for(int i = 0; i < stringArr.size(); i+= 3) {
            links.add(stringArr.get(i));
            pageRanks.add(Integer.parseInt(stringArr.get(i + 1)));
            previousScores.add(Double.parseDouble(stringArr.get(i + 2)));
        }
    }
}
