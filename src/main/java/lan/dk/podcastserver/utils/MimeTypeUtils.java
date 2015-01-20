package lan.dk.podcastserver.utils;

import lan.dk.podcastserver.entity.Item;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * User: kdavin
 * Date: 08/11/2013
 * Time: 23:36
 */
@Component
public class MimeTypeUtils {

    // https://odoepner.wordpress.com/2013/07/29/transparently-improve-java-7-mime-type-recognition-with-apache-tika/
    private static final Tika tika = new Tika();
    
    private static Map<String, String> MimeMap;
    static
    {
        MimeMap = new HashMap<String, String>();
        MimeMap.put("mp4", "video/mp4");
        MimeMap.put("mp3", "audio/mp3");
        MimeMap.put("flv", "video/flv");
        MimeMap.put("webm", "video/webm");
        MimeMap.put("", "video/mp4");
    }

    public static String getMimeType(String extension) {
        if (extension.isEmpty())
            return "unknown/unknown";

        if (MimeMap.containsKey(extension)) {
            return MimeMap.get(extension);
        } else {
            return "unknown/" + extension;
        }
    }

    public static String getExtension(Item item) {
        if (item.getMimeType() != null) {
            return item.getMimeType().replace("audio/", ".").replace("video/", ".");
        }
        if (item.getPodcast().getType() == "Youtube" || item.getUrl().lastIndexOf(".") == -1 ) {
            return ".mp4";
        } else {
            return item.getUrl().substring(item.getUrl().lastIndexOf("."));
        }
    }
    
    public static String probeContentType(Path path) throws IOException {
        return tika.detect(path.toFile());
    }

}
