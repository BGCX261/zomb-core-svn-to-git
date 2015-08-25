package zomb.core.server;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Shrimp
 */
public class MimeMap {

    private Map<String, String> mimeTypes = new HashMap();
    private static MimeMap instance;

    public static MimeMap getInstance() {
        if (instance == null) {
            instance = new MimeMap();
        }
        return instance;
    }

    private MimeMap() {
        mimeTypes.put("txt", "text/plain");
        mimeTypes.put("html", "text/html");
        mimeTypes.put("html", "text/html");
        mimeTypes.put("css", "text/css");
        mimeTypes.put("xml", "text/xml");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("jpeg", "image/jpeg");
        mimeTypes.put("gif", "image/gif");
        mimeTypes.put("js", "application/x-javascript");
    }

    public String getMimeType(String fileName) {
        String result = "text/plain";

        String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if (mimeTypes.containsKey(fileExt)) {
            result = mimeTypes.get(fileExt);
        }

        return result;
    }
}
