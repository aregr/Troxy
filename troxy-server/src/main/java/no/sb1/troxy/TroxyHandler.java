package no.sb1.troxy;

import no.sb1.troxy.http.common.DestinationAddr;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

//Main handler for receiving incoming traffic
public class TroxyHandler extends HandlerWrapper {

    private final Handler simulatorHandler;
    private final List<DestinationAddr> restApiDestinations;

    public TroxyHandler(List<DestinationAddr> restApiDestinations, Handler simulatorHandler) {
        this.restApiDestinations = restApiDestinations != null ? restApiDestinations : Collections.emptyList();
        this.simulatorHandler = simulatorHandler;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String httpUri = baseRequest.getOriginalURI().toLowerCase();
        if (httpUri.toLowerCase().startsWith("https://") || httpUri.startsWith("http://"))
            simulatorHandler.handle(target, baseRequest, request, response); //Always forward proxy-mode (eg. absolute-uri) requests to simulator
        else if (isRestHandlerTarget(baseRequest))
            super.handle(target, baseRequest, request, response); //Send to troxyUIHandlers
        else
            simulatorHandler.handle(target, baseRequest, request, response); //Send remaining to simulator
    }

    private boolean isRestHandlerTarget(Request baseRequest) {
        String hostHeader = baseRequest.getHeader(HttpHeaders.HOST);
        String protocol = baseRequest.getProtocol();
        DestinationAddr destAddr = getDestAddr(protocol.toLowerCase().startsWith("http/") ? "http" : "https", hostHeader);
        String method=baseRequest.getMethod();

        //1. Always use specifiedRestAPIHostnames if specified
        if (restApiDestinations.contains(destAddr)) return true; //Local REST/UI targets

        //2. Attempt heuristic check
        if (baseRequest.getPathInfo().startsWith("/api")) return true; //Looks like local REST
        if ("GET".equals(method)||"HEAD".equals(method)) return true; //Looks like local UI

        return false;
    }

    private DestinationAddr getDestAddr(String protocol, String hostHeader) {
        String[] hostport = hostHeader.split(":", 2);
        if (hostport.length == 1) {
            hostport = new String[]{hostport[0], "http".equals(protocol) ? "80" : "443"};
        }
        return new DestinationAddr(protocol, hostport[0], hostport[1]);
    }
}
