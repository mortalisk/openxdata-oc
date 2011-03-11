package org.openxdata.server.module.openclinica;

import org.junit.Test;
import org.openxdata.server.module.openclinica.ws.ClientHandlerResolver;
import org.openxdata.server.module.openclinica.ws.HeaderHandler;
import org.openxdata.server.module.openclinica.ws.userlist.WsService;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * 
 * @author daniel
 *
 */
public class GetUsersWsTest {
	
	@Test
	public void testGetStudyList(){
		try{
			WsService ws = new WsService();
			ws.setHandlerResolver(new ClientHandlerResolver());
			
			ws.getWsSoap11().getUsers("");
			
			NodeList nodes = HeaderHandler.responseMessage.getSOAPBody().getElementsByTagName("user");
			
			for(int index = 0; index < nodes.getLength(); index++){
				Node node = nodes.item(index);
				if(node.getNodeType() != Node.ELEMENT_NODE)
					continue;
				
				Element element = (Element)node;
				System.out.println(Integer.parseInt(element.getElementsByTagName("userId").item(0).getTextContent()));
				System.out.println(element.getElementsByTagName("name").item(0).getTextContent());
				System.out.println(element.getElementsByTagName("password").item(0).getTextContent());
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
