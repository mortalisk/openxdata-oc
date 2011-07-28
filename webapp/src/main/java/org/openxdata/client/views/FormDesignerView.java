package org.openxdata.client.views;

import org.openxdata.client.AppMessages;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.FormDefVersionText;
import org.purc.purcforms.client.FormDesignerWidget;
import org.purc.purcforms.client.controller.IFormSaveListener;
import org.purc.purcforms.client.util.FormDesignerUtil;
import org.purc.purcforms.client.util.LanguageUtil;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Attr;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * 
 * Encapsulates functionality for Loading the Form Designer.
 */
public class FormDesignerView {

	final AppMessages appMessages = GWT.create(AppMessages.class);

	/** The form designer widget. */
	private FormDesignerWidget formDesigner;

	private final IFormSaveListener saveListener;

	public FormDesignerView(IFormSaveListener saveListener) {
		this.saveListener = saveListener;
	}

	/**
	 * Creates the purcforms FormDesigner Widget.
	 */
	private void createFormDesignerWidget() {
		formDesigner = new FormDesignerWidget(false, true, true);
		formDesigner.setSplitPos("20%");
		formDesigner.setFormSaveListener(saveListener);
		formDesigner.onWindowResized(
				com.google.gwt.user.client.Window.getClientWidth() - 100,
				com.google.gwt.user.client.Window.getClientHeight() - 75);
	}

	/**
	 * Launches the designer with intent to create a new Form.
	 * 
	 * @param formDef
	 *            - Form Definition to create.
	 * @param formDefVersion
	 *            - Form Definition Version to create.
	 */
	public void openForNewForm(FormDefVersion formDefVersion) {
		createFormDesignerWidget();

		designForm(formDefVersion, false);
		createFormDesignerWindow(formDefVersion.getFormDef().getName(),
				newStudyFrmWindowListener);
	}

	/**
	 * Opens the designer with a given Form for editing.
	 * 
	 * @param formDefVersion
	 *            Form Definition to editing.
	 * @param readOnly
	 *            If it should be opened in readOnly mode.
	 */
	public void openFormForEditing(FormDefVersion formDefVersion,
			Boolean readOnly) {
		createFormDesignerWidget();

		designForm(formDefVersion, readOnly);

		createFormDesignerWindow(formDefVersion.getFormDef().getName(),
				editStudyFormWindowListener);
	}

	/**
	 * Loads the given FormDef in the PurcForms designer.
	 * 
	 * @param formDefVersion
	 *            Form to load
	 * @param readOnly
	 *            If it is to be opened in readOnly mode -- cannot edit in this
	 *            mode!
	 */
	private void designForm(FormDefVersion formDefVersion, Boolean readOnly) {
		String formName = formDefVersion.getFormDef().getName();
		String formVersionName = formDefVersion.getName();

		// get the xforms and layout xml
		String xform = formDefVersion.getXform();
		String layout = formDefVersion.getLayout();

		// if not empty load it in the form designer for editing
		if (xform != null && xform.trim().length() > 0) {
			if (!checkMatching(formDefVersion)) {
				xform = changeName(formDefVersion);
			}
			// If the form was localised for the current locale, then translate
			// it to the locale.
			FormDefVersionText text = formDefVersion
					.getFormDefVersionText("en");
			if (text != null) {

				xform = LanguageUtil.translate(XMLParser.parse(xform),
						XMLParser.parse(text.getXformText()).getDocumentElement());

				if (layout != null && layout.trim().length() > 0) {
					layout = LanguageUtil.translate(XMLParser.parse(layout),
							XMLParser.parse(text.getLayoutText()).getDocumentElement());
				}
			}
			formDesigner.loadForm(formDefVersion.getId(), xform, layout, "", readOnly);
		} else {
			formDesigner.addNewForm(formName + "_" + formVersionName,
					getDefaultFormBinding(formDefVersion), formDefVersion.getId());
		}
	}

	/**
	 * Creates the gxt specific modal window in which to embed the Purcforms
	 * FormDesigner.
	 * 
	 * @param formName
	 *            Name of Form Definition being manipulated.
	 * @param beforeHide
	 *            Event that is handled by this modal window when window is
	 *            closing.
	 */
	private void createFormDesignerWindow(String formName,
			Listener<? extends BaseEvent> beforeHide) {
		Widget emit = RootPanel.get().getWidget(0);
                RootPanel.get().clear();
                FormdesignerContainer container = new FormdesignerContainer(formDesigner, formName, emit);
                RootPanel.get().add(container);
	}

