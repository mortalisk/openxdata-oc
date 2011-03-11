package org.akaza.openclinica.ws;


import java.util.List;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.xml.DomUtils;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.XPathParam;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Gets a list of events in a study together with identifiers for crfs in each event.
 * 
 * @author daniel
 *
 */
@Endpoint
public class EventListGetEndpoint {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	private final String NAMESPACE_URI_V1 = "http://openclinica.org/ws/eventListGet/v1";
	private final String SUCCESS_MESSAGE = "success";
	private final String FAIL_MESSAGE = "fail";
	
	private final DataSource dataSource;

	/**
	 * Constructor
	 * 
	 * @param cctsService
	 */
	public EventListGetEndpoint(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	
	/**
	 * if NAMESPACE_URI_V1:getEventListRequest execute this method
	 * 
	 * @return
	 * @throws Exception
	 */
	@PayloadRoot(localPart = "getEventListRequest", namespace = NAMESPACE_URI_V1)
	public Source getEventList(@XPathParam("//eventListGet:studyId") Element studyIdElement) throws Exception {   	
		
		String studyId = studyIdElement == null ? null : DomUtils.getTextValue(studyIdElement);
			
		if (studyId != null) {
			return new DOMSource(mapConfirmation(studyId,SUCCESS_MESSAGE, ""));
		} else {
			return new DOMSource(mapConfirmation(studyId,FAIL_MESSAGE, "Please specify a studyId"));
		}
	}


	/**
	 * Create Response
	 * 
	 * @param confirmation
	 * @return
	 * @throws Exception
	 */
	private Element mapConfirmation(String studyId, String confirmation, String theLabel) throws Exception {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document document = docBuilder.newDocument();

		Element responseElement = document.createElementNS(NAMESPACE_URI_V1, "getEventListResponse");
		Element resultElement = document.createElementNS(NAMESPACE_URI_V1, "result");
		Element label = document.createElementNS(NAMESPACE_URI_V1, "label");
		resultElement.setTextContent(confirmation);
		label.setTextContent(theLabel);
		responseElement.appendChild(resultElement);
		responseElement.appendChild(label);

		StudyDAO studyDao = new StudyDAO(dataSource);
		StudyBean study = (StudyBean)studyDao.findByPK(Integer.parseInt(studyId));

		Element studyListElement = document.createElementNS(NAMESPACE_URI_V1, "eventList");
		responseElement.appendChild(studyListElement);
		
		CRFDAO crfDao = new CRFDAO(dataSource);
		CRFVersionDAO crfVersionDAO = new CRFVersionDAO(dataSource);
		
		StudyEventDefinitionDAO eventDefDao = new StudyEventDefinitionDAO(dataSource);
		List<StudyEventDefinitionBean> eventList = (List<StudyEventDefinitionBean>)eventDefDao.findAllByStudy(study);
		for(int index = 0; index < eventList.size(); index++){
			StudyEventDefinitionBean event = eventList.get(index);
			
			Element studyElement = document.createElementNS(NAMESPACE_URI_V1, "event");
			Element element = document.createElementNS(NAMESPACE_URI_V1, "eventId");
			element.setTextContent(event.getId()+"");
			studyElement.appendChild(element);

			element = document.createElementNS(NAMESPACE_URI_V1, "oid");
			element.setTextContent(event.getOid());
			studyElement.appendChild(element);
			
			element = document.createElementNS(NAMESPACE_URI_V1, "name");
			element.setTextContent(event.getName());
			studyElement.appendChild(element);

			studyListElement.appendChild(studyElement);
			
			List<EventDefinitionCRFBean> eventCrfs = new EventDefinitionCRFDAO(dataSource).findAllByEventDefinitionId(event.getId());
			if(eventCrfs == null)
				continue;
			
			Element crfListElement = document.createElementNS(NAMESPACE_URI_V1, "crfList");
			studyElement.appendChild(crfListElement);
			
			for(int i = 0; i < eventCrfs.size(); i++){
				EventDefinitionCRFBean eventCrf = eventCrfs.get(i);
				
				Element crfElement = document.createElementNS(NAMESPACE_URI_V1, "crf");
				crfListElement.appendChild(crfElement);
				
				element = document.createElementNS(NAMESPACE_URI_V1, "crfId");
				element.setTextContent(eventCrf.getCrfId()+"");
				crfElement.appendChild(element);
				
				CRFVersionBean crfVersionBean = (CRFVersionBean)crfVersionDAO.findByPK(eventCrf.getDefaultVersionId());
				element = document.createElementNS(NAMESPACE_URI_V1, "oid");
				element.setTextContent(crfVersionBean.getOid());
				crfElement.appendChild(element);
				
				element = document.createElementNS(NAMESPACE_URI_V1, "name");
				element.setTextContent(((CRFBean)crfDao.findByPK(eventCrf.getCrfId())).getName() + " " + crfVersionBean.getName());
				crfElement.appendChild(element);
			}
		}

		return responseElement;
	}
}
