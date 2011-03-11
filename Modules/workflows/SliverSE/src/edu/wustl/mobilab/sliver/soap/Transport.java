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
 * Defines an abstract transport for SOAP calls.
 * Transports are able to synchronously create {@link Channel}s to remote
 * endpoints and accept connections from other endpoints.
 * 
 * <p>In situations where accepting connections does not make sense (e.g.,
 * an HTTP transport on a MIDP device), users should create
 * {@link Channel}s directly instead.</p>
 * 
 * @author Greg Hackmann
 */
public interface Transport
{
	/**
	 * Initiates a connection to a remote destination.
	 * 
	 * @param destination the destination's address; the exact Java type
	 * will depend on the transport (e.g., a <code>Socket</code>-based transport
	 * will use <code>InetSocketAddress</code>es)
	 * @return a {@link Channel} representing the new connection
	 * @throws IOException an I/O error occurred while opening the channel
	 */
	public Channel openChannel(Object destination) throws
		IOException;
	
	/**
	 * Sets the {@link TransportListener} that will be notified of incoming
	 * connections.  This method must be called before calling {@link #open()}.
	 * 
	 * @param listener the {@link TransportListener} that will be notified of
	 * incoming connections
	 */
	public void setTransportListener(TransportListener listener);

	/**
	 * Opens the transport and starts listening for incoming connections.
	 * 
	 * @throws IOException there was an I/O error while opening the transport
	 */
	public void open() throws IOException;
	/**
	 * Closes the transport and stops listening for incoming connections.
	 * 
	 * @throws IOException there was an I/O error while closing the transport
	 */
	public void close() throws IOException;
	/**
	 * Gets whether or not the transport has been closed.
	 * 
	 * @return whetehr or not the transport has been closed
	 */
	public boolean isClosed();
}
