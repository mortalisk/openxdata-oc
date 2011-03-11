package org.akaza.openclinica.ws;

import java.util.List;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Gets a list of studies as registered in openclinica.
 * 
 * @author daniel
 *
 */
@Endpoint
public class StudyListGetEndpoint {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	private final String NAMESPACE_URI_V1 = "http://openclinica.org/ws/studyListGet/v1";
	private final String SUCCESS_MESSAGE = "success";
	private final String FAIL_MESSAGE = "fail";

	private final DataSource dataSource;

	/**
	 * Constructor
	 * 
	 * @param cctsService
	 */
	public StudyListGetEndpoint(DataSource dataSource) {
		this.dataSource = dataSource;
	}


	/**
	 * if NAMESPACE_URI_V1:getStudyListRequest execute this method
	 * 
	 * @return
	 * @throws Exception
	 */
	@PayloadRoot(localPart = "getStudyListRequest", namespace = NAMESPACE_URI_V1)
	public Source getStudyList() throws Exception {   	
		if (true) {
			return new DOMSource(mapConfirmation(SUCCESS_MESSAGE, ""));
		} else {
			return new DOMSource(mapConfirmation(FAIL_MESSAGE, null));
		}
	}


	/**
	 * Create Response
	 * 
	 * @param confirmation
	 * @return
	 * @throws Exception
	 */
	private Element mapConfirmation(String confirmation, String theLabel) throws Exception {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document document = docBuilder.newDocument();

		Element responseElement = document.createElementNS(NAMESPACE_URI_V1, "getStudyListResponse");
		Element resultElement = document.createElementNS(NAMESPACE_URI_V1, "result");
		Element label = document.createElementNS(NAMESPACE_URI_V1, "label");
		resultElement.setTextContent(confirmation);
		label.setTextContent(theLabel);
		responseElement.appendChild(resultElement);
		responseElement.appendChild(label);


		Element studyListElement = document.createElementNS(NAMESPACE_URI_V1, "studyList");
		responseElement.appendChild(studyListElement);


		StudyDAO studyDao = new StudyDAO(dataSource);
		List<StudyBean> studyList = (List<StudyBean>)studyDao.findAll();
		for(int index = 0; index < studyList.size(); index++){
			StudyBean study = studyList.get(index);

			Element studyElement = document.createElementNS(NAMESPACE_URI_V1, "study");

			Element element = document.createElementNS(NAMESPACE_URI_V1, "studyId");
			element.setTextContent(study.getId()+"");
			studyElement.appendChild(element);
			
			element = document.createElementNS(NAMESPACE_URI_V1, "oid");
			element.setTextContent(study.getOid());
			studyElement.appendChild(element);

			element = document.createElementNS(NAMESPACE_URI_V1, "name");
			element.setTextContent(study.getName());
			studyElement.appendChild(element);

			studyListElement.appendChild(studyElement);
		}

		return responseElement;
	}
}
