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

import java.util.List;

import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.util.XmlUtil;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Exports a form and all its contents to xml.
 * 
 * @author daniel
 *
 */
public class FormExport {

	/**
	 * Exports a form and all its contents to xml.
	 * 
	 * @param formDef the form to export.
	 * @return the xml representation of the resultant document.
	 */
	public static String export(FormDef formDef){
		return export(formDef,null);
	}

    /**
     * Exports a form and all its contents to xml and then adds them as a child of
     * a given parent node.
     *
     * @param formDef the form to export.
     * @param parentNode the parent node to which to add the child.
     * @return the xml representation of the resultant document.
     */
    public static String export(FormDef formDef, Element parentNode) {
        Assert.notNull(formDef, "FormDef can not be null!");

        Document doc = null;
        if (parentNode == null) {
            doc = XmlUtil.createNewXmlDocument();
        } else {
            doc = parentNode.getOwnerDocument();
        }

        Element formNode = doc.createElement("form");
        formNode.setAttribute("name", formDef.getName());
        formNode.setAttribute("description", formDef.getDescription());

        if (parentNode == null) {
            doc.appendChild(formNode);
        } else {
            parentNode.appendChild(formNode);
        }

        List<FormDefVersion> versions = formDef.getVersions();
        if (versions != null) {
            for (FormDefVersion formDefVersion : versions) {
                VersionExport.export(formDefVersion, formNode);
            }
        }

        return XmlUtil.doc2String(doc);
    }
}
