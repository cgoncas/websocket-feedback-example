package com.liferay.websocket.whiteboard.standalone.example.feedback;

import java.io.File;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.ServletContext;
import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.Endpoint;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.liferay.websocket.whiteboard.standalone.example.feedback.chat.MessageDecoder;
import com.liferay.websocket.whiteboard.standalone.example.feedback.chat.MessageEncoder;

import org.eclipse.jetty.server.Handler;

import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

public class BundleActivator implements org.osgi.framework.BundleActivator {
	private ServiceRegistration<ServletContext> servletContextServiceRegistration;
	
	private ServiceRegistration<Server> serverServiceRegistration;
	
	private ServiceRegistration<Endpoint> websocketServiceRegistration;
	
	@Override
	public void start(BundleContext bundleContext) throws Exception {
        Server server = new Server(8090);
        
        String resourcesPath = new File("./resources/META-INF/resources").getAbsolutePath();
        
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.setBaseResource(Resource.newResource(resourcesPath));
        
        ServletHolder servletHolder = new ServletHolder("default",DefaultServlet.class);
        servletContextHandler.addServlet(servletHolder,"/");
        
        ContextHandlerCollection contextHandlerCollection = new ContextHandlerCollection();
        
        contextHandlerCollection.setHandlers(new Handler[] { servletContextHandler });
            
        server.setHandler(contextHandlerCollection);
    
		WebSocketServerContainerInitializer.configureContext(servletContextHandler);
        
		Dictionary<String, Object> serverProps = new Hashtable<String, Object>();
        serverProps.put("managedServerName", "websocket-server");

        serverServiceRegistration = 
        		bundleContext.registerService(Server.class, server, serverProps);
        
        ServletContext servletContext = servletContextHandler.getServletContext().getContext("/");
        
        Dictionary<String, Object> servletContextProps = new Hashtable<String, Object>();
        servletContextProps.put("websocket.active", Boolean.TRUE);
        
        servletContextServiceRegistration =  
        		bundleContext.registerService(ServletContext.class, servletContext, servletContextProps);
        
		Dictionary<String, Object> properties = new Hashtable<>();

		List<Class<? extends Decoder>> decoders = new ArrayList<>();

		decoders.add(MessageDecoder.class);

		properties.put("org.osgi.http.websocket.endpoint.decoders", decoders);

		List<Class<? extends Encoder>> encoders = new ArrayList<>();
		
		encoders.add(MessageEncoder.class);

		properties.put("org.osgi.http.websocket.endpoint.encoders", encoders);

		properties.put(
			"org.osgi.http.websocket.endpoint.path", "/websocket/chat");

		websocketServiceRegistration = bundleContext.registerService(
			Endpoint.class, new ChatWebSocketEndpoint(), properties);
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {	
		bundleContext.ungetService(serverServiceRegistration.getReference());
		
		bundleContext.ungetService(servletContextServiceRegistration.getReference());		
		
		bundleContext.ungetService(websocketServiceRegistration.getReference());	
	}	

}

