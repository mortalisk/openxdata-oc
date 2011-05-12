package org.openxdata.client.views;

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
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.purc.purcforms.client.locale.LocaleText;

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

    public void init(FormDef formDef, FormDefVersion formDefVersion) {
        formDesigner = new FormDesignerWidget(false, true, true);
        formDesigner.setSplitPos("20%");
        formDesigner.setFormSaveListener(saveListener);

        String formName = formDef.getName();
        String formVersionName = formDefVersion.getName();
        Integer formVersionId = formDef.getDefaultVersion().getFormDefVersionId();
        String formBinding = "binding";

        formDesigner.addNewForm(formDef.getName() + "_" + formVersionName, formBinding,
                formVersionId);

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
        // FIXME: note there are some issues with the purcform widget if you
        // allow the formDesignerWindow to be resized (i.e. more than one open
        // at a time)
        formDesignerWindow.setScrollMode(Scroll.AUTO);
        formDesignerWindow.addListener(Events.BeforeHide,
                newStudyFrmWindowListener);
        formDesignerWindow.setModal(true);

        formDesigner.onWindowResized(
                com.google.gwt.user.client.Window.getClientWidth() - 100,
                com.google.gwt.user.client.Window.getClientHeight() - 75);

        formDesignerWindow.show();
        formDesignerWindow.maximize();
    }

    public void hide() {
        formDesignerWindow.removeListener(Events.BeforeHide,
                newStudyFrmWindowListener);
        formDesignerWindow.hide();
    }
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
