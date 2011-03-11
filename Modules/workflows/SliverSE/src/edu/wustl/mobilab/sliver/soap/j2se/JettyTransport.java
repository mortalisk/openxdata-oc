/* Sliver - a BPEL execution engine for lightweight and mobile devices.
 * Copyright (C) 2006, Washington University in Saint Louis
 * By Gregory Hackmann.
 *
 * Washington University states that Sliver is free software;
 * you can redistribute it and/or modify it under the terms of
 * the current version of the GNU Lesser General Public License
 * as published by the Free Software Foundation.
 *
 * Sliver is distributed in the hope that it will be useful, but
 * THERE ARE NO WARRANTIES, WHETHER ORAL OR WRITTEN, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO, IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR USE.
 *
 * YOU UNDERSTAND THAT SLIVER IS PROVIDED "AS IS" FOR WHICH NO
 * WARRANTIES AS TO CAPABILITIES OR ACCURACY ARE MADE. THERE ARE NO
 * WARRANTIES AND NO REPRESENTATION THAT SLIVER IS FREE OF
 * INFRINGEMENT OF THIRD PARTY PATENT, COPYRIGHT, OR OTHER
 * PROPRIETARY RIGHTS.  THERE ARE NO WARRANTIES THAT SOFTWARE IS
 * FREE FROM "BUGS", "VIRUSES", "TROJAN HORSES", "TRAP DOORS", "WORMS",
 * OR OTHER HARMFUL CODE.
 *
 * YOU ASSUME THE ENTIRE RISK AS TO THE PERFORMANCE OF SOFTWARE AND/OR
 * ASSOCIATED MATERIALS, AND TO THE PERFORMANCE AND VALIDITY OF
 * INFORMATION GENERATED USING SOFTWARE. By using Sliver you agree to
 * indemnify, defend, and hold harmless WU, its employees, officers and
 * agents from any and all claims, costs, or liabilities, including
 * attorneys fees and court costs at both the trial and appellate levels
 * for any loss, damage, or injury caused by your actions or actions of
 * your officers, servants, agents or third parties acting on behalf or
 * under authorization from you, as a result of using Sliver.
 *
 * See the GNU Lesser General Public License for more details, which can
 * be found here: http://www.gnu.org/copyleft/lesser.html
 */

package edu.wustl.mobilab.sliver.soap.j2se;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.http.*;

import org.mortbay.jetty.*;
import org.mortbay.jetty.handler.*;
import org.mortbay.jetty.nio.*;

import edu.wustl.mobilab.sliver.soap.*;

/**
 * Sends and receives SOAP requests over HTTP, using the Jetty HTTP library.
 * <p>
 * Note that Jetty automatically adds a forward-slash to the end of the paths
 * given to {@link #JettyTransport(int, String) the constructor} (e.g.,
 * <code>new JettyTransport(9000, "/path")</code> corresponds to
 * <code>http://localhost:9000/path/</code> instead of
 * <code>http://localhost:9000/path</code>).  In principle, if the client
 * omits the trailing slash, then it will still work: Jetty automatically
 * sends an <code>HTTP FOUND</code> response redirecting the client to the
 * correct location.  Unfortunately, some HTTP clients don't handle this
 * response correctly; for example, Java's {@link HttpURLConnection} will
 * reconnect to the correct location, but will omit the SOAP payload when it
 * resends the request.  So, it's safer to simply manually add the trailing
 * slash on the client's side. 
 * 
 * @author Greg Hackmann
 */
public class JettyTransport extends AsynchronousTransport
{
	/**
	 * A mapping from port numbers to the {@link Server}s that handle them.
	 */
	private static final Hashtable servers = new Hashtable();
	/**
	 * Gets the {@link Server} that handlers a particular port.  If one does
	 * not already exist, then it is created.
	 * 
	 * @param port the TCP port to get the server for
	 * @return the {@link Server} that handles the specified port
	 */
	private static Server getServer(int port)
	{
		Integer key = new Integer(port);
		if(servers.containsKey(key))
			return (Server)servers.get(key);
		// If the server exists, then just return it
		
		Server server = new Server();
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		server.setHandler(contexts);
		
		Connector connector = new SelectChannelConnector();
		connector.setPort(port);
		server.addConnector(connector);
		servers.put(key, server);
		// Otherwise, create a new server and hook it up to the appropriate port
		
		return server;
	}
	
