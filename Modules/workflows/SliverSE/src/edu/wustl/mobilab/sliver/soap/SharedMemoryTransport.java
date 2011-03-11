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

package edu.wustl.mobilab.sliver.soap;

import java.io.*;
import java.util.*;
import edu.wustl.mobilab.sliver.util.Queue;

/**
 * Sends and receives SOAP requests using message queues in shared memory.
 * <p>
 * Note that this transport only works if both endpoints are hosted on the
 * same Java VM.  Generally,
 * {@link edu.wustl.mobilab.sliver.soap.j2se.SocketTransport} should
 * be used instead, since sockets are often more efficient, and work across
 * VM instances.  However, this transport is useful in situations where
 * requests are handled rapidly, and the underlying OS may exhaust its
 * pool of sockets.
 * </p>
 * 
 * @author Greg Hackmann
 */
public class SharedMemoryTransport extends SynchronousTransport
{
	/**
	 * A list of all existing SharedMemoryTransports.
	 */
	private static final Hashtable transports = new Hashtable();

	/**
	 * A queue of incoming "connections" to the transport.
	 */
	private final Queue connections = new Queue();
	/**
	 * Whether or not the transport has been closed.
	 */
	private boolean closed = false;
	
	/**
	 * The transport's name.
	 */
	private final String name;
	
	/**
	 * Creates a new SharedMemoryTransport.
	 * 
	 * @param name the transport's name
	 */
	public SharedMemoryTransport(String name)
	{
		this.name = name;
		transports.put(name, this);
	}
	
	protected Channel acceptChannel() throws IOException
	{
		SharedMemoryChannel channel = (SharedMemoryChannel)connections.pop();
		if(channel == null)
			throw new IOException();
		// Wait for an incoming connection, and complain if the queue was
		// cancelled (i.e., the transport was closed)
		
		return channel;
	}

	public void close() throws IOException
	{
		closed = true;
		connections.cancel();
	}

	public boolean isClosed()
	{
		return closed;
	}

	/**
	 * Notifies the transport when a new "connection" is made.
	 * 
	 * @param client a {@link SharedMemoryChannel} representing the new
	 * connection
	 */
	private void newConnection(SharedMemoryChannel client)
	{
		SharedMemoryChannel server = new SharedMemoryChannel(client);
		connections.push(server);
		// Create a local counterpart to this connection, then push it
		// onto the new connections queue
	}

	/**
	 * Initiates a connection to an existing SharedMemoryTransport.
	 * 
	 * @param name the name of the destination SharedMemoryTransport
	 * @return a {@link Channel} representing the new connection
	 */
	public static Channel openChannel(String name)
	{
		SharedMemoryChannel channel = new SharedMemoryChannel(name);
		((SharedMemoryTransport)transports.get(name)).newConnection(channel);
		// Create a new channel, and notify the transport that we're
		// "connecting" to it
		
		return channel;		
	}

	public Channel openChannel(Object destination) throws IOException
	{
		return openChannel((String)destination);
	}

	public String toString()
	{
		return "SharedMemoryTransport(\"" + name + "\")";
	}
}
