package org.akaza.openclinica.ws;


import java.text.SimpleDateFormat;
import java.util.List;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.xml.DomUtils;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.XPathParam;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Gets a list of subjects in a study and the list of events scheduled for each subject.
 * 
 * @author daniel
 *
 */
@Endpoint
public class SubjectListGetEndpoint {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	private final String NAMESPACE_URI_V1 = "http://openclinica.org/ws/subjectListGet/v1";
	private final String SUCCESS_MESSAGE = "success";
	private final String FAIL_MESSAGE = "fail";

	private final DataSource dataSource;

	/**
	 * Constructor
	 * 
	 * @param cctsService
	 */
	public SubjectListGetEndpoint(DataSource dataSource) {
		this.dataSource = dataSource;
	}


	/**
	 * if NAMESPACE_URI_V1:getSubjectsRequest execute this method
	 * 
	 * @return
	 * @throws Exception
	 */
	@PayloadRoot(localPart = "getSubjectsRequest", namespace = NAMESPACE_URI_V1)
	public Source getStudyList(@XPathParam("//subjectListGet:studyId") Element studyIdElement) throws Exception {   	
		
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

		Element responseElement = document.createElementNS(NAMESPACE_URI_V1, "getSubjectsResponse");
		Element resultElement = document.createElementNS(NAMESPACE_URI_V1, "result");
		Element label = document.createElementNS(NAMESPACE_URI_V1, "label");
		resultElement.setTextContent(confirmation);
		label.setTextContent(theLabel);
		responseElement.appendChild(resultElement);
		responseElement.appendChild(label);

		StudyDAO studyDao = new StudyDAO(dataSource);
		StudyBean study = (StudyBean)studyDao.findByPK(Integer.parseInt(studyId));

		SubjectDAO subjectDAO = new SubjectDAO(dataSource);
		StudySubjectDAO subjectDao = new StudySubjectDAO(dataSource);
		List<StudySubjectBean> subjectList = (List<StudySubjectBean>)subjectDao.findAllByStudy(study);

		Element subjectListElement = document.createElementNS(NAMESPACE_URI_V1, "subjectList");
		responseElement.appendChild(subjectListElement);

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		StudyEventDAO eventDao = new StudyEventDAO(dataSource);

		for(int index = 0; index < subjectList.size(); index++){
			StudySubjectBean studySubject = subjectList.get(index);
			SubjectBean subject = (SubjectBean)subjectDAO.findByPK(studySubject.getSubjectId());

			Element subjectElement = document.createElementNS(NAMESPACE_URI_V1, "subject");

			Element element = document.createElementNS(NAMESPACE_URI_V1, "subjectId");
			element.setTextContent(subject.getId()+"");
			subjectElement.appendChild(element);

			element = document.createElementNS(NAMESPACE_URI_V1, "personId");
			element.setTextContent(subject.getUniqueIdentifier());
			subjectElement.appendChild(element);

			element = document.createElementNS(NAMESPACE_URI_V1, "studySubjectId");
			element.setTextContent(studySubject.getLabel());
			subjectElement.appendChild(element);

			element = document.createElementNS(NAMESPACE_URI_V1, "secondaryId");
			element.setTextContent(studySubject.getSecondaryLabel());
			subjectElement.appendChild(element);

			element = document.createElementNS(NAMESPACE_URI_V1, "gender");
			element.setTextContent(subject.getGender()+"");
			subjectElement.appendChild(element);

			element = document.createElementNS(NAMESPACE_URI_V1, "birthDate");
			element.setTextContent(format.format(subject.getDateOfBirth()));
			subjectElement.appendChild(element);

			subjectListElement.appendChild(subjectElement);


			List<StudyEventBean> events = eventDao.findAllBySubjectId(subject.getId());
			if(events == null)
				continue;

			Element eventListElement = document.createElementNS(NAMESPACE_URI_V1, "eventList");
			subjectElement.appendChild(eventListElement);
			
			for(int i = 0; i < events.size(); i++){
				StudyEventBean event = events.get(i);
				
				Element eventElement = document.createElementNS(NAMESPACE_URI_V1, "event");
				eventListElement.appendChild(eventElement);
				
				element = document.createElementNS(NAMESPACE_URI_V1, "eventId");
				element.setTextContent(event.getStudyEventDefinitionId()+"");
				eventElement.appendChild(element);
				
				element = document.createElementNS(NAMESPACE_URI_V1, "location");
				element.setTextContent(event.getLocation());
				eventElement.appendChild(element);
			}
		}

		return responseElement;
	}
}
