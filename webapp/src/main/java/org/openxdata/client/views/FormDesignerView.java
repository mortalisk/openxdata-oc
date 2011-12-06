package org.openxdata.client.views;

import org.openxdata.client.AppMessages;
import org.openxdata.client.Emit;
import org.openxdata.client.controllers.FormDesignerController;
import org.openxdata.client.util.FormDefVersionUtil;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.FormDefVersionText;
import org.purc.purcforms.client.FormDesignerWidget;
import org.purc.purcforms.client.controller.IFormSaveListener;
import org.purc.purcforms.client.util.LanguageUtil;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.xml.client.XMLParser;

/**
 * 
 * Encapsulates functionality for Loading the Form Designer.
 */
public class FormDesignerView extends View implements IFormSaveListener {

	final AppMessages appMessages = GWT.create(AppMessages.class);

	/** The form designer widget. */
	private FormDesignerWidget formDesigner;
	
	private FormDefVersion formDefVersion;
	
	private FormdesignerContainer container;

	public FormDesignerView(FormDesignerController controller, FormDefVersion formDefVersion) {
		super(controller);
		this.formDefVersion = formDefVersion;
	}

	/**
	 * Creates the purcforms FormDesigner Widget.
	 */
	private void createFormDesignerWidget() {
		formDesigner = new FormDesignerWidget(false, true, true);
		formDesigner.setSplitPos("20%");
		formDesigner.setFormSaveListener(this);
		int width = com.google.gwt.user.client.Window.getClientWidth() - 50;
		int height = com.google.gwt.user.client.Window.getClientHeight() - 105;
		String widthSt = String.valueOf(width);
		String heightSt = String.valueOf(height);
		formDesigner.setSize(widthSt, heightSt);
		formDesigner.onWindowResized(width, height);
	}

	/**
	 * Launches the designer with intent to create a new Form.
	 */
	public void openForNewForm() {
		createFormDesignerWidget();
		designForm(formDefVersion, false);
		createFormDesignerWindow(formDefVersion);
	}

	/**
	 * Opens the designer with a given Form for editing.
	 * @param readOnly boolean true if it should be opened in readOnly mode.
	 */
	public void openFormForEditing(Boolean readOnly) {
		createFormDesignerWidget();
		designForm(formDefVersion, readOnly);
		createFormDesignerWindow(formDefVersion);
	}
	
	/**
	 * Updates the local reference to FormDefVersion to the one saved in the database
	 * and displays a confirmation message
	 * 
	 * @param formDefVersion
	 */
	public void savedFormDefVersion(FormDefVersion formDefVersion) {
		this.formDefVersion = formDefVersion;
		MessageBox.info(appMessages.success(), appMessages.saveSuccess(), null);
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
		String javaScriptSrc = formDefVersion.getJavaScriptSrc();

		// if not empty load it in the form designer for editing
		if (xform != null && xform.trim().length() > 0) {
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
			GWT.log("loading existing form for editing");
			formDesigner.loadForm(formDefVersion.getId(), xform, layout, javaScriptSrc, readOnly);
		} else {
			GWT.log("loading new form for creation");
			formDesigner.addNewForm(formName + "_" + formVersionName,
					FormDefVersionUtil.generateDefaultFormBinding(formDefVersion), formDefVersion.getId());
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
	private void createFormDesignerWindow(FormDefVersion formDefVersion) {
		((Viewport)Registry.get(Emit.VIEWPORT)).hide();
		String fullName = formDefVersion.getFormDef().getStudy().getName() + "-" + formDefVersion.getFormDef().getName() + "-" + formDefVersion.getName();
		container = new FormdesignerContainer(formDesigner, fullName);
		RootPanel.get().add(container);
        ProgressIndicator.hideProgressBar();
	}

	@Override
	public boolean onSaveForm(int formId, String xformsXml, String layoutXml, String javaScriptSrc) {
		try {
			if (formDefVersion == null) {
				MessageBox.alert(appMessages.error(), appMessages.removeFormIdAttribute(), null);
				return false;
			}

			formDefVersion.setXform(xformsXml);
			formDefVersion.setLayout(layoutXml);
			formDefVersion.setJavaScriptSrc(javaScriptSrc);
			formDefVersion.setDirty(true);

			return true;
			// ?? We shall use the onSaveLocaleText() such that we avoid double saving
		} catch (Exception ex) {
			MessageBox.alert(appMessages.error(), appMessages.pleaseTryAgainLater(ex.getMessage()), null);
			return false;
		}
	}

	@Override
	public void onSaveLocaleText(int formId, String xformsLocaleText, String layoutLocaleText) {
		try {
			if (formDefVersion == null) {
				MessageBox.alert(appMessages.error(), appMessages.selectFormVersion(), null);
				return;
			}

			FormDefVersionText formDefVersionText = formDefVersion.getFormDefVersionText("en");
			if (formDefVersionText == null) {
				formDefVersionText = new FormDefVersionText("en", xformsLocaleText, layoutLocaleText);
				formDefVersion.addVersionText(formDefVersionText);
			} else {
				formDefVersionText.setXformText(xformsLocaleText);
				formDefVersionText.setLayoutText(layoutLocaleText);
			}
			formDefVersion.setDirty(true);
			
			((FormDesignerController)this.getController()).saveForm(formDefVersion);
		} catch (Exception ex) {
			MessageBox.alert(appMessages.error(), appMessages.pleaseTryAgainLater(ex.getMessage()), null);
		}
	}

	@Override
    protected void handleEvent(AppEvent event) {
		GWT.log("NewStudyFormView : handleEvent");
		if (event.getType() == FormDesignerController.NEW_FORM) {
			openForNewForm();
		} else if (event.getType() == FormDesignerController.EDIT_FORM) {
			openFormForEditing(false);
		} else if (event.getType() == FormDesignerController.READONLY_FORM) {
			openFormForEditing(true);
		}
    }
	
	public void finishSaving() {
		container.finishCloseButtonAction();
	}
	
	public void abortSaving() {
		container.setCloseDesigner(false);
	}
}
