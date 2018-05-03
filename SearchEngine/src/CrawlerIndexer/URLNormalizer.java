package CrawlerIndexer;

import java.net.URI;
import java.net.URISyntaxException;

public class URLNormalizer {
    public static String normalize(String url) {
        try {
            URI uri = new URI(url);
            url = uri.normalize().toString();
        }catch (URISyntaxException e) {
        }

        if(url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        if(url.contains("#")) {
            url = url.substring(0, url.indexOf("#"));
        }
        return url;
    }
}
