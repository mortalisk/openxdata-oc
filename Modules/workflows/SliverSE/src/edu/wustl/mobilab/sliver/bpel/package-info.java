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
 * Provides a BPEL server and associated helper classes.
 * 
 * <p>
 * To use the BPEL server, it is recommended to follow these basic steps:
 * </p>
 * 
 * <ol>
 * <li>Pick a unique namespace and name for each process.  Create one or more
 * .bpel process specifications using these names and namespaces.</li>
 * 
 * <li>Create a {@link edu.wustl.mobilab.sliver.soap.Transport}, as described in
 * the documentation for {@link edu.wustl.mobilab.sliver.soap}.</lI>
 *
 * <li>Instantiate a {@link edu.wustl.mobilab.sliver.bpel.BPELServer} and
 * register your processes with it:</li>
 * <blockquote><pre>
 *...
 *Transport transport = ...;
 *BPELServer server = new BPELServer(transport);
 *server.addProcess("http://some/Namespace", new FileInputStream("SomeProcess.bpel"));
 *...
 *</blockquote></pre>
 *
 * <li>Bind your process' incoming links by specifing which name/namespace
 * combination refers to which link:</li>
 * <blockquote><pre>
 *...
 *server.bindIncomingLink("ClientLink", "http://some/Namespace", "executeSomeProcess");
 *...
 *</blockquote></pre>
 * 
 * <li>Bind outgoing links to concrete endpoints:</li>
 * <blockquote><pre>
 *...
 *Binding binding = ...;
 *server.bindOutgoingLink("ExternalSOAPServerLink", binding);
 *...
 * </pre></blockquote>
 * 
 * <li>Start the server:</li>
 * <blockquote><pre>
 *...
 *server.start();
 *...
 *</pre></blockquote>
 *
 * <li>(optional) Create a WSDL document that describes your processes.  Sliver
 * does not need this to run, but many SOAP clients require it.</li>
 * </ol>
 * 
 * <p>Note that, unlike when using Sliver's SOAP server, you do not need to
 * create Java types corresponding to your custom message types.</p>
 * 
 * @see edu.wustl.mobilab.sliver.bpel.BPELServer
 * @see edu.wustl.mobilab.sliver.soap
 */
package edu.wustl.mobilab.sliver.bpel;