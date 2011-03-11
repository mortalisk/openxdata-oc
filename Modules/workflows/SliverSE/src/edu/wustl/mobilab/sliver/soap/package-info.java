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

/**
 * Provides a SOAP server and associated helper classes.
 * 
 * <p>
 * To use this package, it is recommended to follow this basic outline:
 * </p>
 * 
 * <ol>
 * <li>Pick at least one namespace for your services.  There's no restrictions
 * on how the namespaces are named, as long as they are unique and in the form
 * of a URL (e.g., "http://example.com/SomeNamespace").  Typically, related
 * services are grouped together under the same namespace, although this is not
 * required.</li>
 * 
 * <li>Create a class for each namespace.  (The class's name does not
 * necessarily have to be related to the namespace.)  For each service in
 * the namespace, add a <b>public static</b> method with the service's name.
 * Note that these methods <i>must</i> return an object that SOAP can
 * serialize, as described below.  For example:</li>
 * 
 * <blockquote><pre>// Service for namespace "http://some/Namespace"
 *public class MyService
 *{
 *    public static MyCustomType someService(...)
 *    {
 *        ...
 *        return new MyCustomType(...);
 *    }
 *}</pre></blockquote>
 *
 * <li>If you use any parameters that aren't defined by XSD (i.e., basically
 * anything besides primitives, arrays, and Strings), then create an object
 * for each of these parameter types.  These classes must implement
 * {@link org.ksoap2.serialization.KvmSerializable} and must have a constructor
 * which takes no parameters.  The easiest way to do this is to extend the
 * {@link org.ksoap2.serialization.SoapObject} helper class, like so:
 * 
 * <blockquote><pre>
 *public class MyCustomType extends SoapObject
 *{
 *    public MyCustomType()
 *    {
 *        this(...);
 *    }
 *    
 *    public MyCustomType(...)
 *    {
 *        super("http://some/Namespace", "MyCustomType");
 *        addProperty("somePropertyName", firstParameter);
 *        ...
 *    }
 *}</pre></blockquote></li>
 * 
 * <li>(optional) If your services throw any faults, then create classes for
 * these faults.  These exceptions should extend
 * {@link edu.wustl.mobilab.sliver.soap.SerializableException}
 * and have a constructor which takes no parameters.  For example:
 * 
 * <blockquote><pre>
 *public class MyCustomFault extends SerializableException
 *{
 *    public MyCustomFault()
 *    {
 *        this(...);
 *    }
 *    
 *    public MyCustomFault(...)
 *    {
 *        super("http://some/Namespace", "MyCustomFault");
 *        addProperty("someChildName", firstChild);
 *        ...
 *    }
 *}</pre></blockquote>
 *
 * Extending SerializableException is not completely necessary, since the SOAP
 * service will handle other exceptions in a generic fashion.  However, 
 * exceptions which do not extend SerializableException will be serialized as
 * a simple string which preserves little semantic information.</li>
 * 
 * <li>Pick a {@link edu.wustl.mobilab.sliver.soap.Transport}.  Usually, you
 * will want to use {@link edu.wustl.mobilab.sliver.soap.j2se.SocketTransport}
 * or {@link edu.wustl.mobilab.sliver.soap.j2se.JettyTransport}.  
 * <code>SocketTransport</code> uses raw sockets, which are lighter weight.
 * <code>JettyTransport</code> uses HTTP, which is heavier-weight than
 * raw sockets, but is compatible with more third-party software.</li>
 * 
 * <li>Register your custom message types with the
 * {@link edu.wustl.mobilab.sliver.soap.Serialization} class, like so:</li>
 * <blockquote><pre>
 *...
 *Serialization.registerClass("http://some/Namespace", MyCustomType.class);
 *...
 * </pre></blockquote>
 * 
 * <li>Instantiate a {@link edu.wustl.mobilab.sliver.soap.SOAPServer} and
 * register your services with it:</li>
 * <blockquote><pre>
 *...
 *Transport transport = ...;
 *SOAPServer server = new SOAPServer(transport);
 *server.registerService("http://some/Namespace", new MethodCallHandler(MyService.class));
 *...
 *server.start();
 * </pre></blockquote>
 *
 * <li>(optional) Create a WSDL document that describes your services.  Sliver
 * does not need this to run, but many SOAP clients require it.</li>
 * </ol>
 * 
 * <p>Note that some of these steps are a little different for J2ME.  See the
 * documentation for {@link edu.wustl.mobilab.sliver.soap.midp} for
 * more information.</p>
 * 
 * @see edu.wustl.mobilab.sliver.soap.SOAPServer
 * @see edu.wustl.mobilab.sliver.soap.Serialization
 * @see edu.wustl.mobilab.sliver.soap.SerializableException
 * @see edu.wustl.mobilab.sliver.soap.Transport
 */
package edu.wustl.mobilab.sliver.soap;