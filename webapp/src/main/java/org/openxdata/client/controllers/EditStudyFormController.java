package org.openxdata.client.controllers;

import org.openxdata.client.AppMessages;
import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.RefreshablePublisher;
import org.openxdata.client.views.EditStudyFormView;
import org.openxdata.server.admin.client.service.FormServiceAsync;
import org.openxdata.server.admin.client.service.StudyServiceAsync;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;

/**
 * Event dispatcher for the EditStudyFormView.
 * 
 */
public class EditStudyFormController extends UserAccessController {

	AppMessages appMessages = GWT.create(AppMessages.class);

	private StudyServiceAsync studyService;
	private FormServiceAsync formService;

	private EditStudyFormView editStudyFormView;

	public final static EventType OPENREADONLY = new EventType();
	public final static EventType EDITSTUDYFORM = new EventType();
	public final static EventType CREATENEWVERSION = new EventType();

	public EditStudyFormController(StudyServiceAsync studyService, FormServiceAsync formService) {
		super();
		this.studyService = studyService;
		this.formService = formService;
		registerEventTypes(OPENREADONLY, EDITSTUDYFORM, CREATENEWVERSION);
	}

	@Override
	public void handleEvent(AppEvent event) {
		GWT.log("EditStudyFormController : handleEvent");
		EventType type = event.getType();
		
		if(type == OPENREADONLY){
			editStudyFormView.launchDesigner(true);
		}
		if (type == EDITSTUDYFORM) {
			editStudyFormView = new EditStudyFormView(this);
			forwardToView(editStudyFormView, event);
		}
		if(type == CREATENEWVERSION){
			editStudyFormView.createNewVersionForFormWithData();
		}
	}

	public void saveForm(final FormDef form) {
		GWT.log("EditStudyFormController : saveForm");
		studyService.saveStudy(form.getStudy(), new EmitAsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				editStudyFormView.closeWindow();
				RefreshablePublisher.get().publish(
						new RefreshableEvent(RefreshableEvent.Type.UPDATE_STUDY, form.getStudy()));
				MessageBox.info(appMessages.success(), appMessages.saveSuccess(), null);
			}
		});
	}

	public void formHasData(FormDefVersion formVersion) {
		GWT.log("EditStudyFormController : formHasData");
		formService.hasEditableData(formVersion, new EmitAsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				editStudyFormView.onFormDataCheckComplete(result);
			}
		});
	}

	@Override
    public StudyServiceAsync getStudyService() {
	    return studyService;
    }

	@Override
    public FormServiceAsync getFormService() {
	    return formService;
    }
}
