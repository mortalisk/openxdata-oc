package org.openxdata.server.sms;

import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static Logger log = LoggerFactory.getLogger(WapPushSms.class);
	
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
