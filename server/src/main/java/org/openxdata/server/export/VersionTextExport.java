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
package org.openxdata.server.export;

import org.apache.log4j.Logger;
import org.openxdata.server.admin.model.FormDefVersionText;
import org.openxdata.server.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Exports a form version text and all its contents to xml.
 * 
 * @author daniel
 *
 */
public class VersionTextExport {

	/** The logger. */
	private static final Logger log = Logger.getLogger(VersionTextExport.class);

	/**
	 * Exports a form version text and all its contents to xml and then adds them as a child of
	 * a given parent node.
	 * 
	 * @param formDefVersionText the form version text to export.
	 * @param parentNode the parent node to which to add the child.
	 * @return the xml representation of the resultant document.
	 */
	public static String export(FormDefVersionText formDefVersionText,Element parentNode){
		try{
			if(formDefVersionText != null && parentNode != null){
				Document doc = parentNode.getOwnerDocument();

				Element versionTextNode = doc.createElement("versionText");
				String text = formDefVersionText.getLocaleKey();
				versionTextNode.setAttribute("locale", text != null ? text : "");
				parentNode.appendChild(versionTextNode);

				Element xformNode = doc.createElement("xform");
				text = formDefVersionText.getXformText();
				xformNode.appendChild(doc.createTextNode(text != null ? text : ""));
				versionTextNode.appendChild(xformNode);

				Element layoutNode = doc.createElement("layout");
				text = formDefVersionText.getLayoutText();
				layoutNode.appendChild(doc.createTextNode(text != null ? text : ""));
				versionTextNode.appendChild(layoutNode);

				return XmlUtil.doc2String(doc);
			}
		}
		catch(Exception ex){
			log.error(ex.getMessage(), ex);
			//ex.printStackTrace();
		}

		return null;
	}
}
