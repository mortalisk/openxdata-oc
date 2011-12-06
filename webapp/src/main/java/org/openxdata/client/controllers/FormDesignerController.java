package org.openxdata.client.controllers;

import org.openxdata.client.AppMessages;
import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.RefreshablePublisher;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.client.views.FormDesignerView;
import org.openxdata.server.admin.client.service.FormServiceAsync;
import org.openxdata.server.admin.client.service.StudyServiceAsync;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.StudyDef;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.google.gwt.core.client.GWT;

public class FormDesignerController extends Controller {
	AppMessages appMessages = GWT.create(AppMessages.class);
	
    private StudyServiceAsync studyService;
    private FormServiceAsync formService;
    private FormDesignerView formDesignerView;

    public final static EventType NEW_FORM = new EventType();
    public final static EventType EDIT_FORM = new EventType();
    public final static EventType READONLY_FORM = new EventType();

    public FormDesignerController(StudyServiceAsync aStudyService, FormServiceAsync aFormService) {
        super();
        studyService = aStudyService;
        formService = aFormService;
        registerEventTypes(NEW_FORM);
        registerEventTypes(EDIT_FORM);
        registerEventTypes(READONLY_FORM);
    }
    
    @Override
    protected void initialize() {
    	GWT.log("NewStudyFormController : initialize");
    }

    @Override
    public void handleEvent(final AppEvent event) {
    	GWT.log("NewStudyFormController : handleEvent");
        EventType type = event.getType();
        if (type == NEW_FORM || type == EDIT_FORM || type == READONLY_FORM) {
        	final FormDefVersion formDefVersion = event.getData("formDefVersion");
        	ProgressIndicator.showProgressBar();
        	formService.getForm(formDefVersion.getFormDef().getId(), new EmitAsyncCallback<FormDef>() {
		            @Override
		            public void onSuccess(FormDef result) {
		            	// ensure that we have the latest copy of the form def to avoid overwriting newer changes
		            	FormDefVersion latestFormDefVersion = result.getVersion(formDefVersion.getName());
		            	formDesignerView = new FormDesignerView(FormDesignerController.this, latestFormDefVersion);
		                forwardToView(formDesignerView, event);
		                ProgressIndicator.hideProgressBar();
		            }
        	});
        	
        }
    }
    
    public void saveForm(final FormDefVersion formDefVersion) {
        GWT.log("FormDesignerController : saveStudy");
        final FormDef form = formDefVersion.getFormDef();
        final StudyDef study = form.getStudy();
        ProgressIndicator.showProgressBar();
        studyService.saveStudy(study, new EmitAsyncCallback<StudyDef>() {
            @Override
            public void onSuccess(StudyDef result) {
            	studyService.getStudy(result.getId(), new EmitAsyncCallback<StudyDef>() {
		            @Override
		            public void onSuccess(StudyDef result) {
		            	GWT.log("saved studyDef id="+result.getId()+" name="+result.getName());
		            	FormDefVersion version = result.getForm(form.getName()).getVersion(formDefVersion.getName());
		                formDesignerView.savedFormDefVersion(version);
		                RefreshablePublisher.get().publish(new RefreshableEvent(RefreshableEvent.Type.UPDATE_STUDY, result));
		                formDesignerView.finishSaving();
		                ProgressIndicator.hideProgressBar();
		            }
		            @Override
		            public void onFailurePostProcessing(Throwable throwable) {
		            	formDesignerView.abortSaving();
		            }
            	});
            }
            @Override
            public void onFailurePostProcessing(Throwable throwable) {
            	formDesignerView.abortSaving();
            }
        });
    }
}