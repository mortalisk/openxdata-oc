package org.openxdata.server.module.openclinica;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openxdata.server.module.openclinica.model.CrfDef;
import org.openxdata.server.module.openclinica.model.StudyEvent;
import org.openxdata.server.module.openclinica.ws.ClientHandlerResolver;
import org.openxdata.server.module.openclinica.ws.HeaderHandler;
import org.openxdata.server.module.openclinica.ws.eventlist.GetEventListRequest;
import org.openxdata.server.module.openclinica.ws.eventlist.GetEventListResponse;
import org.openxdata.server.module.openclinica.ws.eventlist.Ws;
import org.openxdata.server.module.openclinica.ws.eventlist.WsService;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * 
 * @author daniel
 *
 */
public class GetEventListWsTest {

	@Test
	public void testGetEventList(){
		try{
			WsService ws = new WsService();
			ws.setHandlerResolver(new ClientHandlerResolver());

			Ws w = ws.getWsSoap11();

			GetEventListRequest request = new GetEventListRequest();
			request.setStudyId(1);
			GetEventListResponse response = w.getEventList(request);

			NodeList eventNodes = HeaderHandler.responseMessage.getSOAPBody().getElementsByTagName("event");

			System.out.println(eventNodes.getLength());

			for(int index = 0; index < eventNodes.getLength(); index++){
				Node eventNode = eventNodes.item(index);
				if(eventNode.getNodeType() != Node.ELEMENT_NODE)
					continue;

				StudyEvent event =  new StudyEvent();

				Element eventElement = (Element)eventNode;
				event.setEventId(Integer.parseInt(eventElement.getElementsByTagName("eventId").item(0).getTextContent()));
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
					crf.setName(crfElement.getElementsByTagName("name").item(0).getTextContent());
				}
			}

			HeaderHandler.responseMessage.writeTo(System.out);
			//System.out.println(HeaderHandler.message.get);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
