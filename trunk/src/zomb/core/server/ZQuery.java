package zomb.core.server;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Shrimp
 */
public class ZQuery {

    private String source;
    private String sender;
    private String query;
    private boolean direct;

    public ZQuery(JSONObject obj) throws JSONException {
        source = obj.getString(obj.has("src") ? "src" : "source");
        sender = obj.getString(obj.has("name") ? "name" : "sender");
        direct = obj.optBoolean("direct", true);
        query = obj.getString(obj.has("q") ? "q" : "query");
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isDirect() {
        return direct;
    }

    public void setDirect(boolean direct) {
        this.direct = direct;
    }
}
