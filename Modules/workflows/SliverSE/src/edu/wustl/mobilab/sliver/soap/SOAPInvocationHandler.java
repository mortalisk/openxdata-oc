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

import org.ksoap2.serialization.*;

/**
 * Defines how SOAP calls should be handled.  Users should almost always use
 * {@link edu.wustl.mobilab.sliver.soap.j2se.MethodCallHandler} on J2SE and
 * {@link edu.wustl.mobilab.sliver.soap.midp.MethodCallHandler} on MIDP.
 * 
 * @author Greg Hackmann
 */
public interface SOAPInvocationHandler
{
	/**
	 * Handles a new incoming SOAP request.
	 * 
	 * @param request the incoming request
	 * @return the result of handling the SOAP request; the result is
	 * <tt>null</tt> if and only if the request is one-way
	 * 
	 * @throws IllegalArgumentException the SOAP request could not be
	 * understood
	 * @throws UnserializableException while handling the request, a
	 * non-serializable exception was thrown
	 * @throws SerializableException while handling the request, a
	 * serializable exception was thrown
	 * 
	 * @see UnserializableException
	 * @see SerializableException
	 */
	public abstract Object handleInvocation(SoapObject request) throws
		IllegalArgumentException, UnserializableException,
		SerializableException;
}
