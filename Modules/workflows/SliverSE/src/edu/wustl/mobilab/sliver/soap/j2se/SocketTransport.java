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

import edu.wustl.mobilab.sliver.soap.*;

/**
 * Sends and receives SOAP requests over raw sockets.
 * 
 * @author Greg Hackmann
 */
public class SocketTransport extends SynchronousTransport
{
	/**
	 * The underlying {@link ServerSocket} that accepts incoming connections.
	 */
	private final ServerSocket server;
	
	/**
	 * Whether or not the transport has been closed.
	 */
	private boolean closed = false;

	/**
	 * Creates a new SocketTransport.
	 * 
	 * @param port the port to listen to for SOAP requests
	 * @throws IOException an I/O error occurred while opening the transport
	 */
	public SocketTransport(int port) throws IOException
	{
		server = new ServerSocket(port);
	}
	
	protected Channel acceptChannel() throws IOException
	{
		Socket socket = server.accept();
		return new SocketChannel(socket);
	}
	
	/**
	 * Initiates a connection to a remote SocketTransport.
	 * 
	 * @param destination a {@link Endpoint} representing the
	 * remote endpoint
	 * @return a {@link Channel} representing the new connection
	 * @throws IOException an I/O error occurred while opening the channel
	 */
	public Channel openChannel(Object destination) throws IOException
	{
		Endpoint endpoint = (Endpoint)destination;
		Socket socket = new Socket(endpoint.address, endpoint.port);
		return new SocketChannel(socket);
	}
	
	public void close() throws IOException
	{
		closed = true;
		server.close();
	}
	
	public boolean isClosed()
	{
		return closed;
	}
	
	public String toString()
	{
		return "SocketTransport(" + server.getLocalPort() + ")";
	}

	/**
	 * A description of remote socket endpoints.
	 */
	public static final class Endpoint
	{
		/**
		 * The destination address.
		 */
		final InetAddress address;
		/**
		 * The destination port.
		 */
		final int port;

		/**
		 * Creates a new socket Endpoint.
		 * 
		 * @param address the destination address
		 * @param port the destination port
		 */
		public Endpoint(InetAddress address, int port)
		{
			this.address = address;
			this.port = port;
		}
		
		/**
		 * Creates a new socket Endpoint, using an unresolved host name.
		 * 
		 * @param address the destination host's name
		 * @param port the destination port
		 * 
		 * @throws UnknownHostException the remote host could not be resolved
		 */
		public Endpoint(String address, int port) throws
			UnknownHostException
		{
			this(InetAddress.getByName(address), port);
		}
	}
}
