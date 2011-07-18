package org.openxdata.client.views;

import org.openxdata.client.controllers.DeleteStudyFormController;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;

/**
 * Encapsulates UI functionality for Editing a given Study/Form/Form version.
 */
public class DeleteStudyFormView extends ActionOptionView {
	
	private FormDef form;
	private FormDefVersion formVersion;
    
	public DeleteStudyFormView(Controller controller) {
		super(controller);
	}
	
	private void delete() {
            final DeleteStudyFormController controller1 = (DeleteStudyFormController) this.getController();
            
            MessageBox.confirm(appMessages.delete(), appMessages.areYouSureDelete(), new Listener<MessageBoxEvent>() {

                @Override
                public void handleEvent(MessageBoxEvent be) {
                    if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                        ProgressIndicator.showProgressBar();
                        if (firstRadio.getValue()) {
                            controller1.delete(form.getStudy());
                        } else if (secondRadio.getValue()) {
                            controller1.delete(form);
                        } else if (thirdRadio.getValue()) {
                            controller1.delete(formVersion);
                        }
                    }
                }
            });

	}
	
	@Override
	protected void handleEvent(AppEvent event) {
        GWT.log("DeleteStudyFormView : handleEvent");
        if (event.getType() == DeleteStudyFormController.DELETESTUDYFORM) {
            GWT.log("DeleteStudyFormView : DeleteStudyFormController.DELETESTUDYFORM: Delete");

            formVersion = event.getData("formVersion");
            form = event.getData("formDef");

            // Initialize Window
            firstRadio.setBoxLabel(firstRadio.getBoxLabel()+" - "+form.getStudy().getName());
            secondRadio.setBoxLabel(secondRadio.getBoxLabel()+" - "+form.getName());
            if (formVersion != null) {
            	thirdRadio.setBoxLabel(thirdRadio.getBoxLabel()+" - "+formVersion.getName());
            	thirdRadio.show();
            } else {
            	thirdRadio.hide();
            }
            window.setAutoHeight(true);
            window.setWidth(425);
            window.setPlain(true);
            window.setHeading(appMessages.deleteStudyOrForm());
            window.add(formPanel);
            window.setDraggable(true);
            window.setResizable(true);

            window.addButton(execButton);
            window.addButton(cancelButton);

            window.show();
            ProgressIndicator.hideProgressBar();
        }

    }
	   
    public void checkItemHasData(Editable item){
        ((DeleteStudyFormController) this.getController()).itemHasData(item);
    }
    
    public void onItemDataCheckComplete(Boolean hasData) {
        if (!hasData) {
            delete();
        } else {
            MessageBox.alert(appMessages.viewResponses(),appMessages.unableToDeleteFormWithData(), null);
        }
    }

	@Override
	protected void action() {
		if (firstRadio.getValue()) {
			checkItemHasData(form.getStudy());
		} else if (secondRadio.getValue()) {
			checkItemHasData(form);
		} else if (thirdRadio.getValue()) {
			checkItemHasData(formVersion);
		}
	}

	@Override
	String getThirdRadioLabel() {
		return appMessages.deleteFormVersion();
	}

	@Override
	String getSecondRadioLabel() {
		return appMessages.deleteForm();
	}

	@Override
	String getFirstRadioLabel() {
		return appMessages.deleteStudy();
	}

	@Override
	String getExecuteButtonLable() {
		return appMessages.delete();
	}

	@Override
	String getHeading() {
		return appMessages.deleteStudyOrForm();
	}
}