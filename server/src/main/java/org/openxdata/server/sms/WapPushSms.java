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

import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;
import org.smslib.OutboundWapSIMessage;
import org.smslib.Service;
import org.smslib.WapSISignals;
import org.smslib.modem.SerialModemGateway;

/**
 * 
 * Sends WAP Push messages for midlet installation.
 * 
 * @author daniel
 *
 */
public class WapPushSms {

	private static Logger log = Logger.getLogger(WapPushSms.class);
	
	/**
	 * Sends WAP PUSH sms to a list of mobile phone numbers.
	 * 
	 * @param phoneNos the list of mobile phone numbers.
	 * @param url the OTA url which has the installation jad file.
	 * @param modemComPort the port at which the modem is running.
	 * @param modemBaudRate the baud rate for the modem.
	 * @param promptText the text which will appear on the user's screen to prompt them install the mobile application.
	 * @return true if all the messages are successfully sent, else false.
	 */
	public static boolean sendMessages(List<String> phoneNos, String url, String modemComPort, int modemBaudRate, String promptText){
		try{
			
			Service smsService = new Service();
			log.info("Starting OpenXdata WAP Push service at com port:="+modemComPort);
			
			SerialModemGateway gateway = new SerialModemGateway(
					"OpenXdata Gateway Modem", modemComPort, modemBaudRate, "", "",smsService);
			
			gateway.setOutbound(true);
			gateway.setSimPin("0000");
			
			smsService.addGateway(gateway);
			smsService.startService();
			
			OutboundWapSIMessage wapMsg = new OutboundWapSIMessage("", new URL(url), promptText);
			wapMsg.setSignal(WapSISignals.HIGH);
			
			for(String phoneNo : phoneNos){
				wapMsg.setRecipient(phoneNo);
				log.info("Sending WAP Push Message to: "+phoneNo);
				smsService.sendMessage(wapMsg);
			}
			
			smsService.stopService();
			
			return true;
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		return false;
	}
}
