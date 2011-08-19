package org.openxdata.server.admin.client.util;

import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.StudyDef;
import org.purc.purcforms.client.xforms.XmlUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;

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
		
		GWT.log(formDefVersion.getXform());
	}
}
