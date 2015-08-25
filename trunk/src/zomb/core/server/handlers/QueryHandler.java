package zomb.core.server.handlers;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import zomb.core.CommandLine;
import zomb.core.server.ZHttpHandler;
import zomb.core.server.ZHttpRequest;
import zomb.core.server.ZQuery;
import zomb.core.plugins.PluginExecutor;
import zomb.core.plugins.PluginResponse;

/**
 *
 * @author Shrimp
 */
public class QueryHandler extends ZHttpHandler {

    @Override
    protected HttpResponse handleRequest(ZHttpRequest req) {

        if (Integer.valueOf(CommandLine.getParam("debug", "0")) > 0) {
            System.out.println(req.getRequestBody());
        }

        String res = "unknown query";

        if (!req.getRequestBody().isEmpty()) {
            ZQuery query = null;
            try {
                query = new ZQuery(new JSONObject(req.getRequestBody()));
            } catch (JSONException e) {
                e.printStackTrace();
                res = "bad request";
            }

            if (query != null) {
                PluginExecutor exec = new PluginExecutor();

                List<PluginResponse> response = exec.executeCommand(query.getSource(), query.getSender(), query.getQuery(), query.isDirect());

                JSONArray a = new JSONArray();

                for (PluginResponse r : response) {
                    a.put(r.toJson());
                }

                res = a.toString();
            } else {
                res = "invalid request";
            }
        } else {
            res = "empty request";
        }

        if (Integer.valueOf(CommandLine.getParam("debug", "0")) > 0) {
            System.out.println(res);
        }

        return new HttpResponse("text/plain", res);
    }
}
