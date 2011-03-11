package org.openxdata.server.module.openclinica.ws.eventlist;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.SOAPException;

import org.openxdata.server.module.openclinica.model.CrfDef;
import org.openxdata.server.module.openclinica.model.StudyEvent;
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
public class GetEventListWs {

	public GetEventListWs(){
		
	}
	
	public void download(String name,String hashedPassword, int studyId, InputStream is,OutputStream os,String serializerName,String locale) throws IOException {
		
		HeaderHandler.username = name;
		HeaderHandler.password = hashedPassword;
		HeaderHandler.doc = null;
		
		WsService ws = new WsService();
		ws.setHandlerResolver(new ClientHandlerResolver());
		
		GetEventListRequest request = new GetEventListRequest();
		request.setStudyId(studyId);
		ws.getWsSoap11().getEventList(request);
		
		DataOutputStream dos = new DataOutputStream(os);
		
		try{

			NodeList eventNodes = HeaderHandler.responseMessage.getSOAPBody().getElementsByTagName("event");

			dos.writeByte(eventNodes.getLength());
			
			for(int index = 0; index < eventNodes.getLength(); index++){
				Node eventNode = eventNodes.item(index);
				if(eventNode.getNodeType() != Node.ELEMENT_NODE)
					continue;

				StudyEvent event =  new StudyEvent();
				
				Element eventElement = (Element)eventNode;
				event.setEventId(Integer.parseInt(eventElement.getElementsByTagName("eventId").item(0).getTextContent()));
				event.setOid(eventElement.getElementsByTagName("oid").item(0).getTextContent());
				event.setName(eventElement.getElementsByTagName("name").item(0).getTextContent());

				List<CrfDef> crfs = new ArrayList<CrfDef>();
				event.setCrfs(crfs);
				
				NodeList crfNodes = eventElement.getElementsByTagName("crf");
				
				for(int index2 = 0; index2 < crfNodes.getLength(); index2++){
					Node crfNode = crfNodes.item(index2);
					if(crfNode.getNodeType() != Node.ELEMENT_NODE)
						continue;

					CrfDef crf = new CrfDef();
					crfs.add(crf);
					
					Element crfElement = (Element)crfNode;
					crf.setCrfId(Integer.parseInt(crfElement.getElementsByTagName("crfId").item(0).getTextContent()));
					crf.setOid(crfElement.getElementsByTagName("oid").item(0).getTextContent());
					crf.setName(crfElement.getElementsByTagName("name").item(0).getTextContent());
				}
				
				event.serialize(dos);
			}
		}
		catch(SOAPException ex){
			ex.printStackTrace();
		}
	}
}
