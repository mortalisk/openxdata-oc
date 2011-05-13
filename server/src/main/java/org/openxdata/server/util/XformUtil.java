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
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.openxdata.server.OpenXDataConstants;
import org.openxdata.server.admin.model.exception.UnexpectedException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * 
 * Transforms an xform to an xhtml document.
 * 
 * @author daniel
 *
 */
public class XformUtil {
	
	/**
	 * Converts an xform to an xhtml document.
	 * 
	 * @param xform the xform
	 * @return the xhtml representation of the xform.
	 */
	public static String fromXform2Xhtml(String xform) throws IOException, TransformerException {
        String xsl = getDefaultXSLT();
		StringWriter outWriter = new StringWriter();
		Source source = new StreamSource(IOUtils.toInputStream(xform,"UTF-8"));
		Source xslt = new StreamSource(IOUtils.toInputStream(xsl,"UTF-8"));
		Result result = new StreamResult(outWriter);
		
		TransformerFactory tf = TransformerFactory.newInstance();

		Transformer t = tf.newTransformer(xslt);
		t.transform(source, result);
		return outWriter.toString();
	}
	
	/**
	 * Gets the default CSS for XForms.
	 * 
	 * @return the CSS text
	 */	
	public static String getDefaultStyle(){
		return "@namespace xf url(http://www.w3.org/2002/xforms); "+
			"/* Display a red background on all invalid form controls */ "+
			"*:invalid .xf-value { background-color: red; } "+
			" "+
			"/* Display a red asterisk after all required form controls */ "+
			"*:required::after { content: '*'; color: red; } "+
			" "+
			"/* Do not render non-relevant form controls */ "+
			"*:disabled { visibility: hidden; } "+
			" "+
			"/* Display an alert message when appropriate */ "+
			"*:valid   xf|alert { display: none; } "+
			"*:invalid xf|alert { display: show; } "+
			" "+
			"/* Display the selected repeat-item with a light blue color. */ "+
			".xf-repeat-index { background-color: lightblue; } "+
			" "+
			"/* Display repeat items in a table row. */ "+
			"xf|repeat .xf-repeat-item {display: table-row;} "+
			" "+
			"/* Display each select1 and input control within a repeat as a table cell, having a thin solid border and its lable aligned centrally. */ "+
			"xf|repeat xf|select1, xf|repeat xf|input{display: table-cell; border: thin; border-style: solid; text-align: center;}" +
			" " +
			"xf|input xf|label,xf|select xf|label,xf|select1 xf|label {width: 32ex; text-align: right; vertical-align: top; padding-right: 0.5em; padding-top: 1ex; padding-bottom: 1ex;} " +
		    " "+
			"xf|item xf|label {width: 100%; text-align: left; padding-right: 0em; padding-bottom: 0ex; padding-top: 0ex;} " +
		    " "+
			"xf|select {padding-top: 1ex; padding-bottom: 1ex;} " +
		    " "+  
		    "xf|input, xf|select1, xf|select, xf|submit, xf|item { display: table-row; } "+
		    " "+
		    "xf|input xf|label, xf|select1 xf|label, xf|select xf|label { display: table-cell; } ";
	}

	//<xsl:number value="position()" format="1" />   
	/**
	 * Gets the default XSLT for transforming an XForm into an XHTML document.
	 * 
	 * @return the XSLT text
	 */
	private static String getDefaultXSLT(){
		return "<?xml version='1.0' encoding='UTF-8'?> "+
			"<xsl:stylesheet version='2.0' "+
			"xmlns:xsl='http://www.w3.org/1999/XSL/Transform' "+
			"xmlns:fn='http://www.w3.org/2005/xpath-functions' "+
			"xmlns:xf='http://www.w3.org/2002/xforms'> "+
			"<xsl:output method='xml' version='1.0' encoding='UTF-8'/> "+
			"<xsl:template match='/'> "+
			" <html xmlns='http://www.w3.org/1999/xhtml' "+
			"       xmlns:xf='http://www.w3.org/2002/xforms' "+
			"       xmlns:xsd='http://www.w3.org/2001/XMLSchema' "+
			"       xmlns:xs='http://www.w3.org/2001/XMLSchema' "+
			"       xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' "+
			"       xmlns:ev='http://www.w3.org/2001/xml-events' "+
			" > " +
			" <head> "+
			" 	<title> " +
			"		<xsl:value-of select='/xf:xforms/xf:model/xf:instance/*/@name'/> " +
			"	</title>"+
			" 	<xsl:copy-of select='/xf:xforms/xf:model' /> "+
			" </head> "+
			" <body> "+
			" 	<xsl:for-each select='/xf:xforms/*'> "+
			"   	<xsl:if test='local-name() != \"model\"'> "+
			" 			<xsl:copy-of select='.' /> "+
			"       </xsl:if> "+
			" 	</xsl:for-each> "+
			" </body> "+
			" </html> "+
			"</xsl:template> "+
			"</xsl:stylesheet> ";
	}
	
	/**
	 * Gets the javascript needed during the xforms processsing in the browser.
	 * For now the javascript we have deals with deleting of xform repeat items.
	 * 
	 * @return the javascript script.
	 */
	public static String getJavaScriptNode(){
		
		String script = "function deleteRepeatItem(id){ " +
                   "        var model = document.getElementById('modelId'); " +
                   "        var instance = model.getInstanceDocument('instanceId'); " +
                   "        var dataElement = instance.getElementsByTagName('problem_list')[0]; " +
                   "        var itemElements = dataElement.getElementsByTagName(id); " +
                   "        var cnt = itemElements.length; " +

                   "        if (cnt > 1){ " +
                   "             dataElement.removeChild(itemElements[cnt-1]); " +
                   "        } else { " +
				   " 			var values = itemElements[0].getElementsByTagName('value'); " +
				   " 			for(var i=0; i<values.length; i++) " +
				   "			values[i].childNodes[0].nodeValue = null; " +
                   "        } " +

                   "        model.rebuild(); " +
                   "        model.recalculate(); " +
                   "        model.refresh(); " +
                   "   } ";

		return script;
	}
	
	
    /**
     * Adds the form id attribute to an xform.
     *
     * @param versionId the form version id value of the xform.
     * @param xml the xforms xml contents.
     * @return the xml contents of the xforms with the form id attribute value added.
     */
    public static String addFormId2Xform(Integer versionId, String xml) {
        Document doc = XmlUtil.fromString2Doc(xml);
        NodeList elemList = doc.getDocumentElement().getElementsByTagNameNS("*", "instance");

        if (elemList == null || elemList.getLength() == 0) {
            throw new UnexpectedException("Instance node not found");
        }

        Element instance = (Element) elemList.item(0);
        NodeList forms = instance.getChildNodes();
        for (int index = 0; index < forms.getLength(); index++) {
            Node form = forms.item(index);
            if (form.getNodeType() == Node.ELEMENT_NODE) {
                ((Element) form).setAttribute(OpenXDataConstants.ATTRIBUTE_NAME_FORMID, versionId.toString());
                break;
            }
        }

        return XmlUtil.doc2String(doc);
    }
}
