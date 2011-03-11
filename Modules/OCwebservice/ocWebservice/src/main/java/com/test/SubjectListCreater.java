package com.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import java.util.StringTokenizer;

//For jdk1.5 with built in xerces parser
//import com.sun.org.apache.xml.internal.serialize.OutputFormat;
//import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/** 
 *  .
 * @author gbro
 *
 */
public class SubjectListCreater {

	//No generics
	List myData = new ArrayList() ;
	Document dom;
	List datalist;
	

	public SubjectListCreater(List myData) {
		//create a list to hold the data
		this. myData = myData;
		
	}

	public String runEngine(){
		System.out.println("Started .. ");
		createDOMTree();
		printToFile();
		System.out.println("Generated file successfully.");
		File file = new File("Subject.xml");
		CreationUtil utl = new CreationUtil();
		return utl.getContents(file);
	}
	
	public void createDocument() {
		CreationUtil utl = new CreationUtil();
		dom = utl.createDocument(dom);

	}

	/**
	 * The real workhorse which creates the XML structure
	 */
	private void createDOMTree(){

		//create the root element <Studies>
		Element rootEle = dom.createElement("Subjects");
		dom.appendChild(rootEle);

		//No enhanced for
		Iterator it  = myData.iterator();
		while(it.hasNext()) {
			Subject b = (Subject)it.next();
			//For each Subject object  create <Subject> element and attach it to root
			Element SubjectEle = createSubjectElement(b);
			rootEle.appendChild(SubjectEle);
		}

	}

	/**
	 * Helper method which creates a XML element <Studies>
	 * @param b The Subject for which we need to create an xml representation
	 * @return XML element snippet representing a Subject
	 */
	private Element createSubjectElement(Subject b){

		Element SubjectEle = dom.createElement("Subject");
		SubjectEle.setAttribute("SubjectId", b.getSubjectId());

		//create author element and author text node and attach it to SubjectElement
		Element Unique_identifierEle = dom.createElement("Unique_identifier");
		Text Unique_identifierText = dom.createTextNode(b.getUnique_identifier());
		Unique_identifierEle.appendChild(Unique_identifierText);
		SubjectEle.appendChild(Unique_identifierEle);

		//create Gender element and text node and attach it to SubjectElement
		Element GenderEle = dom.createElement("Gender");
		Text titleText = dom.createTextNode(b.getGender());
		GenderEle.appendChild(titleText);
		SubjectEle.appendChild(GenderEle);
		
		//create principal Investigater element and text node and attach it to SubjectElement
		Element StudyIdEle = dom.createElement("StudyId");
		Text StudyIdTxt = dom.createTextNode(b.getStudyId());
		StudyIdEle.appendChild( StudyIdTxt);
		SubjectEle.appendChild(StudyIdEle);

		//create  created date element and text node and attach it to SubjectElement
		Element createddateEle = dom.createElement("DateCreated");
		Text createddateTxt = dom.createTextNode(b.getDateCreated());
		createddateEle.appendChild( createddateTxt);
		SubjectEle.appendChild(createddateEle);
		
		return SubjectEle;

	}

	private void printToFile()
	{
		CreationUtil utl = new CreationUtil();
		utl.printToFile(dom, "Subject.xml");
	}

		
	

}

