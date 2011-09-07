package org.openxdata.client.controllers;

import org.openxdata.client.AppMessages;
import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.RefreshablePublisher;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.client.views.EditStudyFormView;
import org.openxdata.server.admin.client.service.FormServiceAsync;
import org.openxdata.server.admin.client.service.StudyServiceAsync;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.StudyDef;

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
		if (type == EDITSTUDYFORM) {
			editStudyFormView = new EditStudyFormView(this);
			forwardToView(editStudyFormView, event);
		} else if (type == OPENREADONLY) {
			editStudyFormView.launchDesigner(true);
		} else if(type == CREATENEWVERSION){
			ProgressIndicator.showProgressBar();
			final FormDefVersion version = editStudyFormView.createNewVersionForFormWithData();
			studyService.saveStudy(version.getFormDef().getStudy(), new EmitAsyncCallback<StudyDef>() {
				@Override
				public void onSuccess(StudyDef result) {
					RefreshablePublisher.get().publish(
							new RefreshableEvent(RefreshableEvent.Type.CREATE_STUDY, result));
					FormDefVersion newVersion = result.getForm(version.getFormDef().getId()).getVersion(version.getName());
					editStudyFormView.launchDesigner(newVersion, false); // sets the form version with updated ID to avoid saving duplicates
					ProgressIndicator.hideProgressBar();
				}
			});
		}
	}
	

	public void saveForm(final FormDef form, final boolean triggerRefreshEvent) {
		GWT.log("EditStudyFormController : saveForm");
		studyService.saveStudy(form.getStudy(), new EmitAsyncCallback<StudyDef>() {
			@Override
			public void onSuccess(StudyDef result) {
				editStudyFormView.closeWindow();
				if (triggerRefreshEvent) {
					RefreshablePublisher.get().publish(
						new RefreshableEvent(RefreshableEvent.Type.UPDATE_STUDY, result));
				}
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
