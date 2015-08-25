package zomb.core.plugins;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import zomb.core.HttpUtils;

/**
 *
 * @author Shrimp
 */
public class RemotePlugin extends ZPlugin {

    @Override
    public PluginResponse processQuery(String sourceName, String senderName, String command, List<String> params, String origionalQuery, boolean addressedDirectly) {
        PluginResponse res = new PluginResponse();

        res.setPlugin(getName());

        try {
            JSONObject postBody = new JSONObject();
            postBody.put("source", sourceName);
            postBody.put("sender", senderName);
            postBody.put("query", origionalQuery);
            postBody.put("direct", addressedDirectly);
            postBody.put("params", params);
            postBody.put("time", String.format("%tc", System.currentTimeMillis()));

            try {
                String remoteRes = HttpUtils.httpPost(String.format("%1s?command=%2s", getUrl(), command), postBody.toString()).trim();

                System.out.println(remoteRes);

                if (remoteRes.startsWith("{") && remoteRes.endsWith("}")) {
                    JSONObject o = new JSONObject(remoteRes);

                    if (o.has("image")) {
                        res.setImage(o.getString("image"));
                    }

                    if (o.has("multiLine")) {
                        res.setMultiLine(o.getString("multiLine"));
                    }

                    if (o.has("error")) {
                        res.setError(o.getBoolean("error"));
                    }

                    if (o.has("response")) {
                        JSONArray a = o.getJSONArray("response");
                        for (int i = 0; i < a.length(); i++) {
                            res.addResponse(a.getString(i));
                        }
                    }

                } else {
                    if (remoteRes.contains("\n")) {
                        String[] lines = remoteRes.trim().split("\n");
                        for (String l : lines) {
                            res.addResponse(l);
                        }
                    } else {
                        res.setResponse(remoteRes);
                    }
                }

            } catch (MalformedURLException ex) {
                res.setResponse("URL error");
            } catch (IOException ex) {
                res.setResponse("IO Error");
            } catch (JSONException ex) {
                res.setResponse("Malformed response: " + ex.getMessage());
            }
        } catch (JSONException je) {
            res.setResponse("Error building request: " + je.getMessage());
        }

        return res;
    }
}