	/**
	 * The Jetty {@link Server} that is handling this transport's HTTP
	 * connections.
	 */
	private final Server server;
	/**
	 * The Jetty {@link ContextHandler} that is handling this transport's
	 * context.
	 */
	private final ContextHandler context;
	
	/**
	 * The {@link Handler} that handles incoming requests by
	 * forwarding them to the listener.
	 */
	private final Handler handler = new AbstractHandler()
	{
		public void handle(String target, HttpServletRequest request,
			HttpServletResponse response, int type) throws IOException
		{
			JettyChannel channel = new JettyChannel(request, response);
			listener.newConnection(channel);
			channel.waitToClose();
		}
	};
	
	/**
	 * Creates a new JettyTransport.
	 * <p>
	 * <i>NOTE:</i> be sure to read {@link JettyTransport this warning}
	 * about the way Jetty handles paths.
	 * 
	 * @param port the port to listen to for HTTP requests
	 * @param path the "path" on the server (e.g., "/" or "/serviceName") that
	 * requests will try to access
	 * @throws Exception the Jetty server threw an Exception
	 */
	public JettyTransport(int port, final String path) throws Exception
	{
		server = getServer(port);
	
		context = new ContextHandler();
		context.setContextPath(path);
		ContextHandlerCollection contexts =
			(ContextHandlerCollection)server.getHandler();
		contexts.addHandler(context);
		// Create an HTTP context for the given path and add it to the
		// server's context handlers collection
	}
	
	/**
	 * Opens an outgoing HTTP channel.
	 * 
	 * @param destination a {@link Endpoint} describing the destination host
	 * @return a {@link Channel} representing the new connection
	 * @throws IOException an I/O error occurred while opening the channel
	 */
	public Channel openChannel(Object destination) throws IOException
	{
		Endpoint endpoint = (Endpoint)destination;
		HttpURLConnection connection =
			(HttpURLConnection)endpoint.url.openConnection();
		return new HTTPChannel(connection, endpoint.action);
		// Open an HTTP channel to the endpoint
	}
	
	public void open() throws IOException
	{
		context.setHandler(handler);
		// Add our custom handler to the context
		
		if(!server.isStarted())
		{
			try
			{
				server.start();
			}
			catch(Exception e)
			{
				throw new IOException("Could not start Jetty server; caused by " + e);
			}
		}
		// Start the server, if needed
	}
	
	public void close() throws IOException
	{
		try
		{
			server.stop();
		}
		catch(Exception e)
		{
			throw new IOException("Could not stop Jetty server; caused by " + e);
		}
	}
	
	public boolean isClosed()
	{
		return !server.isRunning();
	}
	
	/**
	 * A description of remote SOAP-over-HTTP endpoints.
	 */
	public static class Endpoint
	{
		/**
		 * The URL of the remote server.
		 */
		final URL url;
		/**
		 * The remote server's SOAP action
		 */
		final String action;

		/**
		 * Creates a new SOAP-over-HTTP Endpoint.
		 * 
		 * @param url the destination, described in the form
		 * <code>http://server.name:port/path</code>
		 * @param action the SOAP action
		 * 
		 * @throws MalformedURLException the URL could not be understood
		 */
		public Endpoint(String url, String action)
			throws MalformedURLException
		{
			this.url = new URL(url);
			this.action = action;
		}
		
		public String toString()
		{
			return url.toString();
		}
	}
	
	public String toString()
	{
		return "JettyTransport(http://localhost:"
			+ server.getConnectors()[0].getPort() + context.getContextPath()
			+ ")";
	}
}
