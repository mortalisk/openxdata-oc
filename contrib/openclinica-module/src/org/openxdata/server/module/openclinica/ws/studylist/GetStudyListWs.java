package org.openxdata.server.module.openclinica.ws.studylist;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.soap.SOAPException;

import org.openxdata.server.module.openclinica.ws.ClientHandlerResolver;
import org.openxdata.server.module.openclinica.ws.HeaderHandler;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * 
 * @author daniel
 *
 */
public class GetStudyListWs {

	public GetStudyListWs(){

	}


	public void download(String name,String hashedPassword, InputStream is,OutputStream os,String serializerName,String locale) throws IOException {

		HeaderHandler.username = name;
		HeaderHandler.password = hashedPassword;
		HeaderHandler.doc = null;

		WsService ws = new WsService();
		ws.setHandlerResolver(new ClientHandlerResolver());
		ws.getWsSoap11().getStudyList("");
		
		DataOutputStream dos = new DataOutputStream(os);
		
		try{
			NodeList nodes = HeaderHandler.responseMessage.getSOAPBody().getElementsByTagName("study");
			
			dos.writeByte(nodes.getLength());
			
			for(int index = 0; index < nodes.getLength(); index++){
				Node node = nodes.item(index);
				if(node.getNodeType() != Node.ELEMENT_NODE)
					continue;
				
				Element element = (Element)node;
				dos.writeInt(Integer.parseInt(element.getElementsByTagName("studyId").item(0).getTextContent()));
				dos.writeUTF(element.getElementsByTagName("name").item(0).getTextContent());
				dos.writeUTF(element.getElementsByTagName("oid").item(0).getTextContent());
				dos.writeByte(0); //no forms, just a list of studies
			}
		}
		catch(SOAPException ex){
			ex.printStackTrace();
		}
	}
}
