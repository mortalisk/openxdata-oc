package org.openxdata.client.views;

import org.openxdata.client.controllers.DeleteStudyFormController;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.model.Editable;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;

public class DeleteStudyFormView extends ActionOptionView {
    
	final DeleteStudyFormController controller = (DeleteStudyFormController) this.getController();
	public DeleteStudyFormView(Controller controller) {
		super(controller);
	}
	
	@Override
	protected void handleEvent(AppEvent event) {
		super.handleEvent(event);
		GWT.log("DeleteStudyFormView : handleEvent");
		if (event.getType() == DeleteStudyFormController.DELETESTUDYFORM) {
			formVersion = event.getData("formVersion");
			form = event.getData("formDef");
		}
		updateRadioButtons();
	}

	private void delete() {

            MessageBox.confirm(appMessages.delete(), appMessages.areYouSureDelete(), new Listener<MessageBoxEvent>() {

                @Override
                public void handleEvent(MessageBoxEvent be) {
                    if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                        ProgressIndicator.showProgressBar();
                        if (firstRadio.getValue()) {
                        	controller.delete(form.getStudy());
                        } else if (secondRadio.getValue()) {
                        	controller.delete(form);
                        } else if (thirdRadio.getValue()) {
                        	controller.delete(formVersion);
                        }
                    }
                }
            });

	}
	
    public void checkItemHasData(Editable item){
    	controller.itemHasData(item);
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
	String getExecuteButtonLabel() {
		return appMessages.delete();
	}

	@Override
	String getHeading() {
		return appMessages.deleteStudyOrForm();
	}
}