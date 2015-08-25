package zomb.core.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.Deflater;

/**
 *
 * @author Shrimp
 */
public abstract class ZHttpHandler implements HttpHandler {

    private final long CACHE_TIME = (60000 * 60 * 24 * 14);
    private final String PUB_PATH = "public/";

    @Override
    public void handle(HttpExchange t) throws IOException {
        String path = t.getRequestURI().getPath().replaceFirst(t.getHttpContext().getPath(), "");
        Headers requestHeaders = t.getRequestHeaders();
        Headers responseHeaders = t.getResponseHeaders();
        try {
            if (!serveFile(path, t)) {
                ZHttpRequest req = new ZHttpRequest(t);

                HttpResponse response = handleRequest(req);
                if (response != null) {
                    OutputStream os = t.getResponseBody();
                    responseHeaders.add("Content-Type", response.getContentType());
                    byte[] bodyBytes = response.getBody().getBytes("UTF-8");
                    if (requestHeaders.containsKey("Accept-Encoding") && requestHeaders.getFirst("Accept-Encoding").contains("deflate")) {
                        bodyBytes = deflateBytes(bodyBytes);
                        if (requestHeaders.containsKey("User-agent") && requestHeaders.getFirst("User-agent").contains("MSIE")) {
                            responseHeaders.add("Content-Encoding", "gzip");
                        } else {
                            responseHeaders.add("Content-Encoding", "deflate");
                        }
                    }
                    t.sendResponseHeaders(200, bodyBytes.length);
                    os.write(bodyBytes);
                    os.flush();
                    os.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(t.getRemoteAddress().getAddress().getHostAddress()
                + " - - " + (new SimpleDateFormat("[dd/MMM/yyyy:HH:mm:ss Z]").format(new Date()))
                + " \"" + t.getRequestMethod() + " " + t.getRequestURI().getPath() + "\" "
                + t.getResponseCode() + " " + ((responseHeaders.getFirst("Content-Length") != null) ? responseHeaders.getFirst("Content-Length") : "0"));
        //+ " \"" + t.getRequestHeaders().getFirst("User-agent") + "\"");
    }

    protected abstract HttpResponse handleRequest(ZHttpRequest req);

    protected boolean serveFile(String path, HttpExchange t) {
        boolean result = false;

        Headers requestHeaders = t.getRequestHeaders();
        Headers responseHeaders = t.getResponseHeaders();

        path = path.replace("..", "");
        File staticFile = new File(PUB_PATH + path);
        if (staticFile.exists() && !staticFile.isDirectory()) {

            InputStream is = null;
            OutputStream os = null;
            try {
                String etag = getEtag(staticFile);

                responseHeaders.add("Content-type", MimeMap.getInstance().getMimeType(path));
                responseHeaders.add("Date", new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z").format(new Date()));
                responseHeaders.add("Expires", new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z").format(new Date(System.currentTimeMillis() + CACHE_TIME)));
                responseHeaders.add("ETag", etag);

                if (requestHeaders.containsKey("If-None-Match") && (requestHeaders.getFirst("If-None-Match").equalsIgnoreCase(etag))) {
                    responseHeaders.add("Connection", "close");
                    t.sendResponseHeaders(304, 0);
                    result = true;
                } else {
                    try {
                        os = t.getResponseBody();
                        is = new FileInputStream(staticFile);

                        byte[] fileBytes = new byte[(int) staticFile.length()];
                        is.read(fileBytes);

                        if (requestHeaders.containsKey("Accept-Encoding") && requestHeaders.getFirst("Accept-Encoding").contains("deflate")) {
                            fileBytes = deflateBytes(fileBytes);
                            if (requestHeaders.containsKey("User-agent") && requestHeaders.getFirst("User-agent").contains("MSIE")) {
                                responseHeaders.add("Content-Encoding", "gzip");
                            } else {
                                responseHeaders.add("Content-Encoding", "deflate");
                            }
                        }

                        t.sendResponseHeaders(200, fileBytes.length);

                        os.write(fileBytes);

                        result = true;
                    } finally {
                        try {
                            is.close();
                            os.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private byte[] deflateBytes(byte[] bytes) {
        byte[] result = null;
        try {
            Deflater zip = new Deflater(Deflater.BEST_COMPRESSION);

            zip.setInput(bytes);
            zip.finish();

            byte[] buff = new byte[1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length);

            while (!zip.finished()) {
                int c = zip.deflate(buff);
                bos.write(buff, 0, c);
            }
            bos.close();

            result = bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getEtag(File file) {
        String result = "m" + Long.toString(file.lastModified()) + "s" + Long.toString(file.length());

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(result.getBytes());
            result = new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public class HttpResponse {

        private String contentType;
        private String body;

        public HttpResponse(String contentType, String body) {
            this.contentType = contentType;
            this.body = body;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }
    }
}
