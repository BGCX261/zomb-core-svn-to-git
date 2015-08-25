package zomb.core.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import zomb.core.server.handlers.QueryHandler;
import zomb.core.server.handlers.RootHandler;

/**
 *
 * @author Shrimp
 */
public class ZHttpServer {

    public ZHttpServer(int port) {
        try {
            HttpServer server = HttpServerProvider.provider().createHttpServer(new InetSocketAddress(port), 0);
            server.createContext("/", new RootHandler());
            server.createContext("/query", new QueryHandler());

            server.setExecutor(Executors.newCachedThreadPool());

            server.start();

            System.out.println("HTTP Listening on port " + server.getAddress().getPort());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
