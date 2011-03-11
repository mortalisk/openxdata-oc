/*
 *  Licensed to the OpenXdata Foundation (OXDF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The OXDF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, 
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 *  Copyright 2010 http://www.openxdata.org.
 */
package org.openxdata.server.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.openxdata.communication.bluetooth.BluetoothServer; 
import org.openxdata.communication.bluetooth.BluetoothServerListener; 
import org.openxdata.server.FormsServer;

/**
 * The bluetooth server for openxdata. It servers forms, studies, users, locales,
 * and mobile menu text. It relays all the connections to the formsserver by
 * passing the input and output streams over to it.
 * 
 * @author daniel
 *
 */
public class FormsBluetoothServer implements BluetoothServerListener  {
	
	/** The logger. */
	//private Log log = LogFactory.getLog(this.getClass());
	private Logger log = Logger.getLogger(this.getClass());

	/** The bluetooth server. */
	private BluetoothServer btServer;
	
	/** The forms server. */
	private FormsServer formsServer;
	
	
	/** Constructs an xforms bluetooth server instance. */
	public FormsBluetoothServer(String name, String serverUUID){
		btServer = new BluetoothServer(name,serverUUID,this);
		formsServer = new FormsServer();
	}
	
	/** Starts running this bluetooth server. */
	public void start(){
		if(btServer != null)
			btServer.start();
	}
	
	/** Stop this server from running. */
	public void stop(){
		if(btServer != null)
			btServer.stop();
	}
	
	/**
	 * Called when a new connection has been received.
	 * 
	 * @param dis - the stream to read from.
	 * @param dos - the stream to write to.
	 */
	@Override
	public void processConnection(DataInputStream dis, DataOutputStream dos){
		// It is important that any exceptions which happen in this call are propagated
		// back. If not done, the client will assume success which can be disastrous.
		// For instance if the client was uploading data and this reports success,
		// the client may delete data on the device which was not uploaded successfully to the server.
		try{
			formsServer.processConnection(dis, dos);
		}catch(Exception e){
			log.error(e.getMessage(),e);
			try{
				dos.writeByte(FormsServer.STATUS_FAILURE);
			}catch(IOException ex){
				log.error(ex);
			}
		}
	}
		
	/**
	 * Called when an error occurs during processing.
	 * 
	 * @param errorMessage - the error message.
	 * @param e - the exception, if any, that did lead to this error.
	 */
	@Override
	public void errorOccured(String errorMessage, Exception e){
		log.error(errorMessage, e);
	}
}
