package com.test;

/**
 * author@ gbro
 * **/

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.springframework.ws.server.endpoint.AbstractJDomPayloadEndpoint;

public class StudySubEndpoint extends AbstractJDomPayloadEndpoint {

	private XPath studyNumberExpression;
	
	private XPath creationDateExpression;
	
	private XPath byExpression;
	
    private XPath study_IdentityExpression;

    private XPath numberOfSubjectsExpression;
    
    private XPath nameExpression;

    private XPath ageExpression;
    
    private XPath sexExpression;

    private final StudySubService StudySubjService;

    public StudySubEndpoint(StudySubService StudySubjService) throws JDOMException {
    	System.out.println("Entered in constructor");
        this.StudySubjService = StudySubjService;
        Namespace namespace = Namespace.getNamespace("tr", "http://sample.com/tr/schemas");
        
        studyNumberExpression = XPath.newInstance("//tr:StudyNumber");
        studyNumberExpression.addNamespace(namespace);
        
        creationDateExpression = XPath.newInstance("//tr:CreationDate");
        creationDateExpression.addNamespace(namespace);
        
        byExpression = XPath.newInstance("//tr:By");
        byExpression.addNamespace(namespace);
        
        study_IdentityExpression = XPath.newInstance("//tr:Study_Identity");
        study_IdentityExpression.addNamespace(namespace);
        
        numberOfSubjectsExpression = XPath.newInstance("//tr:NumberOfSubjects");
        numberOfSubjectsExpression.addNamespace(namespace);
        
        nameExpression = XPath.newInstance("//tr:Name");
        nameExpression.addNamespace(namespace);
        
        ageExpression = XPath.newInstance("//tr:Age");
        ageExpression.addNamespace(namespace);
        
        sexExpression = XPath.newInstance("//tr:Sex");
        sexExpression.addNamespace(namespace);
    }

    protected Element invokeInternal(Element stdSubRequest) throws Exception {    
    	System.out.println("Entered in invokeInternal");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        ServiceDetails serviceDetail = new ServiceDetails();
        
        Integer stdNumber = Integer.parseInt(studyNumberExpression.valueOf(stdSubRequest));
        serviceDetail.setStudyNumber(stdNumber);
        Date crtDate = dateFormat.parse(creationDateExpression.valueOf(stdSubRequest));
        serviceDetail.setCreationDate(crtDate);
        String by = byExpression.valueOf(stdSubRequest);
        serviceDetail.setBy(by);
        String std_identity = study_IdentityExpression.valueOf(stdSubRequest);
        System.out.println("After invokeInternal");
        serviceDetail.setStd_identity(std_identity);
        Integer noOfSubj = Integer.parseInt(numberOfSubjectsExpression.valueOf(stdSubRequest));
        serviceDetail.setNoOfSubj(noOfSubj);
        String name = nameExpression.valueOf(stdSubRequest);
        serviceDetail.setName(name);
        Integer age = Integer.parseInt(ageExpression.valueOf(stdSubRequest));
        serviceDetail.setAge(age);
        String sex = sexExpression.valueOf(stdSubRequest);
        serviceDetail.setSex(sex);
        System.out.println("Entered to connect");
        String responseMessage = StudySubjService.ImplementTask(serviceDetail);
        return generateResponse(responseMessage);
    }
    
	private Element generateResponse(String message) 
	{
		System.out.println("Entered in generateResponse");
		Element response = null;
		Namespace respNamespace = Namespace.getNamespace("tr", "http://sample.com/tr/schemas");
		try {
			Element msgResponseRoot = new Element("StudyResponse",
					respNamespace);

			Element messageElement = new Element("Code", respNamespace);
			messageElement.addContent(message);
			msgResponseRoot.addContent(messageElement);

			response = new Document(msgResponseRoot).getRootElement();
		} catch (Exception e) {
			System.out.println("Error response writing");
		}
		return response;
	}
}