package org.openxdata.server.export;

import org.openxdata.server.admin.model.FormDefVersionText;
import org.openxdata.server.util.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger log = LoggerFactory.getLogger(VersionTextExport.class);

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
