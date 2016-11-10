package com.liferay.websocket.whiteboard.standalone.example.feedback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
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
        Server server = new Server(8080);
        
        File tempDir = File.createTempFile("tmp", ".txt").getParentFile();
        
        String resourcesPath = tempDir.getAbsolutePath();
        
        fileFromResource(bundleContext, resourcesPath, "/META-INF/resources", "index.html");
        fileFromResource(bundleContext, resourcesPath, "/META-INF/resources", "css/reset.css");
        fileFromResource(bundleContext, resourcesPath, "/META-INF/resources", "css/style.css");
        fileFromResource(bundleContext, resourcesPath, "/META-INF/resources", "css/bootstrap.min.css");
        fileFromResource(bundleContext, resourcesPath, "/META-INF/resources", "images/avatar-animals.png");
              
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.setBaseResource(Resource.newResource(resourcesPath));
        
        ServletHolder servletHolder = new ServletHolder("default",DefaultServlet.class);
        servletContextHandler.addServlet(servletHolder,"/");
        
        ContextHandlerCollection contextHandlerCollection = new ContextHandlerCollection();
        
        contextHandlerCollection.setHandlers(new Handler[] { servletContextHandler });
            
        server.setHandler(contextHandlerCollection);
    
		WebSocketServerContainerInitializer.configureContext(servletContextHandler).setDefaultMaxSessionIdleTimeout(10000000);
        
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
	
	private void fileFromResource(BundleContext bundleContext, String tempPath, String originalFolder, String path) {
		try {
	        URL url = bundleContext.getBundle().getEntry(originalFolder + "/" + path);
	        	
			System.out.println("Resources " + url);
			
			InputStream is = url.openStream();
			
			File f = new File(tempPath + "/" + path);
			
			f.getParentFile().mkdir();
			
			OutputStream os = new FileOutputStream(f);
			
			byte[] buffer = new byte[1024];
	        int bytesRead;
	        
	        //read from is to buffer
	        while((bytesRead = is.read(buffer)) !=-1){
	            os.write(buffer, 0, bytesRead);
	        }
	        
	        is.close();
	        //flush OutputStream to write any buffered data to file
	        os.flush();
	        os.close();
		} 
		catch (IOException e) {
            e.printStackTrace();
        }

		
	}

}

