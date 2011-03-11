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

/**
 * Helps to implement {@link Transport}s for synchronous connections, like
 * {@link java.net.Socket}s.  The {@link #open()} method creates a new Thread
 * which continously accepts new connections and notifies the
 * {@link TransportListener} when they arrive.
 */
public abstract class SynchronousTransport extends AsynchronousTransport
	implements Runnable 
{	
	/**
	 * Accepts a connection from a remote endpoint.
	 * 
	 * @return a {@link Channel} representing the new connection
	 * @throws IOException an I/O error occurred while opening the channel
	 */
	protected abstract Channel acceptChannel() throws IOException;
	
	public void open()
	{
		new Thread(this, toString()).start();
	}
	
	public void run()
	{
		while(!isClosed())
		{
			try
			{
				Channel channel = acceptChannel();
				listener.newConnection(channel);
//				channel.close();
				// FIXME: do we need to close the channel here?
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		// While the connection is open, accept incoming connections and
		// send them to the listener
	}
}
