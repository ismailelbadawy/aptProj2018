package CrawlerIndexer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/*
This thread runs separately checking for internet connection
 */
public class InternetConnectionThread extends Thread{

    public static InternetConnectionThread connectionThread;
    private boolean isConnectedToInternet;

    private InternetConnectionThread() {
        isConnectedToInternet = false;
    }

    public static InternetConnectionThread getInstance() {
        if(connectionThread == null) {
            connectionThread = new InternetConnectionThread();
        }
        return connectionThread;
    }

    public boolean isConnectedToInternet() {
        return isConnectedToInternet;
    }

    @Override
    public void run() {
        while(true) {
            try {
                URL url = new URL("https://www.google.com");
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                isConnectedToInternet = true;
            }catch (MalformedURLException e) {
                isConnectedToInternet = false;
            }catch (IOException e) {
                isConnectedToInternet = false;
            }
            try {
                sleep(500);
            }catch (InterruptedException e) {
                return;
            }
        }
    }
}
