package org.openxdata.client.views;

import org.openxdata.client.AppMessages;
import org.openxdata.client.util.DesignerUtilities;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.FormDefVersionText;
import org.purc.purcforms.client.FormDesignerWidget;
import org.purc.purcforms.client.controller.IFormSaveListener;
import org.purc.purcforms.client.util.LanguageUtil;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.xml.client.XMLParser;

/**
 * 
 * Encapsulates functionality for Loading the Form Designer.
 */
public class FormDesignerView {

    final AppMessages appMessages = GWT.create(AppMessages.class);
    
    /** The form designer widget. */
    private FormDesignerWidget formDesigner;
    
    /** The formDesignerWindow for the form designer */
    Window formDesignerWindow;
    
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
     * @param formDef - Form Definition to create.
     * @param formDefVersion - Form Definition Version to create.
     */
    public void openForNewForm(FormDefVersion formDefVersion) {
        createFormDesignerWidget();
        
        designForm(formDefVersion, false);
        createFormDesignerWindow(formDefVersion.getFormDef().getName(), newStudyFrmWindowListener);
    }

    /**
     * Opens the designer with a given Form for editing.
     * 
     * @param formDefVersion Form Definition to editing.
     * @param readOnly If it should be opened in readOnly mode.
     */
    public void openFormForEditing(FormDefVersion formDefVersion, Boolean readOnly) {
        createFormDesignerWidget();

        designForm(formDefVersion, readOnly);
        
        createFormDesignerWindow(formDefVersion.getFormDef().getName(), editStudyFormWindowListener);
    }

    /**
     * Loads the given FormDef in the PurcForms designer.
     * 
     * @param formDefVersion Form to load
     * @param readOnly If it is to be opened in readOnly mode -- cannot edit in this mode!
     */
	private void designForm(FormDefVersion formDefVersion, Boolean readOnly) {
		String formName = formDefVersion.getFormDef().getName();
        String formVersionName = formDefVersion.getName();

        // get the xforms and layout xml
        String xform = formDefVersion.getXform();
        String layout = formDefVersion.getLayout();
        
        // if not empty load it in the form designer for editing
        if (xform != null && xform.trim().length() > 0) {
            if (!DesignerUtilities.checkMatching(formDefVersion)) {
                    xform = DesignerUtilities.changeName(formDefVersion);
            }
            // If the form was localised for the current locale, then translate
            // it to the locale.
            FormDefVersionText text = formDefVersion.getFormDefVersionText("en");
            if (text != null) {

                xform = LanguageUtil.translate(XMLParser.parse(xform),
                        XMLParser.parse(text.getXformText()).getDocumentElement());

                if (layout != null && layout.trim().length() > 0) {
                    layout = LanguageUtil.translate(XMLParser.parse(layout),
                            XMLParser.parse(text.getLayoutText()).getDocumentElement());
                }
            }
            formDesigner.loadForm(formDefVersion.getFormDefVersionId(), xform, layout, "", readOnly);
        } else {
            formDesigner.addNewForm(formName + "_" + formVersionName, 
            		DesignerUtilities.getDefaultFormBinding(formDefVersion), formDefVersion.getFormDefVersionId());
        }
	}

    /**
     * Creates the gxt specific modal window in which to embed the Purcforms FormDesigner.
     * 
     * @param formName Name of Form Definition being manipulated.
     * @param beforeHide Event that is handled by this modal window when window is closing.
     */
    private void createFormDesignerWindow(String formName, Listener<? extends BaseEvent> beforeHide) {
        formDesignerWindow = new Window();
        formDesignerWindow.setPlain(true);
        formDesignerWindow.setHeading(appMessages.designForm() + " : "
                + formName);
        formDesignerWindow.setMaximizable(true);
        formDesignerWindow.setMinimizable(false);
        formDesignerWindow.setDraggable(false);
        formDesignerWindow.setResizable(false);
        formDesignerWindow.setModal(true);
        formDesignerWindow.setSize(
                com.google.gwt.user.client.Window.getClientWidth(),
                com.google.gwt.user.client.Window.getClientHeight());
        formDesignerWindow.add(formDesigner);
        formDesignerWindow.setScrollMode(Scroll.AUTO);
        formDesignerWindow.setModal(true);

        formDesignerWindow.show();
        formDesignerWindow.maximize();
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
    
    /**
     * Conceals the Form Designer window from the User.
     */
    public void hide() {
        formDesignerWindow.hide();
        ProgressIndicator.hideProgressBar();
    }
}
