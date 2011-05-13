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
package org.openxdata.server.sms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.fcitmuk.communication.sms.SMSServer;
import org.fcitmuk.communication.sms.SmsProcessMessageResponse;
import org.openxdata.server.FormsServer;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormSmsArchive;
import org.openxdata.server.admin.model.FormSmsError;
import org.openxdata.server.service.FormDownloadService;
import org.openxdata.server.service.SettingService;
import org.smslib.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class serves as the sms server for OpenXdata. 
 * It handles both text and binary sms.
 * 
 * @author Tumwebaze
 * @author daniel
 *
 */
@Component
public class FormSmsServer implements org.fcitmuk.communication.sms.SMSServerListener{

	@Autowired
	private SettingService settingService;
	
	@Autowired
	private FormDownloadService formDownloadService;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	/** The sms engine. */
	private org.fcitmuk.communication.sms.SMSServer smsServer;
	
	@Autowired
	private FormsServer formsServer;
	
	@Autowired
	private FormSmsParser formsSmsParser;

	/** Flag to determine whether the sms sender wants success reports. */
	private boolean smsSendSuccessReports = true;
	
	/** Flag to determine if the sms sender wants failure reports. */
	private boolean smsSendFailureReports = true;
	
	private String gatewayId,comPort,modemManufacturer, modemModel;
	
	private int srcPort, dstPort,baudRate;
	
	/** Flag determining whether to use polling for inbound messages.
	 * This is useful for modems like HUAWEI E220 whose service indications
	 * do not work for inbound message notifications.
	 */
	private boolean useInboundPolling = false;
	
	/** The sleep interval for each polling for inbound messages. */
	private int inboundPollingInterval = 30000;
	
	//Response messages
	private String MSG_RECEIVE_PROCESS_SUCCESS = "Message received and processed sucessfully.";
	private String MSG_PROCESS_FAILURE = "Errors occured while processing message on the server. Please report this error to the administrator.";
	
	public FormSmsServer() {
    }

	/**
	 * Constructs a new instance of the forms sms server.
	 * 
	 * @param gatewayId the idenfifier for this sms gateway. This is only for display purposes and so can be anything.
	 * @param comPort the com port at which the modem is running.
	 * @param srcPort the port from which smses originate.
	 * @param dstPort the port at which sms should be sent for java midlets.
	 * @param baudRate the modem baud rate.
	 * @param modemManufacturer the manufacturer of the modem. This is also for only display purposes.
	 * @param modemModel the modem model. This is also for display purposes only.
	 */
	public FormSmsServer(String gatewayId, String comPort, int srcPort, int dstPort, int baudRate, 
			String modemManufacturer, String modemModel,boolean useInboundPolling, 
			int inboundPollingInterval) {
		this();
		smsServer = new SMSServer(gatewayId,comPort, srcPort, dstPort, 
				baudRate,modemManufacturer, modemModel, useInboundPolling, inboundPollingInterval, this);
	}

	public String getGatewayId(){
		return this.gatewayId;
	}

	public void setGatewayId(String gatewayId){
		this.gatewayId = gatewayId;
	}

	public String getComPort(){
		return this.comPort;
	}

	public void setComPort(String comPort){
		this.comPort = comPort;
	}

	public int getSourcePort(){
		return this.srcPort;
	}

	public void setSourcePort(int srcPort){
		this.srcPort = srcPort;
	}

	public int getDestinationPort(){
		return this.dstPort;
	}
	
	public void setDestinationPort(int dstPort){
		this.dstPort = dstPort;
	}

	public int getBaudRate(){
		return this.baudRate;
	}
	
	public void setBaudRate(int baudRate){
		this.baudRate = baudRate;
	}
	
	public String getModemManufacturer(){
		return this.modemManufacturer;
	}

	public void setModemManufacturer(String modemManufacturer){
		this.modemManufacturer = modemManufacturer;
	}

	public String getModemModel(){
		return this.modemModel;
	}

    public void setModemModel(String modemModel){
		this.modemModel = modemModel;
	}
	
	public boolean isUseInboundPolling() {
		return useInboundPolling;
	}
	
	public void setUseInboundPolling(boolean useInboundPolling) {
		this.useInboundPolling = useInboundPolling;
	}
	
	public int getInboundPollingInterval() {
		return inboundPollingInterval;
	}
	
