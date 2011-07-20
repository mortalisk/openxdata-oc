package org.openxdata.server.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.openxdata.communication.bluetooth.BluetoothServer;
import org.openxdata.communication.bluetooth.BluetoothServerListener;
import org.openxdata.server.FormsServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private Logger log = LoggerFactory.getLogger(FormsBluetoothServer.class);

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
			log.error("Failed while processing connection",e);
			try{
				dos.writeByte(FormsServer.STATUS_FAILURE);
			}catch(IOException ex){
				log.error("Failed to write failure response",ex);
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
