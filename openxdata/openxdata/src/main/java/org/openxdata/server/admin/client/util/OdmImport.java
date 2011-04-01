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
package org.openxdata.server.admin.client.util;

import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.StudyDef;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import org.purc.purcforms.client.xforms.XmlUtil;

/**
 * Imports studies in ODM format
 * 
 * @author daniel
 *
 */
public class OdmImport {

	/**
	 * Imports a study, form, or form version from xml.
	 * 
	 * @param xml the xml text.
	 * @return study, form or form version, depending on what is in the xml text.
	 */
	public static Editable importStudyItem(Document doc){
		Element root = doc.getDocumentElement();
		NodeList nodes = root.getElementsByTagName("Study");
		if(nodes == null || nodes.getLength() == 0)
			return null;
		
		StudyDef studyDef = new StudyDef();
		Element studyNode = (Element)nodes.item(0);
		studyDef.setStudyKey(studyNode.getAttribute("OID"));
		
		nodes = root.getElementsByTagName("StudyName");
		studyDef.setName(XmlUtil.getTextValue((Element)nodes.item(0)));
		
		nodes = root.getElementsByTagName("StudyDescription");
		studyDef.setDescription(XmlUtil.getTextValue((Element)nodes.item(0)));
		
		Element metaDataVersionNode = (Element)root.getElementsByTagName("MetaDataVersion").item(0);
		
		nodes = root.getElementsByTagName("FormDef");
		for(int index = 0; index < nodes.getLength(); index++)
			importFormItem((Element)nodes.item(index),studyDef,metaDataVersionNode);
		
		return studyDef;
	}
	
	public static void importFormItem(Element node, StudyDef studyDef, Element metaDataVersionNode){
		String name = node.getAttribute("Name");
		
		int pos = name.lastIndexOf("-");
		FormDef formDef = new FormDef(0,name.substring(0, pos - 1),studyDef);
		studyDef.addForm(formDef);
		
		FormDefVersion formDefVersion = new FormDefVersion(0,name.substring(pos + 2),formDef);
		formDef.addVersion(formDefVersion);
		
		formDefVersion.setXform(OdmXformImport.importXform(node,metaDataVersionNode));
		
		System.out.println(formDefVersion.getXform());
	}
}
