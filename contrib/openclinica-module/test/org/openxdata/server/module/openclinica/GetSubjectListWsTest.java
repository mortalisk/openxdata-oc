package org.openxdata.server.module.openclinica;

import org.junit.Test;
import org.openxdata.server.module.openclinica.ws.ClientHandlerResolver;
import org.openxdata.server.module.openclinica.ws.HeaderHandler;
import org.openxdata.server.module.openclinica.ws.subjectlist.GetSubjectsRequest;
import org.openxdata.server.module.openclinica.ws.subjectlist.WsService;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * 
 * @author daniel
 *
 */
public class GetSubjectListWsTest {

	@Test
	public void testGetSubjectList(){
		try{
			WsService ws = new WsService();
			ws.setHandlerResolver(new ClientHandlerResolver());
			GetSubjectsRequest request = new GetSubjectsRequest();
			request.setStudyId(1);
			ws.getWsSoap11().getSubjects(request);
			
			NodeList subjectNodes = HeaderHandler.responseMessage.getSOAPBody().getElementsByTagName("subject");
			
			for(int index = 0; index < subjectNodes.getLength(); index++){
				Node subjectNode = subjectNodes.item(index);
				if(subjectNode.getNodeType() != Node.ELEMENT_NODE)
					continue;
				
				Element subjectElement = (Element)subjectNode;
				System.out.println(Integer.parseInt(subjectElement.getElementsByTagName("subjectId").item(0).getTextContent()));
				System.out.println(subjectElement.getElementsByTagName("personId").item(0).getTextContent());
				System.out.println(subjectElement.getElementsByTagName("studySubjectId").item(0).getTextContent());
				System.out.println(subjectElement.getElementsByTagName("secondaryId").item(0).getTextContent());
				System.out.println(subjectElement.getElementsByTagName("gender").item(0).getTextContent());
				System.out.println(subjectElement.getElementsByTagName("birthDate").item(0).getTextContent());
				
				NodeList eventNodes = subjectElement.getElementsByTagName("event");
				System.out.println(eventNodes.getLength());
				
				for(int index2 = 0; index2 < eventNodes.getLength(); index2++){
					Node eventNode = eventNodes.item(index2);
					if(eventNode.getNodeType() != Node.ELEMENT_NODE)
						continue;
					
					Element eventElement = (Element)eventNode;
					System.out.println(Integer.parseInt(eventElement.getElementsByTagName("eventId").item(0).getTextContent()));
					System.out.println(eventElement.getElementsByTagName("location").item(0).getTextContent());
				}
			}
			
			//System.out.println(response.getResult());
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
