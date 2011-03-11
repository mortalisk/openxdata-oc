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
public class StudyListCreater {

	//No generics
	List myData = new ArrayList() ;
	Document dom;
	List datalist;
	

	public StudyListCreater(List myData) {
		//create a list to hold the data
		this. myData = myData;
		
	}

	public String runEngine(){
		System.out.println("Started .. ");
		createDOMTree();
		printToFile();
		System.out.println("Generated file successfully.");
		File file = new File("Study.xml");
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
		Element rootEle = dom.createElement("Studies");
		dom.appendChild(rootEle);

		//No enhanced for
		Iterator it  = myData.iterator();
		while(it.hasNext()) {
			Study b = (Study)it.next();
			//For each Study object  create <Study> element and attach it to root
			Element studyEle = createstudyElement(b);
			rootEle.appendChild(studyEle);
		}

	}

	/**
	 * Helper method which creates a XML element <Studies>
	 * @param b The Study for which we need to create an xml representation
	 * @return XML element snippet representing a study
	 */
	private Element createstudyElement(Study b){

		Element studyEle = dom.createElement("Study");
		studyEle.setAttribute("StudyId", b.getStudyId());

		//create author element and author text node and attach it to studyElement
		Element nameEle = dom.createElement("Name");
		Text nameText = dom.createTextNode(b.getStudyName());
		nameEle.appendChild(nameText);
		studyEle.appendChild(nameEle);

		//create Description element and text node and attach it to studyElement
		Element descripEle = dom.createElement("Description");
		Text titleText = dom.createTextNode(b.getDescription());
		descripEle.appendChild(titleText);
		studyEle.appendChild(descripEle);
		
		//create principal Investigater element and text node and attach it to studyElement
		Element evenstigatorEle = dom.createElement("Principal_Investigator");
		Text evenstigatorTxt = dom.createTextNode(b.getPrincipalInvestigator());
		evenstigatorEle.appendChild( evenstigatorTxt);
		studyEle.appendChild(evenstigatorEle);

		//create  created date element and text node and attach it to studyElement
		Element createddateEle = dom.createElement("DateCreated");
		Text createddateTxt = dom.createTextNode(b.getDateCreated());
		createddateEle.appendChild( createddateTxt);
		studyEle.appendChild(createddateEle);
		
		return studyEle;

	}

	private void printToFile()
	{
		CreationUtil utl = new CreationUtil();
		utl.printToFile(dom, "Study.xml");
	}

		
	

}

