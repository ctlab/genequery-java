package com.genequery.rest;

import com.genequery.rest.endpoints.FisherSearcherEndPoint;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by Arbuzov Ivan.
 */
public class RestServer {
    private static final Logger LOG = LoggerFactory.getLogger(RestServer.class);

    private static Server webServer;

    public void start(int port) throws Exception {
        webServer = new Server();
        LOG.info("Starting EmbeddedServer...");

        ServerConnector http = new ServerConnector(webServer);
        http.setReuseAddress(true);
        http.setHost("localhost");
        http.setPort(port);
        http.setIdleTimeout(30000);
        webServer.addConnector(http);

        ServletContextHandler context = new ServletContextHandler(
                webServer, "/", ServletContextHandler.NO_SESSIONS);

        ServletHolder restServletHolder = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/gqrest/*");
        restServletHolder.setInitOrder(0);

        // Tells the Jersey Servlet which REST service/class to load.
        restServletHolder.setInitParameter(
                "jersey.config.server.provider.classnames", FisherSearcherEndPoint.class.getCanonicalName());


        webServer.start();
        webServer.join();

        LOG.info("Started EmbeddedServer");
    }
}
