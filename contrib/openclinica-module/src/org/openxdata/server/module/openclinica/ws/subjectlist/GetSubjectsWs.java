package org.openxdata.server.module.openclinica.ws.subjectlist;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.SOAPException;

import org.openxdata.server.module.openclinica.model.StudySubjectEvent;
import org.openxdata.server.module.openclinica.model.Subject;
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
public class GetSubjectsWs {

	public GetSubjectsWs(){

	}

	public void download(String name,String hashedPassword, int studyId, InputStream is,OutputStream os,String serializerName,String locale) throws IOException {

		HeaderHandler.username = name;
		HeaderHandler.password = hashedPassword;
		HeaderHandler.doc = null;

		WsService ws = new WsService();
		ws.setHandlerResolver(new ClientHandlerResolver());
		GetSubjectsRequest request = new GetSubjectsRequest();
		request.setStudyId(studyId);
		ws.getWsSoap11().getSubjects(request);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		DataOutputStream dos = new DataOutputStream(os);
		
		try{

			NodeList subjectNodes = HeaderHandler.responseMessage.getSOAPBody().getElementsByTagName("subject");

			dos.writeInt(subjectNodes.getLength());
			
			for(int index = 0; index < subjectNodes.getLength(); index++){
				Node subjectNode = subjectNodes.item(index);
				if(subjectNode.getNodeType() != Node.ELEMENT_NODE)
					continue;

				Subject subject =  new Subject();
				
				Element subjectElement = (Element)subjectNode;
				subject.setSubjectId(Integer.parseInt(subjectElement.getElementsByTagName("subjectId").item(0).getTextContent()));
				subject.setPersonId(subjectElement.getElementsByTagName("personId").item(0).getTextContent());
				subject.setStudySubjectId(subjectElement.getElementsByTagName("studySubjectId").item(0).getTextContent());
				subject.setSecondaryId(subjectElement.getElementsByTagName("secondaryId").item(0).getTextContent());
				subject.setOid(subjectElement.getElementsByTagName("oid").item(0).getTextContent());
				subject.setGender(subjectElement.getElementsByTagName("gender").item(0).getTextContent());
				subject.setBirthDate(dateFormat.parse(subjectElement.getElementsByTagName("birthDate").item(0).getTextContent()));

				List<StudySubjectEvent> events = new ArrayList<StudySubjectEvent>();
				subject.setEvents(events);
				
				NodeList eventNodes = subjectElement.getElementsByTagName("event");
				
				for(int index2 = 0; index2 < eventNodes.getLength(); index2++){
					Node eventNode = eventNodes.item(index2);
					if(eventNode.getNodeType() != Node.ELEMENT_NODE)
						continue;

					StudySubjectEvent event = new StudySubjectEvent();
					events.add(event);
					
					Element eventElement = (Element)eventNode;
					event.setEventId(Integer.parseInt(eventElement.getElementsByTagName("eventId").item(0).getTextContent()));
					event.setLocation(eventElement.getElementsByTagName("location").item(0).getTextContent());
				}
				
				subject.serialize(dos);
			}
		}
		catch(SOAPException ex){
			ex.printStackTrace();
		}
		catch(ParseException ex){
			ex.printStackTrace();
		}
	}
}
