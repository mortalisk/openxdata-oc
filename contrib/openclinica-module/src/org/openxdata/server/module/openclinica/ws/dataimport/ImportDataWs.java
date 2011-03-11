package org.openxdata.server.module.openclinica.ws.dataimport;

import java.io.IOException;

import org.openxdata.server.module.openclinica.ws.ClientHandlerResolver;
import org.openxdata.server.module.openclinica.ws.HeaderHandler;
import org.openxdata.server.util.XmlUtil;


/**
 * 
 * @author daniel
 *
 */
public class ImportDataWs {

	public ImportDataWs(){
		
	}
	
	
	public void importData(String name,String hashedPassword, String xml) throws IOException {
		HeaderHandler.username = name;
		HeaderHandler.password = hashedPassword;
		HeaderHandler.doc = XmlUtil.fromString2Doc(xml);
		
		WsService ws = new WsService();
		ws.setHandlerResolver(new ClientHandlerResolver());
	
		ImportDataResponse response = ws.getWsSoap11().importData("");
		
		AuditMessagesType messages = response.getAuditMessages();
		System.out.println(messages);
	}
}
