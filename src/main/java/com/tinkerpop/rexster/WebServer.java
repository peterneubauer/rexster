package com.tinkerpop.rexster;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.restlet.Component;
import org.restlet.data.Protocol;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class WebServer {

    private static final String DEFAULT_HOST = "";
    private Component component;


    protected static Logger logger = Logger.getLogger(WebServer.class);

    static {
        PropertyConfigurator.configure(RexsterApplication.class.getResource("log4j.properties"));
    }

    public WebServer(final Properties properties, boolean user) throws Exception {
        logger.info(".:Welcome to Rexster:.");
        if (user)
            this.startUser(properties);
        else
            this.start(properties);
    }

    protected void startUser(final Properties properties) throws Exception {
        this.start(properties);
        // user interaction to shutdown server thread
        logger.info("Hit <enter> to shutdown Rexster");
        System.in.read();
        logger.info("Shutting down Rexster");
        this.stop();
        System.exit(0);
    }

    protected void start(final Properties properties) throws Exception {
        RexsterApplication rexster = new RexsterApplication(properties);
        Integer port = new Integer(properties.getProperty("rexster.webserver.port"));

        component = new Component();
        logger.info("Server running on http://localhost:" + port);
        component.getServers().add(Protocol.HTTP, port);
        component.getDefaultHost().attach(DEFAULT_HOST, rexster);
        component.start();
    }

    protected void stop() throws Exception {
        component.stop();
    }

    public static void main(final String[] args) throws Exception {
        Properties properties = new Properties();
        if (args.length == 1) {
            try {
                properties.load(new FileReader(args[0]));
            } catch (IOException e) {
                throw new Exception("Could not locate " + args[0] + " properties file.");
            }
        } else {
            properties.load(RexsterApplication.class.getResourceAsStream("rexster.properties"));
        }

        new WebServer(properties, true);
    }
}