package zomb.core.server.handlers;

import zomb.core.server.ZHttpHandler;
import zomb.core.server.ZHttpRequest;

/**
 *
 * @author Shrimp
 */
public class RootHandler extends ZHttpHandler {

    @Override
    protected HttpResponse handleRequest(ZHttpRequest req) {
        return new HttpResponse("text/html", "<h2>Nothing to see here</h2>");
    }
}
