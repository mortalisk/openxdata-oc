package org.openxdata.client.views;

import org.openxdata.client.controllers.ItemImportController;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.client.util.StudyImport;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.StudyDef;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.gwt.core.client.GWT;

public class ItemImportView extends ActionOptionView {

	private Editable editable;
	
	final ItemImportController controller = (ItemImportController) this.getController();
	private boolean importingStudyOnly;
	
	public ItemImportView(Controller controller) {
		super(controller);
	}

	protected void initialize(){
		super.initialize();
		((UploadFileFormPanel)formPanel).initialize();
		
		firstRadio.setToolTip(appMessages.importStudyTooltip());
		secondRadio.setToolTip(appMessages.importFormTooltip());
		thirdRadio.setToolTip(appMessages.importFormVersionTooltip());
	}
	
	@Override
	protected FormPanel createFormPanel() {
		FormPanel formPanel = new UploadFileFormPanel() {
			@Override
            public void handleUploadedFile(String fileContents) {
				ProgressIndicator.showProgressBar();
				Editable editable = StudyImport.importStudyItem(fileContents);
				controller.setEditable(editable);
				processImportedItem();
            }			
		};
		return formPanel;
	}
		
	@Override
	protected void handleEvent(AppEvent event) {
		
        GWT.log("ItemImportView : handleEvent");
        super.handleEvent(event);
        this.editable = event.getData("editable");
        
        if(editable != null){
			this.form = ((FormDefVersion) editable).getFormDef();
			this.formVersion = (FormDefVersion) editable;
			updateRadioButtons();

			// We cannot specify the name of the study because we do not know it before hand.
			firstRadio.setBoxLabel(getFirstRadioLabel());
        }
        else{
        	
        	// Change heading to reflect we only importing a study.
        	window.setHeading(appMessages.importStudy());
        	this.importingStudyOnly = true;
        	
        	// Radios not needed.
        	firstRadio.hide();
        	secondRadio.hide();
        	thirdRadio.hide();
        	
        	// Enable the import button here.
        	execButton.enable();
        	
        	// Workaround for removing trailing opaque background.
        	window.center();
        }
	}
	
	protected void updateRadioButtons(){
		firstRadio.setBoxLabel(getFirstRadioLabel());
		secondRadio.setBoxLabel(getSecondRadioLabel()+" (" + appMessages.intoStudy()+ " " +getStudyName()+")");
		thirdRadio.setBoxLabel(getThirdRadioLabel()+" ("+ appMessages.intoForm()+ " " + getFormName(false)+")");
	}

	@Override
 	protected void action() {
		((UploadFileFormPanel)formPanel).uploadFile();
	}
	
	public void importSuccess() {
		ProgressIndicator.hideProgressBar();
		formPanel.clear();
		closeWindow();
		MessageBox.info(appMessages.success(), appMessages.importSuccess(), null);
	}

	@Override
	String getExecuteButtonLabel() {
		return appMessages.importX();
	}
	
	@Override
	String getHeading() {
		return appMessages.importEditable();
	}

	@Override
	String getThirdRadioLabel() {
		return appMessages.importFormVersion();
	}

	@Override
	String getSecondRadioLabel() {
		return appMessages.importForm();
	}

	@Override
	String getFirstRadioLabel() {
		return appMessages.importStudy();
	}

	
	
	private void processImportedItem() {
		
		GWT.log("ItemImportView : Processing imported Item for Saving.");
		if(firstRadio.getValue() || importingStudyOnly){
			controller.createStudy();
		}
		
		else if(editable != null){
			if (secondRadio.getValue()) {
				StudyDef study = ((FormDefVersion) editable).getFormDef().getStudy();
				controller.addFormToStudy(study);
				
			}
			if(thirdRadio.getValue()){
				FormDef form = ((FormDefVersion)editable).getFormDef();
				controller.addFormVersionToForm(form);			
			}					
		}
		else{
			MessageBox.alert(appMessages.listOfForms(),
					appMessages.formMustBeSelected(), null);
		}
	}
}
