package org.openxdata.server.util;

import java.util.Vector;

import org.openxdata.server.xpath.XPathExpression;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * This class contains utilities used during the translation of xforms and form layout
 * in various locales.
 * 
 * @author daniel
 *
 */
public class LanguageUtil {

	/** The xpath attribute name. */
	public static final String ATTRIBUTE_NAME_XPATH = "xpath";
	
	/** The value attribute name. */
	public static final String ATTRIBUTE_NAME_VALUE = "value";


	/**
	 * Replaces localizable text in am xml document with that in the language document.
	 * 
	 * @param srcXml the document xml.
	 * @param languageXml the language document xml.
	 * @return the new document xml after its text has been replaced with that from the language document.
	 */
	public static String translate(String srcXml, String languageXml){
		if(languageXml == null || srcXml == null)
			return srcXml;
		
		Document srcXmlDoc = XmlUtil.fromString2Doc(srcXml);
		Document langXmlDoc = XmlUtil.fromString2Doc(languageXml);
		
		if(srcXmlDoc == null || langXmlDoc == null)
			return null;
		
		return translate(srcXmlDoc,langXmlDoc.getDocumentElement());
	}
	
	
	/**
	 * Replaces localizable text in am xml document with that in the language document.
	 * 
	 * @param doc the document whose localizable text to replace.
	 * @param languageXml the parent node of the language document
	 * @return the new document xml after its text has been replaced with that from the language document.
	 */
	private static String translate(Document doc, Node parentLangNode){
		NodeList nodes = parentLangNode.getChildNodes();
		for(int index = 0; index < nodes.getLength(); index++){
			Node node = nodes.item(index);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;

			String xpath = ((Element)node).getAttribute(ATTRIBUTE_NAME_XPATH);
			String value = ((Element)node).getAttribute(ATTRIBUTE_NAME_VALUE);
			if(xpath == null || value == null)
				continue;

			Vector<?> result = new XPathExpression(doc, xpath).getResult();
			if(result != null){
				for(int item = 0; item < result.size(); item++){
					Element targetNode = (Element)result.get(item);
					int pos = xpath.lastIndexOf('@');
					if(pos > 0 && xpath.indexOf('=',pos) < 0){
						String attributeName = xpath.substring(pos + 1, xpath.indexOf(']',pos));
						targetNode.setAttribute(attributeName, value);
					}
					else
						targetNode.setTextContent(value);
				}
			}
		}
		return XmlUtil.doc2String(doc);
	}
}
