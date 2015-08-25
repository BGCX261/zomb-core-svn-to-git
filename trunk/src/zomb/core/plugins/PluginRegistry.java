package zomb.core.plugins;

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import zomb.core.HttpUtils;

/**
 *
 * @author Shrimp
 */
public class PluginRegistry {

    private static PluginRegistry instance;
    private List<ZPlugin> plugins = new ArrayList();
    private final String PLUGIN_PATH = "plugins";

    public static PluginRegistry getInstance() {
        if (instance == null) {
            instance = new PluginRegistry();
            instance.loadPlugins();
        }
        return instance;
    }

    public ZPlugin loadRemotePlugin(String url) throws PluginLoaderException {
        ZPlugin res = null;
        try {
            String pluginSource = HttpUtils.httpPost(url, "");
            System.out.println(pluginSource);
            JSONObject pluginData = new JSONObject(pluginSource);

            res = new RemotePlugin();

            res.setAddedDate(new Date());
            res.setUrl(url);

            /*
             * get the name, abort if no name present
             */
            if (!pluginData.optString("name", "").isEmpty()) {
                res.setName(pluginData.getString("name"));
            } else {
                throw new PluginLoaderException("Plugin name not defined");
            }

            /*
             * get the help string, ignore if none present
             */
            res.setHelp(pluginData.optString("help", "No help available"));

            /*
             * load commands list
             */
            if (pluginData.optJSONArray("commands").length() > 0) {

                JSONArray cmds = pluginData.getJSONArray("commands");
                for (int i = 0; i < cmds.length(); i++) {
                    ZPluginCommand c = new ZPluginCommand();

                    /*
                     * if a command doesn't have a name, abort
                     */
                    if (!cmds.getJSONObject(i).optString("name", "").isEmpty()) {
                        c.setName(cmds.getJSONObject(i).getString("name"));
                    } else {
                        throw new PluginLoaderException("Empty command name at index " + i);
                    }

                    /*
                     * command help is optional
                     */
                    c.setHelp(cmds.getJSONObject(i).optString("help", "No help available"));

                    /*
                     * minimum parameter count, optional, -1 means it will be
                     * ignored
                     */
                    c.setParamCount(cmds.getJSONObject(i).optInt("paramCount", -1));

                    /*
                     * if there are patterns defined for this command, compile
                     * them now, and abort on failure
                     */
                    if (cmds.getJSONObject(i).has("patterns")) {
                        for (int j = 0; j < cmds.getJSONObject(i).getJSONArray("patterns").length(); j++) {
                            try {
                                ZPluginCommandPattern pat = new ZPluginCommandPattern();
                                if (cmds.getJSONObject(i).optJSONArray("patterns").get(j) instanceof String) {
                                    pat.setPattern(Pattern.compile(cmds.getJSONObject(i).optJSONArray("patterns").getString(j)));
                                } else {
                                    pat.setPattern(Pattern.compile(cmds.getJSONObject(i).optJSONArray("patterns").getJSONObject(j).getString("pattern")));
                                    pat.setAddressedOnly(cmds.getJSONObject(i).optJSONArray("patterns").getJSONObject(j).optBoolean("addressedOnly"));
                                }
                                c.getPatterns().add(pat);
                            } catch (PatternSyntaxException ex) {
                                ex.printStackTrace();
                                throw new PluginLoaderException("Error compiling pattern " + j + ": " + ex.getDescription());
                            }
                        }
                    }

                    res.getCommands().add(c);
                }

            } else {
                throw new PluginLoaderException("Plugin commands not defined");
            }

            res.setId(UUID.randomUUID().toString());

        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new PluginLoaderException("Error loading plugin definition - " + ex.getMessage());
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            throw new PluginLoaderException("Bad URL");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new PluginLoaderException("404");
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new PluginLoaderException("Load failed - " + ex.getMessage());
        }


        return res;
    }

    public void addPlugin(ZPlugin plugin) {
        removePlugin(getByName(plugin.getName()));

        plugins.add(plugin);

        if (plugin.isSave()) {
            savePlugins();
        }
    }

    public void removePlugin(ZPlugin plugin) {
        if (plugins.remove(plugin)) {
            savePlugins();
        }
    }

    public ZPlugin getByName(String name) {
        ZPlugin res = null;
        for (ZPlugin p : plugins) {
            if (p.getName().equalsIgnoreCase(name)) {
                res = p;
                break;
            }
        }

        return res;
    }

    public List<ZPlugin> getPlugins() {
        return plugins;
    }

    private void loadPlugins() {
        File[] pList = new File(PLUGIN_PATH).listFiles();
        for (File f : pList) {
            if (f.isFile()) {
                try {
                    ObjectInputStream os = new ObjectInputStream(new FileInputStream(f));
                    plugins.add((ZPlugin) os.readObject());
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void savePlugins() {
        for (ZPlugin p : plugins) {
            if (p.isSave()) {
                try {
                    ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(PLUGIN_PATH + "/" + p.getId()));
                    os.writeObject(p);
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        File path = new File(PLUGIN_PATH);
        for (File f : path.listFiles()) {
            boolean keep = false;
            if (f.isFile() && !f.isDirectory()) {
                for (ZPlugin p : plugins) {
                    if (f.getName().equals(p.getId())) {
                        keep = true;
                        break;
                    }
                }
                if (!keep) {
                    f.delete();
                }
            }
        }
    }
}
