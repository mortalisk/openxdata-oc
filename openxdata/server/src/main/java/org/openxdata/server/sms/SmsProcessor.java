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

import org.fcitmuk.communication.sms.SMSServer;
import org.springframework.beans.factory.annotation.Autowired;

public class SmsProcessor {

	@Autowired
	private SMSServer smsServer;
	
	@Autowired
	private FormSmsServer formSmsServer;

	private static SmsProcessor instance;

	private String gatewayId,comPort,modemManufacturer, modemModel;
	private int srcPort, dstPort,baudRate;
	private boolean useInboundPolling = false;
	private int inboundPollingInterval = 30000;
	
	private SmsProcessor(){	
	}
	
	public static SmsProcessor getInstance(){
		if(instance == null)
			instance = new SmsProcessor();
		
		return instance;
	}
	
	/**
	 * gets the gatewayId for the SmsServer
	 * @return
	 */
	public String getGatewayId(){
		return this.gatewayId;
	}
	
	/**
	 * sets the gateway id for the SmsServer
	 * @param gatewayId
	 */
	public void setGatewayId(String gatewayId){
		this.gatewayId = gatewayId;
	}
	
	/**
	 * gets the com port
	 * @return
	 */
	public String getComPort(){
		return this.comPort;
	}
	
	/**
	 * sets the com port
	 * @param comPort
	 */
	public void setComPort(String comPort){
		this.comPort = comPort;
	}
	
	/**
	 * gets the source port
	 * @return
	 */
	public int getSourcePort(){
		return this.srcPort;
	}
	
	/**
	 * sets the source port
	 * @param srcPort
	 */
	public void setSourcePort(int srcPort){
		this.srcPort = srcPort;
	}
	
	/**
	 * gets the destination port
	 * @return
	 */
	public int getDestinationPort(){
		return this.dstPort;
	}
	
	/**
	 * sets the destination port
	 * @param dstPort
	 */
	public void setDestinationPort(int dstPort){
		this.dstPort = dstPort;
	}
	
	/**
	 * gets the baud rate
	 * @return
	 */
	public int getBaudRate(){
		return this.baudRate;
	}
	
	/**
	 * sets the baud rate
	 * @param baudRate
	 */	
	public void setBaudRate(int baudRate){
		this.baudRate = baudRate;
	}
	
	/**
	 * gets the modem manufacturer
	 * @return
	 */
	public String getModemManufacturer(){
		return this.modemManufacturer;
	}
	
	/**
	 * sets the modem manufacturer
	 * @param modemManufacturer
	 */
	public void setModemManufacturer(String modemManufacturer){
		this.modemManufacturer = modemManufacturer;
	}
	
	/**
	 * gets the modem model
	 * @return
	 */
	public String getModemModel(){
		return this.modemModel;
	}
	
	/**
	 * sets the modem model
	 * @param modemModel
	 */
	public void setModemModel(String modemModel){
		this.modemModel = modemModel;
	}
	
	/**
	 * sets the sms server
	 * @param smsServer
	 */
	public void setSmsServer(SMSServer smsServer){
		this.smsServer = smsServer;
		setSmsServerProperties();
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
	 * sets the SMS server properties
	 */
	private void setSmsServerProperties() {
		if(this.smsServer != null){
			smsServer.setGatewayId(this.gatewayId);
			smsServer.setComPort(this.comPort);
			smsServer.setSourcePort(this.srcPort);
			smsServer.setDestinationPort(this.dstPort);
			smsServer.setBaudRate(this.baudRate);
			smsServer.setModemManufacturer(this.modemManufacturer);
			smsServer.setModemModel(this.modemModel);
			smsServer.setInboundPollingInterval(this.inboundPollingInterval);
			smsServer.setUseInboundPolling(this.useInboundPolling);
		}
	}
	
	/**
	 * gets the sms server
	 * @return
	 */
	public SMSServer getSmsServer(){
		return this.smsServer;
	}

	public void setFormSmsServer(FormSmsServer formSmsServer) {
		this.formSmsServer = formSmsServer;
	}

	public FormSmsServer getFormSmsServer() {
		return formSmsServer;
	}

    /**
     * Starts running the sms server.
     */
    public void start() {
        if (smsServer != null) {
            setSmsServerProperties();
            smsServer.start();
        }

        if (formSmsServer != null) {
            formSmsServer.initialize();
        }
    }

	/**
	 * Stops the running of the sms server.
	 */
	public void stop(){
		if(smsServer != null)
			smsServer.stop();
	}
}
