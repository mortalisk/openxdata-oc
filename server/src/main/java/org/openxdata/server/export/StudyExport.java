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
