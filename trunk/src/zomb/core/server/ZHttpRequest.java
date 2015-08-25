package zomb.core.server;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Shrimp
 */
public class ZHttpRequest {

    private HttpExchange ex;
    private Map<String, String> params = new HashMap();
    private Map<String, String> cookies = new HashMap();
    private String requestBody;

    public ZHttpRequest(HttpExchange ex) {
        this.ex = ex;
        setParams(this.ex.getRequestURI().getRawQuery());
        try {
            requestBody = read(this.ex.getRequestBody());
            if (requestBody != null) {
                setParams(requestBody);
            }
            if (this.ex.getRequestHeaders().containsKey("Cookie")) {
                setCookies(this.ex.getRequestHeaders().getFirst("Cookie"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPath() {
        return ex.getRequestURI().getPath().replaceFirst(ex.getHttpContext().getPath(), "");
    }

    public void setParam(String name, String value) {
        params.put(name, value);
    }

    public String getParam(String name) {
        return getParam(name, null);
    }

    public String getParam(String name, String defValue) {
        String result = defValue;

        if (params.containsKey(name)) {
            result = params.get(name);
        }

        return result;
    }

    public String getCookie(String name) {
        return getCookie(name, null);
    }

    public String getCookie(String name, String defValue) {
        String result = defValue;

        if (cookies.containsKey(name)) {
            result = cookies.get(name);
        }

        return result;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public HttpExchange getHttpExchange() {
        return ex;
    }

    private void setParams(String query) {
        if ((query != null) && !query.trim().isEmpty()) {
            String[] paramParts = query.trim().split("&");
            for (String param : paramParts) {
                try {
                    String[] p = param.split("=");
                    if (p.length == 2) {
                        setParam(URLDecoder.decode(p[0], "UTF-8"), URLDecoder.decode(p[1], "UTF-8"));
                    } else {
                        setParam(URLDecoder.decode(p[0], "UTF-8"), "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setCookies(String cookie) {
        if ((cookie != null) && !cookie.trim().isEmpty()) {
            String[] cookieParts = cookie.trim().split(";");
            for (String c : cookieParts) {
                try {
                    String[] p = c.trim().split("=");
                    if (p.length == 2) {
                        cookies.put(URLDecoder.decode(p[0], "UTF-8"), URLDecoder.decode(p[1], "UTF-8"));
                    } else {
                        cookies.put(URLDecoder.decode(p[0], "UTF-8"), "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Map<String, String> getParams() {
        return params;
    }

    private String read(InputStream is) throws IOException {
        StringBuilder result = new StringBuilder();

        int b = is.read();
        while ((b != -1)) {
            result.append(String.valueOf((char) b));
            b = is.read();
        }

        return result.toString();
    }

    public void setCookie(String name, String value, long lifeSpanDays, String path) {
        String cookie = name + "=" + value;
        if (!path.isEmpty()) {
            cookie += "; path=" + path;
        }

        if (lifeSpanDays != 0) {
            cookie += "; expires=" + (new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss zzz").format(new Date(System.currentTimeMillis() + (86400000 * lifeSpanDays))));
        }

        getHttpExchange().getResponseHeaders().add("Set-Cookie", cookie);
        System.out.println("Set-Cookie: " + cookie);
        cookies.put(name, value);
    }

    public void setCookie(String name, String value) {
        setCookie(name, value, 0, "");
    }
}
