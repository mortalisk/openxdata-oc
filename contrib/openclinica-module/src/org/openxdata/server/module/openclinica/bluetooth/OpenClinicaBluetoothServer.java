package org.openxdata.server.module.openclinica.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.fcitmuk.communication.bluetooth.BluetoothServer;
import org.fcitmuk.communication.bluetooth.BluetoothServerListener;
import org.openxdata.server.FormsServer;
import org.openxdata.server.module.openclinica.OpenClinicaServer;


/**
 * 
 * @author daniel
 *
 */
public class OpenClinicaBluetoothServer implements BluetoothServerListener {
	
	/** The logger. */
	//private Log log = LogFactory.getLog(this.getClass());
	private Logger log = Logger.getLogger(this.getClass());

	/** The bluetooth server. */
	private BluetoothServer btServer;
	
	/** The forms server. */
	private OpenClinicaServer openclinicaServer;
	
	
	/** Constructs an xforms bluetooth server instance. */
	public OpenClinicaBluetoothServer(String name, String serverUUID){
		btServer = new BluetoothServer(name,serverUUID,this);
		openclinicaServer = new OpenClinicaServer();
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
	public void processConnection(DataInputStream dis, DataOutputStream dos){
		//It is important that any exceptions which happen in this call are propagated
		//back. If not done, the client will assume success which can be disastrous.
		//For instance if the client was uploading data and this reports success,
		//the client may delete data on the device which was not uploaded successfully
		//to the server.
		try{
			openclinicaServer.processConnection(dis, dos);
		}catch(Exception e){
			log.error(e.getMessage(),e);
			try{
				dos.writeByte(FormsServer.STATUS_FAILURE);
				//dos.writeUTF(e.getMessage());
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
	public void errorOccured(String errorMessage, Exception e){
		log.error(errorMessage, e);
	}
}
