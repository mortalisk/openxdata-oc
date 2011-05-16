package org.openxdata.client.views;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.core.client.GWT;
import org.openxdata.client.AppMessages;
import org.purc.purcforms.client.FormDesignerWidget;
import org.purc.purcforms.client.controller.IFormSaveListener;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.xml.client.XMLParser;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.FormDefVersionText;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.util.LanguageUtil;

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
    public void openForNewForm(FormDef formDef, FormDefVersion formDefVersion) {
        createFormDesignerWidget();
        
        String formName = formDef.getName();
        String formVersionName = formDefVersion.getName();
        Integer formVersionId = formDef.getDefaultVersion().getFormDefVersionId();
        String studyName = formDef.getStudy().getName();
        String binding = studyName+"_"+formName+"_"+formVersionName;
        binding = binding.replaceAll(" ", "_");

        formDesigner.addNewForm(formDef.getName() + "_" + formVersionName, binding,
                formVersionId);
        createFormDesignerWindow(formName, newStudyFrmWindowListener);
    }

    /**
     * Opens the designer with a given Form for editing.
     * 
     * @param form Form Definition for editing.
     * @param readOnly If it should be opened in readOnly mode.
     */
    public void openFormForEditing(FormDef form, Boolean readOnly) {
        createFormDesignerWidget();

        String formName = form.getName();
        String formVersionName = form.getDefaultVersion().getName();

        // get the xforms and layout xml
        String xform = form.getDefaultVersion().getXform();
        String layout = form.getDefaultVersion().getLayout();
        // if not empty load it in the formdesigner for editing
        if (xform != null && xform.trim().length() > 0) {
            // If the form was localised for the current locale, then translate
            // it to the locale.
            FormDefVersionText text = form.getDefaultVersion().getFormDefVersionText("en");
            if (text != null) {

                xform = LanguageUtil.translate(XMLParser.parse(xform),
                        XMLParser.parse(text.getXformText()).getDocumentElement());

                if (layout != null && layout.trim().length() > 0) {
                    layout = LanguageUtil.translate(XMLParser.parse(layout),
                            XMLParser.parse(text.getLayoutText()).getDocumentElement());
                }
            }
            formDesigner.loadForm(form.getDefaultVersion().getFormDefVersionId(), xform, layout, "", readOnly);
        } else {
            formDesigner.addNewForm(formName + "_" + formVersionName, "binding",
                    form.getDefaultVersion().getFormDefVersionId());
        }

        createFormDesignerWindow(formName, editStudyFormWindowListener);
    }

    /**
     * Conceals the FormDesigner window from the User.
     */
    public void hide() {
        formDesignerWindow.removeListener(Events.BeforeHide,
                newStudyFrmWindowListener);
        formDesignerWindow.hide();
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
        formDesignerWindow.addListener(Events.BeforeHide, beforeHide);
        formDesignerWindow.setModal(true);

        formDesignerWindow.show();
        formDesignerWindow.maximize();
    }
    
    final Listener<ComponentEvent> editStudyFormWindowListener = new EditWindowListener();

    class EditWindowListener implements Listener<ComponentEvent> {

        @Override
        public void handleEvent(ComponentEvent be) {
            be.setCancelled(true);
            be.stopEvent();
            MessageBox.confirm(appMessages.cancel(), appMessages.areYouSure(),
                    new Listener<MessageBoxEvent>() {

                        @Override
                        public void handleEvent(MessageBoxEvent be) {
                            if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                                // remove the form in the designer
                                formDesignerWindow.removeListener(
                                        Events.BeforeHide,
                                        editStudyFormWindowListener);
                                formDesignerWindow.hide();
                                ProgressIndicator.hideProgressBar();
                                formDesignerWindow.addListener(
                                        Events.BeforeHide,
                                        editStudyFormWindowListener);
                                // clear the formdesigner from any pending
                                // previous form
                                // this is a hack to prevent the context from
                                // referencing a form
                                // even after closing the window,results are
                                // context sees a new loaded
                                // form with properties of a previously closed
                                // form.
                                org.purc.purcforms.client.Context.setFormDef(null);
                            }
                        }
                    });
        }
    };
    final Listener<ComponentEvent> newStudyFrmWindowListener = new WindowListener();

    class WindowListener implements Listener<ComponentEvent> {

        @Override
        public void handleEvent(ComponentEvent be) {
            be.setCancelled(true);
            be.stopEvent();
            MessageBox.confirm(appMessages.cancel(),
                    LocaleText.get("cancelFormPrompt"),
                    new Listener<MessageBoxEvent>() {

                        @Override
                        public void handleEvent(MessageBoxEvent be) {
                            if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                                formDesignerWindow.removeListener(
                                        Events.BeforeHide,
                                        newStudyFrmWindowListener);
                                formDesignerWindow.hide();
                                formDesignerWindow.addListener(
                                        Events.BeforeHide,
                                        newStudyFrmWindowListener);
                            }
                        }
                    });
        }
    };
}
