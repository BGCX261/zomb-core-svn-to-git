package zomb.core.plugins;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Shrimp
 */
public class PluginResponse {

    private final static DateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String multiLine;
    private String plugin;
    private Date date = new Date();
    private List<String> response = new ArrayList();
    private String image;
    private boolean error;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response.clear();
        this.response.add(response);
    }

    public void addResponse(String response) {
        this.response.add(response);
    }

    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    public void setMultiLine(String multiLine) {
        this.multiLine = multiLine;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public JSONObject toJson() {
        JSONObject res = new JSONObject();

        try {
            res.putOpt("plugin", plugin);
            res.putOpt("date", DATE_FMT.format(date));
            JSONArray a = new JSONArray();
            for (String s : response) {
                a.put(s);
            }
            res.putOpt("response", a);
            res.putOpt("multiLine", multiLine);
            res.putOpt("image", image);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
}
