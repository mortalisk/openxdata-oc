package org.openxdata.client.util;

import org.openxdata.client.AppMessages;
import org.openxdata.server.admin.model.Exportable;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.purc.purcforms.client.util.FormDesignerUtil;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.xml.client.Attr;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * Encapsulates utility functions used by the PurcForms Designer.
 * 
 */
public class DesignerUtilities {

	static AppMessages appMessages = GWT.create(AppMessages.class);
	
	/**
	 * Creates a unique form binding.
	 * 
	 * @param formDefVersion the form definition-version object we manipulating.
	 * 
	 * @return the form binding.
	 */
	public static String getDefaultFormBinding(FormDefVersion formDefVersion){
		FormDef formDef = formDefVersion.getFormDef();
		String binding = formDef.getStudy().getName() + "_" + formDef.getName() + "_" + formDefVersion.getName();
		return FormDesignerUtil.getXmlTagName(binding);
	}
	
	/**
	 * Exports the given Editable. Right now we only export studies.
	 * 
	 * @param editable Editable to export.
	 * @param fileName File Name for the item to export.
	 */
	public static void exportEditable(final Exportable exportable, final String fileName){

		ProgressIndicator.showProgressBar();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				try{
					Integer id = exportable.getId();
					String type = exportable.getType();

					String url = "studyexport?";
					url += "type=" + type;
					url += "&id=" + id;
					url += "&filename=" + fileName;

					Window.Location.replace(URL.encode(url));

					ProgressIndicator.hideProgressBar();	
				}
				catch(Exception ex){
					MessageBox.alert(appMessages.error(), appMessages.exportError(), null);
					ProgressIndicator.hideProgressBar();	
				}	
			}
		});
	}

        /**
         * Checks whether the xform of the FormDefVersion binding needs to be
         * changed.
         * @param formDefVersion
         * @return boolean whether xform needs to be changed or not
         */
        public static boolean checkMatching(FormDefVersion formDefVersion) {
            Document xformDocument = XMLParser.parse(formDefVersion.getXform());
            NodeList instanceList = xformDocument.getElementsByTagName("instance");
            Node instanceRoot = instanceList.item(0).getChildNodes().item(1);
            Node nameNode = instanceRoot.getAttributes().getNamedItem("name");

            String name = nameNode.getNodeValue();
            if (formDefVersion.isNew() && !formDefVersion.getName().equals(name)) {
                return false;
            } else {
                return false;
            }
        }

        /**
         * Changes the name it in the xml of a FormDefVersion that needs a name
         * change
         * @param formDefVersion FormDefVersion that has had its name changed
         * @return
         */
        public static String changeName(FormDefVersion formDefVersion) {
            Document xformDocument = XMLParser.parse(formDefVersion.getXform());
            NodeList instanceList = xformDocument.getElementsByTagName("instance");
            Node instanceNode = instanceList.item(0);
            Node instanceRoot = instanceNode.getChildNodes().item(1);
            Node nameNode = instanceRoot.getAttributes().getNamedItem("name");

            String newName = getDefaultFormBinding(formDefVersion);
            String oldName = instanceRoot.getNodeName();

            // do all changes
            resetIdValue(instanceNode, newName);
            resetFormKeyValue(instanceRoot, newName);
            changeDescriptionTemplateValue(instanceRoot, oldName, newName);
            nameNode.setNodeValue(formDefVersion.getFormDef().getName() + "_" + formDefVersion.getName());
            replaceInstaceRoot(xformDocument, newName, instanceRoot);
            changeNodesetOnBindElements(xformDocument, oldName, newName);

            String xform = xformDocument.toString();
            formDefVersion.setXform(xform);
            return xform;
        }

    private static void changeDescriptionTemplateValue(Node instanceRoot, String oldName, String newName) {
        Node descriptionNode = instanceRoot.getAttributes().getNamedItem("description-template");
        if (descriptionNode != null) {
            String value = descriptionNode.getNodeValue();
            value = value.replaceAll(oldName, newName);
            descriptionNode.setNodeValue(value);
        }
    }

    private static void resetFormKeyValue(Node instanceRoot, String newName) {
        Node formKeyNode = instanceRoot.getAttributes().getNamedItem("formKey");
        if (formKeyNode != null) {
            formKeyNode.setNodeValue(newName);
        }
    }

    private static void resetIdValue(Node instanceNode, String newName) {
        Node idNode = instanceNode.getAttributes().getNamedItem("id");
        if (idNode != null) {
            idNode.setNodeValue(newName);
        }
    }

    private static void changeNodesetOnBindElements(Document xformDocument, String oldName, String newName) {
        // Change the nodeset attribute on bind-elements to reflect new instance root name
        NodeList bindElements = xformDocument.getElementsByTagName("bind");
        for (int i = 0; i < bindElements.getLength(); i++) {
            Node bind = bindElements.item(i);
            Node nodeset = bind.getAttributes().getNamedItem("nodeset");
            String nodesetValue = nodeset.getNodeValue();
            nodesetValue = nodesetValue.replaceAll(oldName, newName);
            nodeset.setNodeValue(nodesetValue);
        }
    }

    private static void replaceInstaceRoot(Document xformDocument, String newName, Node instanceRoot) {
        // We need to create a new element to replace the old name
        // Renaming is not allowed in DOM
        Element newInstanceRoot = xformDocument.createElement(newName);
        // Copy the attributes to the new element
        NamedNodeMap attrs = instanceRoot.getAttributes();
        for (int i=0; i<attrs.getLength(); i++) {
            Attr attr2 = (Attr)xformDocument.importNode(attrs.item(i), true);
            newInstanceRoot.setAttribute(attr2.getName(), attr2.getValue());
        }
        // Move all the children
        while (instanceRoot.hasChildNodes()) {
            newInstanceRoot.appendChild(instanceRoot.getFirstChild());
        }
        // Replace the new node for the old one
        instanceRoot.getParentNode().replaceChild(newInstanceRoot, instanceRoot);
    }

}