	public void setInboundPollingInterval(int inboundPollingInterval) {
		this.inboundPollingInterval = inboundPollingInterval;
	}
	
	
	/**
	 * Starts running the sms server.
	 * 
	 */
	public void initialize() {
		formsSmsParser.init();
    }

	
	/**
	 * Stops the running of the sms server.
	 */
	public void stop(){
		if(smsServer != null){
			smsServer.stop();
		}
	}

	/**
	 * This method is called by the sms engine when a new binary sms comes in.
	 * 
	 * @param dis the stream from which to read the message.
	 * @param dos the stream to write to for a reply.
	 */
	@Override
	public void processMessage(DataInputStream dis, DataOutputStream dos) {
		//It is important that any exceptions which happen in this call are propagated
		//back. If not done, the client will assume success which can be disastrous.
		//For instance if the client was uploading data and this reports success,
		//the client may delete data on the device which was not uploaded successfully
		//to the server.
		try{
			formsServer.processConnection(dis, dos);
		}catch(Exception e){
			//			if(e != null && log != null)
			//				log.error(e.getMessage(),e);
			//			else
			log.error(e.getLocalizedMessage(), e);

			try{
				dos.writeByte(FormsServer.STATUS_FAILURE);
				//dos.writeUTF(e.getMessage());
			}catch(IOException ex){
				//				if(log != null && ex != null)
				//					log.error(ex);
				//				else
				log.error(e.getLocalizedMessage(), e);
			}
		}	
	}

	/**
	 * This method is called by the sms engine when a new text sms comes in.
	 * 
	 * @param sender the phone number sending the sms.
	 * @param text the text contents of the sms.
	 */
	@Override
	public SmsProcessMessageResponse processMessage(String sender, String text, Service srv){
		SmsProcessMessageResponse reply = new SmsProcessMessageResponse();

		try{
			
			loadSettings();
			
			FormData formData = formsSmsParser.sms2FormData(sender, text);
			formDownloadService.saveFormData(formData);

			FormSmsArchive formSmsArchive = new FormSmsArchive(formData);
			formSmsArchive.setSender(sender);
			formSmsArchive.setData(text);
			formSmsArchive.setArchiveCreator(formData.getCreator());
			formSmsArchive.setArchiveDateCreated(formData.getDateCreated());

			formDownloadService.saveFormSmsArchive(formSmsArchive);

			if(smsSendSuccessReports){
				reply.setStatus(SmsProcessMessageResponse.RESPONSE_STATUS_OK);
				reply.setReponseMessage(MSG_RECEIVE_PROCESS_SUCCESS);
			}
		}
		catch(Exception ex){
			log.error(ex.getLocalizedMessage(), ex);	

			reply.setStatus(SmsProcessMessageResponse.RESPONSE_STATUS_ERR);
			if(smsSendFailureReports){
				reply.setReponseMessage(ex.getMessage());
				if(reply.getResponseMessage() == null)
					reply.setReponseMessage(MSG_PROCESS_FAILURE);
			}

			try{
				formDownloadService.saveFormSmsError(new FormSmsError(sender,text,new Date(),null,ex.getMessage()));
			}
			catch(Exception e){
				log.error(e.getLocalizedMessage(), e);			
				if(smsSendFailureReports)
					reply.setReponseMessage(reply.getResponseMessage() + " " +  e.getMessage());
			}
		}

		return reply;
	}
	
	/**
	 * Loads settings for user customization of sms processing.
     *
     */
	private void loadSettings() {
		
		String val = settingService.getSetting("smsSendSuccessReports");
		if("false".equalsIgnoreCase(val))
			smsSendSuccessReports = false;
		
		val = settingService.getSetting("smsSendFailureReports");
		if("false".equalsIgnoreCase(val))
			smsSendFailureReports = false;
		
		val = settingService.getSetting("MSG_RECEIVE_PROCESS_SUCCESS");
		if(val != null && val.trim().length() > 0)
			MSG_RECEIVE_PROCESS_SUCCESS = val;
		
		val = settingService.getSetting("MSG_PROCESS_FAILURE");
		if(val != null && val.trim().length() > 0)
			MSG_PROCESS_FAILURE = val;
	}

    @Override
	public void errorOccured(String string, Exception excptn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
