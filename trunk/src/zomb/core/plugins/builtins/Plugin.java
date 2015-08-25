package zomb.core.plugins.builtins;

import zomb.core.plugins.ZPluginCommand;
import zomb.core.plugins.ZPlugin;
import zomb.core.plugins.PluginRegistry;
import zomb.core.plugins.ZPluginCommandPattern;
import zomb.core.plugins.PluginResponse;
import zomb.core.plugins.PluginLoaderException;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author Shrimp
 */
public class Plugin extends ZPlugin {

    public Plugin() {
        setSave(false);
        setSwallowCommand(true);

        setName("plugin");
        setHelp("Allows the adding and removing of plugins");

        ZPluginCommand c = new ZPluginCommand();
        c.setName("add");
        c.setHelp("Add a plugin. One parameter expected, the URL to a plugin service");
        getCommands().add(c);


        c = new ZPluginCommand();
        c.setName("remove");
        c.setHelp("Remove a plugin. One parameter expected, which can be either the plugin name or the URL to the plugin service");
        getCommands().add(c);


        c = new ZPluginCommand();
        c.setName("refresh");
        c.setHelp("Reload a plugin definition");
        getCommands().add(c);

        c = new ZPluginCommand();
        c.setName("list");
        c.setHelp("List all installed plugins");
        getCommands().add(c);


        c = new ZPluginCommand();
        ZPluginCommandPattern p = new ZPluginCommandPattern();
        p.setAddressedOnly(true);
        c.setName("help");
        p.setPattern(Pattern.compile("help .+"));
        c.setHelp("See the help from a plugin");
        c.getPatterns().add(p);
        getCommands().add(c);
    }

    @Override
    public PluginResponse processQuery(String sourceName, String senderName, String command, List<String> params, String origionalQuery, boolean addressedDirectly) {
        PluginResponse res = new PluginResponse();

        res.setPlugin(getName());

        if (command.equalsIgnoreCase("add")) {
            try {
                res.setResponse(addPlugin(params.get(0)));
            } catch (PluginLoaderException ex) {
                ex.printStackTrace();
                res.setResponse("Add plugin failed: " + ex.getMessage());
            }
        } else if (command.equalsIgnoreCase("remove")) {
            try {
                res.setResponse(removePlugin(params.get(0)));
            } catch (PluginLoaderException ex) {
                ex.printStackTrace();
                res.setResponse("Remove plugin failed: " + ex.getMessage());
            }
        } else if (command.equalsIgnoreCase("refresh")) {

            try {
                res.setResponse(refreshPlugin(params.get(0)));
            } catch (PluginLoaderException ex) {
                ex.printStackTrace();
                res.setResponse("Refresh plugin failed: " + ex.getMessage());
            }

        } else if (command.equalsIgnoreCase("list")) {
            for (ZPlugin p : PluginRegistry.getInstance().getPlugins()) {
                res.addResponse(p.getName());
            }
        } else if (command.equalsIgnoreCase("help")) {
            if (params.size() > 0) {
                ZPlugin plugin = PluginRegistry.getInstance().getByName(params.get(0));
                if (plugin != null) {
                    if (params.size() > 1) {
                        ZPluginCommand cmd = plugin.getCommand(params.get(1), true);
                        if (cmd != null) {
                            res.setResponse(cmd.getHelp());
                        } else {
                            res.setResponse("Plugin " + plugin.getName() + " doesn't have a command named " + params.get(1));
                        }
                    } else {
                        res.setMultiLine("force");
                        res.setResponse(plugin.getHelp());
                        String commands = "Available commands: ";
                        for (ZPluginCommand c : plugin.getCommands()) {
                            commands += c.getName();
                            if (plugin.getCommands().indexOf(c) < plugin.getCommands().size() - 1) {
                                commands += ", ";
                            }
                        }
                        res.addResponse(commands);
                    }
                } else {
                    res.setResponse("Plugin " + params.get(0) + " does not exist");
                }
            } else {
                res.setResponse("Supply a plugin name, and optionally, a command");
            }
        } else {
            res.setResponse("Unknown command");
        }


        return res;
    }

    private String addPlugin(String url) throws PluginLoaderException {
        ZPlugin plug = PluginRegistry.getInstance().loadRemotePlugin(url);

        ZPlugin currentPlugin = PluginRegistry.getInstance().getByName(plug.getName());
        if (currentPlugin != null) {
            throw new PluginLoaderException("Plugin named " + plug.getName() + " already installed");
        } else {
            PluginRegistry.getInstance().addPlugin(plug);
        }

        return "Added plugin " + plug.getName();
    }

    private String removePlugin(String name) throws PluginLoaderException {
        ZPlugin plug = PluginRegistry.getInstance().getByName(name);

        if (plug != null) {
            if (plug.isSave()) {
                PluginRegistry.getInstance().removePlugin(plug);
            } else {
                throw new PluginLoaderException("Plugin " + name + " cannot be removed");
            }
        } else {
            throw new PluginLoaderException("Plugin " + name + "does not exist");
        }

        return "Removed plugin " + name;
    }

    private String refreshPlugin(String name) throws PluginLoaderException {

        ZPlugin currentPlugin = PluginRegistry.getInstance().getByName(name);
        ZPlugin newPlugin = null;

        if (currentPlugin != null) {
            if (currentPlugin.isSave()) {
                newPlugin = PluginRegistry.getInstance().loadRemotePlugin(currentPlugin.getUrl());

                PluginRegistry.getInstance().removePlugin(currentPlugin);
                PluginRegistry.getInstance().addPlugin(newPlugin);
            } else {
                throw new PluginLoaderException("Plugin " + name + " cannot be refreshed");
            }
        } else {
            throw new PluginLoaderException("No such plugin " + name);
        }

        return "Reloaded plugin " + newPlugin.getName();
    }
}
