package zomb.core.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Shrimp
 */
public class PluginExecutor {

    public List<PluginResponse> executeCommand(String sourceName, String senderName, String query, boolean addressedDirectly) {
        List<PluginResponse> res = new ArrayList();

        /*
         * try to break up the command first...
         */
        List<String> queryParts = parseCommand(query);

        /*
         * identify basic plugin match first
         */
        ZPlugin exactPlugin = PluginRegistry.getInstance().getByName(queryParts.get(0));

        /*
         * if there's a command or some sort of parameter attached
         */
        if (exactPlugin != null && queryParts.size() >= 2) {

            /*
             * see if there is a command ("plugin command")
             */
            ZPluginCommand exactCmd = exactPlugin.getCommand(queryParts.get(1), addressedDirectly);

            /*
             * execute the neatly matching command
             */
            if (exactCmd != null) {
                res.add(exactPlugin.processQuery(sourceName, senderName, exactCmd.getName(),
                        queryParts.subList(2, queryParts.size()), query, addressedDirectly));
            } else {

                /*
                 * no matching command check if the params match a regex in this
                 * plugin
                 */
                ZPluginCommand regCmd = exactPlugin.getCommandPattern(query.replaceFirst(queryParts.get(0), "").trim(), addressedDirectly);

                /*
                 * there's a command with a regex, execute that
                 */
                if (regCmd != null) {
                    res.add(exactPlugin.processQuery(sourceName, senderName, regCmd.getName(),
                            queryParts.subList(2, queryParts.size()), query, addressedDirectly));
                } else {

                    /*
                     * no command, and no regex, see if there's a default
                     * command (same name as the plugin)
                     */
                    ZPluginCommand defaultCmd = exactPlugin.getCommand(queryParts.get(0), addressedDirectly);
                    if (defaultCmd != null) {
                        res.add(exactPlugin.processQuery(sourceName, senderName, defaultCmd.getName(),
                                queryParts.subList(1, queryParts.size()), query, addressedDirectly));
                    }
                }
            }
        }

        /*
         * basic command execution done. see if there are any matching regexes
         * some plugins may swallow commands, to prevent regexes consuming them
         */
        if (exactPlugin == null || (exactPlugin != null && !exactPlugin.isSwallowCommand())) {
            for (ZPlugin p : PluginRegistry.getInstance().getPlugins()) {
                /*
                 * only allow one response per plugin...?
                 */
                if (!hasPluginResponse(res, p.getName())) {
                    ZPluginCommand regCmd = p.getCommandPattern(query.trim(), addressedDirectly);
                    if (regCmd != null) {
                        res.add(p.processQuery(sourceName, senderName, regCmd.getName(),
                                queryParts.get(0).equalsIgnoreCase(regCmd.getName()) ? queryParts.subList(1, queryParts.size()) : queryParts,
                                query, addressedDirectly));
                    }
                }
            }
        }

        return res;
    }

    private boolean hasPluginResponse(List<PluginResponse> responses, String plugin) {
        boolean res = false;

        for (PluginResponse r : responses) {
            if (r.getPlugin().equalsIgnoreCase(plugin)) {
                res = true;
                break;
            }
        }

        return res;
    }

    /*
     * stolen from the intetwebs, thanks guy
     * http://stackoverflow.com/questions/366202/regex-for-splitting-a-string-using-space-when-not-surrounded-by-single-or-double
     */
    private List<String> parseCommand(String command) {
        List<String> res = new ArrayList<String>();
        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        Matcher regexMatcher = regex.matcher(command);
        while (regexMatcher.find()) {
            if (regexMatcher.group(1) != null) {
                // Add double-quoted string without the quotes
                res.add(regexMatcher.group(1));
            } else if (regexMatcher.group(2) != null) {
                // Add single-quoted string without the quotes
                res.add(regexMatcher.group(2));
            } else {
                // Add unquoted word
                res.add(regexMatcher.group());
            }
        }

        return res;
    }
}
