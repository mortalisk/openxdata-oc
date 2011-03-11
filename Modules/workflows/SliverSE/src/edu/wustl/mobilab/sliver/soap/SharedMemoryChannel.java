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

import edu.wustl.mobilab.sliver.util.*;

/**
 * A {@link Channel} for {@link SharedMemoryTransport}-based communication.
 * 
 * @author Greg Hackmann
 */
public class SharedMemoryChannel extends StreamChannel
{
	/**
	 * The transport's name.
	 */
	private final String name;
	/**
	 * A {@link Queue} for storing incoming messages.
	 */
	private final Queue in;
	/**
	 * A {@link Queue} for storing outgoing messages.
	 */
	private final Queue out;

	/**
	 * Creates a new SharedMemoryChannel.
	 * 
	 * @param name the transport's name
	 * @param in a {@link Queue} for storing incoming messages
	 * @param out a {@link Queue} for storing outgoing messages
	 */
	private SharedMemoryChannel(String name, Queue in, Queue out)
	{
		this.name = name;
		this.in = in;
		this.out = out;
	}

	/**
	 * Creates a new SharedMemoryChannel.
	 * 
	 * @param name the transport's name
	 */
	SharedMemoryChannel(String name)
	{
		this(name, new Queue(), new Queue());
	}
	
	/**
	 * Creates a new SharedMemoryChannel and "connects" it to an existing
	 * channel.
	 * 
	 * @param other the existing channel to "connect" to
	 */
	SharedMemoryChannel(SharedMemoryChannel other)
	{
		this(other.name, other.out, other.in);
	}

	protected InputStream getInputStream() throws IOException
	{
		byte [] message = (byte [])in.pop();
		if(message == null)
			throw new IOException("Connection closed");
		return new ByteArrayInputStream(message);
	}
	
	protected void write(byte [] buffer) throws IOException
	{
		out.push(buffer);
	}
	
	protected void closeImpl() throws IOException
	{
		in.cancel();
		out.cancel();
	}
	
	public String toString()
	{
		return "SharedMemoryChannel(\"" + name + "\")";
	}
}
