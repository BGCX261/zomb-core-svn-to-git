package zomb.core;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Shrimp
 */
public class CommandLine {

    private static Map<String, String> params = new HashMap();

    public static String getParam(String param, String def) {
        String res = def;
        if (params.containsKey(param)) {
            res = params.get(param);
        }
        return res;
    }

    public static void loadParams(String[] args) {
        params.clear();
        for (String s : args) {
            if (s.startsWith("--")) {
                s = s.replaceFirst("--", "");
            }

            if (s.startsWith("-")) {
                s = s.replaceFirst("-", "");
            }

            if (s.contains("=")) {
                String[] p = s.split("=");
                params.put(p[0], p[1]);
            } else {
                params.put(s, "true");
            }
        }
    }
}
