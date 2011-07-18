package org.openxdata.client.controllers;

import org.openxdata.client.AppMessages;
import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.RefreshablePublisher;
import org.openxdata.client.service.StudyServiceAsync;
import org.openxdata.client.views.DeleteStudyFormView;
import org.openxdata.server.admin.client.service.FormServiceAsync;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.StudyDef;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.google.gwt.core.client.GWT;

public class DeleteStudyFormController extends Controller {
	
	AppMessages appMessages = GWT.create(AppMessages.class);

	private DeleteStudyFormView deleteStudyFormView;
	private StudyServiceAsync studyService;
	private FormServiceAsync formService;
	
	public static final EventType DELETESTUDYFORM = new EventType();
	
	public DeleteStudyFormController(StudyServiceAsync studyService,FormServiceAsync formService){
		super();
		this.studyService = studyService;
                this.formService = formService;
		registerEventTypes(DELETESTUDYFORM);
	}

	@Override
	public void handleEvent(AppEvent event) {
    	GWT.log("DeleteStudyFormController : handleEvent");
        EventType type = event.getType();
        if (type == DELETESTUDYFORM) {
        	deleteStudyFormView = new DeleteStudyFormView(this);
            forwardToView(deleteStudyFormView, event);
        }
		
	}
	
	public void delete(final Editable editable) {
		if (editable instanceof StudyDef) {
			
			GWT.log("DeleteStudyFormController : delete study");
			studyService.deleteStudy((StudyDef) editable,
					new EmitAsyncCallback<Void>() {

                @Override
                public void onSuccess(Void result) {
                    RefreshablePublisher.get().publish(new RefreshableEvent(RefreshableEvent.Type.CREATE_STUDY, editable));
                    deleteStudyFormView.closeWindow();
                }
            });
		}
		
		if(editable instanceof FormDef){
			FormDef form = (FormDef) editable;
			form.getStudy().removeForm(form);
			saveStudy(form.getStudy(), form);
		}
		
		if(editable instanceof FormDefVersion){
			FormDefVersion formDefVersion = (FormDefVersion) editable;
			formDefVersion.getFormDef().removeVersion(formDefVersion);
			saveStudy(formDefVersion.getFormDef().getStudy(), formDefVersion);
		}
	}
	
	private void saveStudy(StudyDef study, final Editable editable) {
		studyService.saveStudy(study, new EmitAsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				RefreshablePublisher.get().publish(
						new RefreshableEvent(RefreshableEvent.Type.CREATE_STUDY, editable));
				deleteStudyFormView.closeWindow();
			}
		});
	}

	public void itemHasData(Editable item) {
		GWT.log("EditStudyFormController : formHasData");
		formService.hasEditableData(item, new EmitAsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				deleteStudyFormView.onItemDataCheckComplete(result);
			}
		});
	}
}
