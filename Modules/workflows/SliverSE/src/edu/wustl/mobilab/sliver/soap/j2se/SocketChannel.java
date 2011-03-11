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
 * A {@link Channel} for {@link Socket}-based communication.
 * 
 * @author Greg Hackmann
 */
public class SocketChannel extends StreamChannel
{
	/**
	 * The underlying {@link Socket}.
	 */
	private final Socket socket;
	
	/**
	 * Creates a new SocketChannel.
	 * 
	 * @param socket a {@link Socket} connection to the remote host
	 */
	public SocketChannel(Socket socket)
	{
		this.socket = socket;
	}

	public void closeImpl() throws IOException
	{
		socket.close();
	}

	protected InputStream getInputStream() throws IOException
	{
		DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		String xml = in.readUTF();
		
		return new ByteArrayInputStream(xml.getBytes());
	}
	
	protected void write(byte [] buffer) throws IOException
	{
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		
		out.writeUTF(new String(buffer));
		out.flush();
	}
	
	public String toString()
	{
		return "SocketChannel(" + socket + ")";
	}
}