	final Listener<ComponentEvent> editStudyFormWindowListener = new EditWindowListener();

	class EditWindowListener implements Listener<ComponentEvent> {

		@Override
		public void handleEvent(ComponentEvent be) {
			hide();
			be.setCancelled(true);
			be.stopEvent();
			org.purc.purcforms.client.Context.setFormDef(null);
		}
	};

	final Listener<ComponentEvent> newStudyFrmWindowListener = new WindowListener();

	class WindowListener implements Listener<ComponentEvent> {

		@Override
		public void handleEvent(ComponentEvent be) {
			hide();
			be.setCancelled(true);
			be.stopEvent();
		}
	};

	public void hide() {
		ProgressIndicator.hideProgressBar();
	}

	/**
	 * Creates a unique form binding.
	 * 
	 * @param formDefVersion
	 *            the form definition-version object we manipulating.
	 * 
	 * @return the form binding.
	 */
	public String getDefaultFormBinding(FormDefVersion formDefVersion) {
		FormDef formDef = formDefVersion.getFormDef();
		String binding = formDef.getStudy().getName() + "_" + formDef.getName()
				+ "_" + formDefVersion.getName();
		return FormDesignerUtil.getXmlTagName(binding);
	}

	/**
	 * Checks whether the xform of the FormDefVersion binding needs to be
	 * changed.
	 * 
	 * @param formDefVersion
	 * @return boolean whether xform needs to be changed or not
	 */
	public boolean checkMatching(FormDefVersion formDefVersion) {
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
	 * 
	 * @param formDefVersion
	 *            FormDefVersion that has had its name changed
	 * @return
	 */
	public String changeName(FormDefVersion formDefVersion) {
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
		nameNode.setNodeValue(formDefVersion.getFormDef().getName() + "_"
				+ formDefVersion.getName());
		replaceInstanceRoot(xformDocument, newName, instanceRoot);
		changeNodesetOnBindElements(xformDocument, oldName, newName);

		String xform = xformDocument.toString();
		formDefVersion.setXform(xform);
		return xform;
	}

	private void changeDescriptionTemplateValue(Node instanceRoot,
			String oldName, String newName) {
		Node descriptionNode = instanceRoot.getAttributes().getNamedItem(
				"description-template");
		if (descriptionNode != null) {
			String value = descriptionNode.getNodeValue();
			value = value.replaceAll(oldName, newName);
			descriptionNode.setNodeValue(value);
		}
	}

	private void resetFormKeyValue(Node instanceRoot, String newName) {
		Node formKeyNode = instanceRoot.getAttributes().getNamedItem("formKey");
		if (formKeyNode != null) {
			formKeyNode.setNodeValue(newName);
		}
	}

	private void resetIdValue(Node instanceNode, String newName) {
		Node idNode = instanceNode.getAttributes().getNamedItem("id");
		if (idNode != null) {
			idNode.setNodeValue(newName);
		}
	}

	private void changeNodesetOnBindElements(Document xformDocument,
			String oldName, String newName) {
		// Change the nodeset attribute on bind-elements to reflect new instance
		// root name
		NodeList bindElements = xformDocument.getElementsByTagName("bind");
		for (int i = 0; i < bindElements.getLength(); i++) {
			Node bind = bindElements.item(i);
			Node nodeset = bind.getAttributes().getNamedItem("nodeset");
			String nodesetValue = nodeset.getNodeValue();
			nodesetValue = nodesetValue.replaceAll(oldName, newName);
			nodeset.setNodeValue(nodesetValue);
		}
	}

	private void replaceInstanceRoot(Document xformDocument, String newName,
			Node instanceRoot) {
		// We need to create a new element to replace the old name
		// Renaming is not allowed in DOM
		Element newInstanceRoot = xformDocument.createElement(newName);
		// Copy the attributes to the new element
		NamedNodeMap attrs = instanceRoot.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			Attr attr2 = (Attr) xformDocument.importNode(attrs.item(i), true);
			newInstanceRoot.setAttribute(attr2.getName(), attr2.getValue());
		}
		// Move all the children
		while (instanceRoot.hasChildNodes()) {
			newInstanceRoot.appendChild(instanceRoot.getFirstChild());
		}
		// Replace the new node for the old one
		instanceRoot.getParentNode()
				.replaceChild(newInstanceRoot, instanceRoot);
	}
}
