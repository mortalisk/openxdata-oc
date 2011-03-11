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

import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.FormDefVersionText;
import org.openxdata.server.util.XmlUtil;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Exports a form version and all its contents to xml.
 * 
 * @author daniel
 *
 */
public class VersionExport {

    /**
     * Exports a form version and all its contents to xml.
     *
     * @param formDefVersion the form version to export.
     * @return the xml representation of the resultant document.
     */
    public static String export(FormDefVersion formDefVersion) {
        return export(formDefVersion, null);
    }

    /**
     * Exports a form version and all its contents to xml and then adds them as a child of
     * a given parent node.
     *
     * @param formDefVersion the form version to export.
     * @param parentNode the parent node to which to add the child.
     * @return the xml representation of the resultant document.
     */
    public static String export(FormDefVersion formDefVersion, Element parentNode) {
        Assert.notNull(formDefVersion, "formDefVersion can not be null!");

        Document doc = null;
        if (parentNode == null) {
            doc = XmlUtil.createNewXmlDocument();
        } else {
            doc = parentNode.getOwnerDocument();
        }

        Element versionNode = doc.createElement("version");
        versionNode.setAttribute("name", formDefVersion.getName());
        versionNode.setAttribute("description", formDefVersion.getDescription());

        if (formDefVersion.getXform() != null) {
            Element xformNode = doc.createElement("xform");
            xformNode.appendChild(doc.createTextNode(formDefVersion.getXform()));
            versionNode.appendChild(xformNode);

            if (formDefVersion.getLayout() != null) {
                Element layoutNode = doc.createElement("layout");
                layoutNode.appendChild(doc.createTextNode(formDefVersion.getLayout()));
                versionNode.appendChild(layoutNode);
            }
        }

        if (parentNode == null) {
            doc.appendChild(versionNode);
        } else {
            parentNode.appendChild(versionNode);
        }

        List<FormDefVersionText> versionTexts = formDefVersion.getVersionText();
        if (versionTexts != null) {
            for (FormDefVersionText formDefVersionText : versionTexts) {
                VersionTextExport.export(formDefVersionText, versionNode);
            }
        }

        return XmlUtil.doc2String(doc);
    }
}
