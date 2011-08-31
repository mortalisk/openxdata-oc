package org.openxdata.client.controllers;

import java.util.Map;

import org.openxdata.client.AppMessages;
import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.RefreshablePublisher;
import org.openxdata.client.service.StudyServiceAsync;
import org.openxdata.client.views.NewStudyFormView;
import org.openxdata.server.admin.client.service.FormServiceAsync;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.StudyDef;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;

public class NewStudyFormController extends UserAccessController {
    AppMessages appMessages = GWT.create(AppMessages.class);
    private StudyServiceAsync studyService;
    private FormServiceAsync formService;
    private NewStudyFormView newStudyFormView;

    public final static EventType NEWSTUDYFORM = new EventType();

    public NewStudyFormController(FormServiceAsync aFormService,StudyServiceAsync aStudyService) {
        super();
        studyService = aStudyService;
        formService = aFormService;
        registerEventTypes(NEWSTUDYFORM);
    }
    
    @Override
    protected void initialize() {
    	GWT.log("NewStudyFormController : initialize");
    }

    @Override
    public void handleEvent(AppEvent event) {
    	GWT.log("NewStudyFormController : handleEvent");
        EventType type = event.getType();
        if (type == NEWSTUDYFORM) {
        	newStudyFormView = new NewStudyFormView(this);
            forwardToView(newStudyFormView, event);
        }
    }

    public void getStudies() {
        GWT.log("FormListController : getStudies");
        studyService.getStudyNamesForCurrentUser(new EmitAsyncCallback<Map<Integer,String>>() {

            @Override
            public void onSuccess(Map<Integer,String> result) {
                newStudyFormView.setStudyNames(result);
            }
        });
    }

    public void getStudyDef(Integer studyId) {
        GWT.log("FormListController : getStudies");
        studyService.getStudy(studyId, new EmitAsyncCallback<StudyDef>() {

            @Override
            public void onSuccess(StudyDef result) {
                newStudyFormView.setStudyDef(result);
            }
        });    	
    }

    public void getForms(Integer studyId){
        GWT.log("FormListController : getStudies");
        formService.getFormNamesForCurrentUser(studyId, new EmitAsyncCallback<Map<Integer, String>>() {

            @Override
            public void onSuccess(Map<Integer, String> result) {
                newStudyFormView.setFormNames(result);
            }
        });
    }
    public void getFormDef(Integer formId) {
        GWT.log("FormListController : getStudies");
        formService.getForm(formId, new EmitAsyncCallback<FormDef>() {

            @Override
            public void onSuccess(FormDef result) {
                newStudyFormView.setFormDef(result);
            }
        });    	
    }
    
    public void saveStudy(final StudyDef study){
        GWT.log("NewStudyFormController : saveStudies");
        studyService.saveStudy(study, new EmitAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                newStudyFormView.closeWindow();
                RefreshablePublisher.get().publish(new RefreshableEvent(RefreshableEvent.Type.CREATE_STUDY, study));
                MessageBox.info(appMessages.success(), appMessages.saveSuccess(), null);
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