/*
 *  Licensed to the OpenXdata Foundation (OXDF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The OXDF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, 
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 *  Copyright 2010 http://www.openxdata.org.
 */
package org.openxdata.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.openxdata.server.admin.model.exception.UnexpectedException;
import org.openxdata.server.xpath.XPathExpression;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


/**
 * This class has utilities for handling xml documents.
 * 
 * @author daniel
 *
 */
public class XmlUtil {

	/**
	 * Converts a document to its text representation.
	 * 
	 * @param doc - the document.
	 * @return - the text representation of the document.
	 */
	public static String doc2String(Document doc){
        TransformerFactory factory = TransformerFactory.newInstance();
		try{
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			StringWriter outStream  = new StringWriter();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(outStream);
			transformer.transform(source, result);
			return outStream.getBuffer().toString();
		}
		catch(TransformerException ex){
			throw new UnexpectedException(ex);
		}
	}
	
    /**
     * Creates a document object from xml text.
     *
     * @param xml the xml text.
     * @return the document object.
     */
    public static Document fromString2Doc(String xml) {
        try {
            InputStream input = IOUtils.toInputStream(xml, "UTF-8");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();

            return builder.parse(input);
        } catch (IOException ex) {
            throw new UnexpectedException(ex);
        } catch (ParserConfigurationException ex) {
            throw new UnexpectedException(ex);
        } catch (SAXException ex) {
            throw new UnexpectedException(ex);
        }
    }
	
    /**
     * Gets the text value of a node as pointed to by an xpath expression in an xml document.
     *
     * @param doc the xml document.
     * @param xpath the xpath expression.
     * @return the text value.
     */
    public static String getNodeValue(Document doc, String xpath) {
        int pos = xpath.lastIndexOf('@');
        String attributeName = null;
        boolean hasAttribute = false;
        if (pos > 0) {
            hasAttribute = true;
            attributeName = xpath.substring(pos + 1, xpath.length());
            xpath = xpath.substring(0, pos - 1);
        }

        XPathExpression xpls = new XPathExpression(doc.getDocumentElement(), xpath);
        Vector<Node> result = xpls.getResult();
        for (Enumeration<Node> e = result.elements(); e.hasMoreElements();) {
            Node obj = e.nextElement();
            if (obj instanceof Element) {
                if (hasAttribute) {
                    return ((Element) obj).getAttribute(attributeName);
                } else {
                    return ((Element) obj).getTextContent();
                }
            }
        }
        return "";
    }
	
	/**
	 * Gets the value of the description template form an xforms model document.
	 * 
	 * @param node the root node of the xml document.
	 * @param template the description template
	 * @return
	 */
	public static String getDescriptionTemplate(Element node, String template){
		if(template == null || template.trim().length() == 0)
			return null;
		
//		String s = "Where does ${name}$ come from?";
		String f,v,text = template;

		int startIndex,j,i = 0;
		do{
			startIndex = i; //mark the point where we found the first $ character.

			i = text.indexOf("${",startIndex); //check the opening $ character
			if(i == -1)
				break; //token not found.

			j = text.indexOf("}$",i+1); //check the closing $ character
			if(j == -1)
				break; //closing token not found. possibly wrong syntax.

			f = text.substring(0,i); //get the text before token
			v = getValue(node,text.substring(i+2, j)); //append value of token.

			f += (v == null) ? "" : v;
			f += text.substring(j+2, text.length()); //append value after token.

			text = f;

		}while (true); //will break out when dollar symbols are out.

		return text;
	}
	
	/**
	 * Gets the text value of a node as pointed to by an xpath expression in an xml document
	 * whose root node is given.
	 * 
	 * @param node the root node.
	 * @param xpath the xpath expression.
	 * @return the text value.
	 */
	private static String getValue(Element node, String xpath){
		int pos = xpath.lastIndexOf('@'); String attributeName = null;
		if(pos > 0){
			attributeName = xpath.substring(pos+1,xpath.length());
			xpath = xpath.substring(0,pos-1);
		}
		
		XPathExpression xpls = new XPathExpression(node, xpath);
		Vector<?> result = xpls.getResult();

		for (Enumeration<?> e = result.elements(); e.hasMoreElements();) {
			Object obj = e.nextElement();
			if (obj instanceof Element){
				if(pos > 0) //Check if we are to set attribute value.
					return ((Element) obj).getAttribute(attributeName);
				else
					return ((Element) obj).getTextContent();
			}
		}
		
		return null;
	}
	
    public static Document createNewXmlDocument() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException ex) {
            throw new UnexpectedException(ex);
        }
    }
}
