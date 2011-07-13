package org.openxdata.client.controllers;

import java.util.List;

import org.openxdata.client.AppMessages;
import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.RefreshablePublisher;
import org.openxdata.client.service.StudyServiceAsync;
import org.openxdata.client.service.UserServiceAsync;
import org.openxdata.client.views.EditStudyFormView;
import org.openxdata.server.admin.client.service.FormServiceAsync;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.admin.model.mapping.UserStudyMap;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;

/**
 * Event dispatcher for the EditStudyFormView.
 * 
 */
public class EditStudyFormController extends Controller {

	AppMessages appMessages = GWT.create(AppMessages.class);

	private StudyServiceAsync studyService;
	private UserServiceAsync userService;
	private FormServiceAsync formService;

	private EditStudyFormView editStudyFormView;

	public final static EventType EDITSTUDYFORM = new EventType();

	public EditStudyFormController(StudyServiceAsync studyService,
			FormServiceAsync formService, UserServiceAsync userService) {
		super();

		this.studyService = studyService;
		this.userService = userService;
		this.formService = formService;
		registerEventTypes(EDITSTUDYFORM);
	}

	@Override
	public void handleEvent(AppEvent event) {
		GWT.log("EditStudyFormController : handleEvent");
		EventType type = event.getType();
		if (type == EDITSTUDYFORM) {
			editStudyFormView = new EditStudyFormView(this);
			forwardToView(editStudyFormView, event);
		}

	}

	public void saveForm(final FormDef form) {
		GWT.log("EditStudyFormController : saveForm");
		studyService.saveStudy(form.getStudy(), new EmitAsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				editStudyFormView.closeWindow();
				RefreshablePublisher.get().publish(
						new RefreshableEvent(
								RefreshableEvent.Type.UPDATE_STUDY, form
										.getStudy()));
				MessageBox.info(appMessages.success(), appMessages.saveSuccess(), null);
			}
		});
	}

	public void getUsers() {
		GWT.log("EditStudyFormController : getUsers");
		userService.getUsers(new EmitAsyncCallback<List<User>>() {

			@Override
			public void onSuccess(List<User> result) {
				editStudyFormView.setUsers(result);
			}
		});
	}

	public void getUserMappedStudies() {
		GWT.log("EditStudyFormController : saveUsermappedStudies");
		studyService
				.getUserMappedStudies(new EmitAsyncCallback<List<UserStudyMap>>() {

					@Override
					public void onSuccess(List<UserStudyMap> result) {
						editStudyFormView.setUserMappedStudies(result);
					}
				});
	}

	public void getUserMappedForms() {
		GWT.log("EditStudyFormController : getUserMappedForms");
		formService
				.getUserMappedForms(new EmitAsyncCallback<List<UserFormMap>>() {

					@Override
					public void onSuccess(List<UserFormMap> result) {
						editStudyFormView.setUserMappedForms(result);
					}
				});

	}

	public void saveUserMappedForms(FormDef form, List<User> users) {
		GWT.log("EditStudyFormController : saveUserMappedForms");
		studyService.setUserMappingForForm(form, users, 
				new EmitAsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						GWT.log("Successfully saved mapped forms");
						//MessageBox.info(appMessages.success(), appMessages.saveSuccess(), null);
					}
				});
	}

	public void saveUserMappedStudies(StudyDef study, List<User> users) {
		GWT.log("EditStudyFormController : saveUserMappedStudies");
		studyService.setUserMappingForStudy(study, users, 
				new EmitAsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						GWT.log("Successfully saved mapped studies");
						//MessageBox.info(appMessages.success(), appMessages.saveSuccess(), null);
					}
				});
	}

	public void formHasData(FormDef form) {
		GWT.log("EditStudyFormController : formHasData");
		formService.hasEditableData(form.getDefaultVersion(), new EmitAsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				editStudyFormView.onFormDataCheckComplete(result);
			}
		});
	}
}
