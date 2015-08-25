package zomb.core;

import zomb.core.server.ZHttpServer;
import zomb.core.plugins.PluginRegistry;
import zomb.core.plugins.builtins.Plugin;

/**
 *
 * @author Shrimp
 */
public class ZOMB {

    public static void main(String[] args) {
        /*
         * expected parameters --port: port to listen on
         */
        CommandLine.loadParams(args);

        // set up built-in plugins
        Plugin p = new Plugin();
        PluginRegistry.getInstance().addPlugin(p);

        new ZHttpServer(Integer.valueOf(CommandLine.getParam("port", "8080")));
    }
}
