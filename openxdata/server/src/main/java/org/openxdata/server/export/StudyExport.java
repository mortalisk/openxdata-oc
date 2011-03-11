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
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.util.XmlUtil;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Exports a study and all its contents to xml.
 * 
 * @author daniel
 *
 */
public class StudyExport {

    /**
     * Exports a study and all its contents to xml.
     *
     * @param studyDef the study to export.
     * @return the xml representation of the study and all its contents.
     */
    public static String export(StudyDef studyDef) {
        Assert.notNull(studyDef, "studyDef should not be null!");
        Document doc = XmlUtil.createNewXmlDocument();

        Element studyNode = doc.createElement("study");
        studyNode.setAttribute("name", studyDef.getName());
        studyNode.setAttribute("description", studyDef.getDescription());
        studyNode.setAttribute("studyKey", studyDef.getStudyKey() == null ? "" : studyDef.getStudyKey());
        doc.appendChild(studyNode);

        List<FormDef> forms = studyDef.getForms();
        if (forms != null) {
            for (FormDef formDef : forms) {
                FormExport.export(formDef, studyNode);
            }
        }

        return XmlUtil.doc2String(doc);
    }
}
