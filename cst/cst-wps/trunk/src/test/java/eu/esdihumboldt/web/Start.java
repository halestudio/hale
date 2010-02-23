package eu.esdihumboldt.web;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class Start {
   

	public static Server server = new Server();
	public static void main(String[] args) throws Exception {
		try {
		SocketConnector connector = new SocketConnector();
		connector.setPort(8080);
		server.setConnectors(new Connector[] { connector });
		WebAppContext context = new WebAppContext();
		context.setServer(server);
		context.setContextPath("/cst-wps");
		context.setWar("src/main/webapp");
		server.addHandler(context);
		
		server.start();
		
		} catch (Exception e) {          
            if (server != null) {
                try {
                    server.stop();
                } catch (Exception e1) {
                   // log.log(Level.SEVERE,
                   //     "Unable to stop the " + "Jetty server:" + e1.getMessage(), e1);
                  throw new RuntimeException(e1);	
                }
            }
        }

	}
}
