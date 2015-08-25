package zomb.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Shrimp
 */
public class HttpUtils {

    private static int readTimeout = 20000;

    /**
     * Perform a HEAD request on a URL. Handy to test an HTTP server's status
     *
     * @param website the URL to check.
     * @return 1 on success, 0 on failure
     */
    public static int httpHead(String website) {
        HttpURLConnection conn = null;

        int result = 0;

        try {
            URL url = new URL(website);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.setDoOutput(true);
            conn.setReadTimeout(readTimeout);
            conn.connect();

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }

            result = 1;

            rd.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return result;
    }

    /**
     * POSTs simple data to a URL
     *
     * @param webUrl the URL of the page/service to submit data to
     * @param params HTTP encoded name=value pairs, separated with "&amp;", or POST body
     * @return the server's response
     */
    public static String httpPost(String webUrl, String params) throws MalformedURLException, IOException {
        HttpURLConnection conn = null;

        String result = "";

        try {
            URL url = new URL(webUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setReadTimeout(readTimeout);
            conn.connect();

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(params);
            wr.flush();

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }

            result = sb.toString();

            wr.close();
            rd.close();
        } finally {
        }

        return result;
    }

    /**
     * @return the current read timeout for HTTP requests
     */
    public static int getReadTimeout() {
        return readTimeout;
    }

    /**
     * Set the read timeout for HTTP requests
     *
     * @param readTimeout timeout in milliseconds
     */
    public static void setReadTimeout(int readTimeout) {
        HttpUtils.readTimeout = readTimeout;
    }
}
