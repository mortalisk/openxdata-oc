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

import org.ksoap2.*;
import org.ksoap2.serialization.*;

/**
 * Defines a centralized location for all things related to SOAP serialization.
 * The only relevant method for end users is
 * {@link #registerClass(String, Class)}, which makes Sliver aware of objects
 * that may be used in SOAP messages.
 * 
 * @author Greg Hackmann
 */
public abstract class Serialization
{
	/**
	 * The supported version of SOAP.
	 */
	private static final int SOAP_VERSION = SoapEnvelope.VER11;
	/**
	 * The "parent" SOAP envelope.
	 * kSOAP stores all class mapping on a per-envelope basis, so we store
	 * all our custom mappings in one centralized envelope and make duplicates
	 * when we need a scratch envelope.
	 */
	private static final CloneableEnvelope parentEnvelope =
		new CloneableEnvelope(SOAP_VERSION);
	// TODO: what about different versions?
	
	/**
	 * Gets the "parent" SOAP envelope that all other envelopes inherit mapping
	 * information from.  Generally, users should use the
	 * {@link #registerClass(String, Class)} or
	 * {@link #registerClass(String, String, Class)} helper method instead of
	 * modifying the envelope's mappings directly.  This method is only
	 * recommended for registering using one of the optional marshallers
	 * included with kSOAP
	 * (e.g., using {@link MarshalBase64#register(SoapSerializationEnvelope)}}.
	 * 
	 * @return the "parent" {@link SoapSerializationEnvelope} whose mapping
	 * information will be inherited by all other envelopes
	 * 
	 *  @see #registerClass(String, Class)
	 *  @see #registerClass(String, String, Class)
	 */
	public static SoapSerializationEnvelope getParentEnvelope()
	{
		return parentEnvelope;
	}

	static
	{
		new CustomPrimitiveMarshal().register(parentEnvelope);
		new MarshalBase64().register(parentEnvelope);
		// Register our custom primitive marshaller and Base64 marshaller
	}
	
	/**
	 * Creates a new scratch envelope for SOAP serialization/deserialization.
	 * This envelope "inherits" the class mappings defined by the user.
	 * 
	 * @return a new scratch {@link SoapSerializationEnvelope}
	 */
	public static SoapSerializationEnvelope newEnvelope()
	{
		return parentEnvelope.duplicate();
	}
	
	/**
	 * Creates a new scratch envelope for DOM serializationh/deserialization.
	 * 
	 * @return a new scratch {@link SoapEnvelope}
	 */
	public static SoapEnvelope newDOMEnvelope()
	{
		return new SoapEnvelope(SOAP_VERSION);
	}
	
	/**
	 * Maps a namespace to the class whose type it defines, using the class's
	 * name as the message type.
	 * This information is used to marshal custom types not defined by
	 * SOAP or XSD.
	 * 
	 * @param namespace the custom type's namespace
	 * @param clazz the custom type's class
	 */
	public static void registerClass(String namespace, Class clazz)
	{
		parentEnvelope.addMapping(namespace, clazz.getName(), clazz);
	}

	/**
	 * Maps a namespace to the class whose type it defines, using a custom
	 * message type.
	 * This information is used to marshal custom types not defined by
	 * SOAP or XSD.
	 * 
	 * @param namespace the custom type's namespace
	 * @param messageType the name of the message type that this class
	 * represents
	 * @param clazz the custom type's class
	 */
	public static void registerClass(String namespace, String messageType,
		Class clazz)
	{
		parentEnvelope.addMapping(namespace, messageType, clazz);
	}
}

/**
 * A {@link SoapSerializationEnvelope} whose sole purpose in life is to let
 * us copy it.  SoapSerializationEnvelope does not define a copy
 * constructor or <code>clone()</code> method, so we have to make our
 * own custom class.
 * 
 * @author Greg Hackmann
 */
class CloneableEnvelope extends SoapSerializationEnvelope
{
	/**
	 * Creates a new envelope.
	 * 
	 * @param version the SOAP version to be used for this envelope
	 * (one of {@link SoapEnvelope#VER10}, {@link SoapEnvelope#VER11}, or
	 * {@link SoapEnvelope#VER12})
	 */
	CloneableEnvelope(int version)
	{
		super(version);
	}
	
	/**
	 * Creates a "duplicate" of the envelope.
	 * Note that this is not a complete duplicate, since we only copy the class
	 * mapping data.  However, that mapping data is all we really care about.
	 * 
	 * @return another envelope with the same class mapping data as this
	 * envelope
	 */
	public SoapSerializationEnvelope duplicate()
	{
		CloneableEnvelope clone = new CloneableEnvelope(version);
		clone.qNameToClass = qNameToClass;
		clone.classToQName = classToQName;
		
		return clone;
	}
}
