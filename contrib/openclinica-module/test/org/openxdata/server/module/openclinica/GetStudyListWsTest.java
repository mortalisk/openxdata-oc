package org.openxdata.server.module.openclinica;

import org.junit.Test;
import org.openxdata.server.module.openclinica.ws.ClientHandlerResolver;
import org.openxdata.server.module.openclinica.ws.HeaderHandler;
import org.openxdata.server.module.openclinica.ws.studylist.GetStudyListResponse;
import org.openxdata.server.module.openclinica.ws.studylist.Ws;
import org.openxdata.server.module.openclinica.ws.studylist.WsService;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * 
 * @author daniel
 *
 */
public class GetStudyListWsTest {

	@Test
	public void testGetStudyList(){
		try{
			WsService ws = new WsService();
			ws.setHandlerResolver(new ClientHandlerResolver());
			
			Ws w = ws.getWsSoap11();

			GetStudyListResponse response = w.getStudyList("");
			
			NodeList nodes = HeaderHandler.responseMessage.getSOAPBody().getElementsByTagName("study");
			
			for(int index = 0; index < nodes.getLength(); index++){
				Node node = nodes.item(index);
				if(node.getNodeType() != Node.ELEMENT_NODE)
					continue;
				
				Element element = (Element)node;
				System.out.println(Integer.parseInt(element.getElementsByTagName("studyId").item(0).getTextContent()));
				System.out.println(element.getElementsByTagName("name").item(0).getTextContent());
				System.out.println(""); //identifier;
			}
			
			System.out.println(response.getResult());
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
