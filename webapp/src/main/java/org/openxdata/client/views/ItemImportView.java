package org.openxdata.client.views;

import org.openxdata.client.controllers.ItemImportController;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.client.util.StudyImport;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.StudyDef;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

public class ItemImportView extends ActionOptionView {

	private Editable editable;
	private FileUploadField uploadFile;
	final String ACTION_URL = GWT.getModuleBaseURL() + "formopen";
	final ItemImportController controller = (ItemImportController) this.getController();
	private boolean importingStudyOnly;
	
	public ItemImportView(Controller controller) {
		super(controller);
	}

	protected void initialize(){
		super.initialize();
		
		firstRadio.setToolTip(appMessages.importStudyTooltip());
		secondRadio.setToolTip(appMessages.importFormTooltip());
		thirdRadio.setToolTip(appMessages.importFormVersionTooltip());
		
		formPanel.setAction(ACTION_URL);
		formPanel.setEncoding(Encoding.MULTIPART);
		formPanel.setMethod(Method.POST);
		
		uploadFile =  new FileUploadField();
		uploadFile.setAllowBlank(false);
		uploadFile.setName("filecontents");
		uploadFile.setFieldLabel(appMessages.filename());
		
		formPanel.addListener(Events.Submit, new Listener<FormEvent>(){

			@Override
			public void handleEvent(FormEvent be) {
				handleUploadResponse();
			}
			
		});
		
		formPanel.add(uploadFile);
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
		thirdRadio.setBoxLabel(getThirdRadioLabel()+" ("+ appMessages.intoForm()+ " " + getFormName()+")");
	}

	@Override
 	protected void action() {
		String action = ACTION_URL;
		if(action.contains("?"))
			action += "&";
		else
			action += "?";
		
		action += "pathname="+uploadFile.getValue();
		formPanel.setAction(action);
		formPanel.submit();
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

	private void handleUploadResponse() {
		
		GWT.log("ItemImportView : Handling Imported Item Response.");
		
		ProgressIndicator.showProgressBar();
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,"formopen");

		try{
			builder.sendRequest(null, new RequestCallback(){
				@Override
				public void onResponseReceived(Request request, Response response){

					final Response resp = response;
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {

						@Override
						public void execute() {
							try{
								
								Editable editable = StudyImport.importStudyItem(resp.getText());
								controller.setEditable(editable);
								ProgressIndicator.hideProgressBar();
							}
							catch(Exception ex){
								ProgressIndicator.hideProgressBar();
								MessageBox.alert(appMessages.error(), appMessages.importParseError(), null);
								
							}	
							
							// Process the item. It is out of the try/catch because it has it's own error messages to display
							// the user.
							processImportedItem();
							ProgressIndicator.hideProgressBar();
						}
						
					}); 
				}
				
				@Override
				public void onError(Request request, Throwable ex){
					ProgressIndicator.hideProgressBar();
					MessageBox.alert(appMessages.error(), ex.getLocalizedMessage(), null);
				}
			}); 
		}
		catch(RequestException ex){
			ProgressIndicator.hideProgressBar();
			GWT.log(ex.getLocalizedMessage());
			MessageBox.alert(appMessages.error(), appMessages.importError(), null);
			
		}
	}
	
	private void processImportedItem() {
		
		GWT.log("ItemImportView : Processing imported Item for Saving.");
		if(firstRadio.getValue() || importingStudyOnly){
			controller.importStudy();
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
		
		MessageBox.info(appMessages.success(), appMessages.importSuccess(), null);
		formPanel.clear();
		closeWindow();
	}
}
