package org.openxdata.client.controllers;

import java.util.List;
import java.util.Map;

import org.openxdata.client.AppMessages;
import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.RefreshablePublisher;
import org.openxdata.client.service.StudyServiceAsync;
import org.openxdata.client.service.UserServiceAsync;
import org.openxdata.client.views.NewStudyFormView;
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

public class NewStudyFormController extends Controller {
    AppMessages appMessages = GWT.create(AppMessages.class);
    private StudyServiceAsync studyService;
    private FormServiceAsync formService;
    private NewStudyFormView newStudyFormView;
    private UserServiceAsync userService;

    public final static EventType NEWSTUDYFORM = new EventType();

    public NewStudyFormController(FormServiceAsync aFormService,StudyServiceAsync aStudyService,UserServiceAsync aUserService) {
        super();
        studyService = aStudyService;
        formService = aFormService;
        userService = aUserService;
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
    public void getUsersMappedToStudy(Integer studyId) {
        GWT.log("FormListController : getStudies");
        studyService.getUserMappedStudies(studyId, new EmitAsyncCallback<List<UserStudyMap>>() {

            @Override
            public void onSuccess(List<UserStudyMap> result) {
                newStudyFormView.setUserMappedStudies(result);
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
    public void getUsersMappedToForm(Integer formId) {
        GWT.log("FormListController : getStudies");
        formService.getUserMappedForms(formId, new EmitAsyncCallback<List<UserFormMap>>() {

            @Override
            public void onSuccess(List<UserFormMap> result) {
                newStudyFormView.setUserMappedForms(result);
            }
        });
    }
    
    public void saveStudy(final StudyDef study){
        GWT.log("NewStudyFormController : saveStudies");
        studyService.saveStudy(study, new EmitAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                newStudyFormView.onSaveStudyComplete();
                newStudyFormView.closeWindow();
                RefreshablePublisher.get().publish(new RefreshableEvent(RefreshableEvent.Type.CREATE_STUDY, study));
                MessageBox.info(appMessages.success(), appMessages.saveSuccess(), null);
            }
        });
    }
    
    public void saveUserMappedStudies(StudyDef study, List<User> users) {
        GWT.log("NewStudyFormController : saveUsermappedStudies");
        studyService.setUserMappingForStudy(study, users,new EmitAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                GWT.log("Successfully saves mapped study");
            }
        });
    }
    
    public void saveUserMappedForms(FormDef form, List<User> users) {
        GWT.log("NewStudyFormController : saveUsermappedForms");
        studyService.setUserMappingForForm(form, users, new EmitAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
               GWT.log("Successfully saves mapped form");
            }
        });
    }

    public void getUsers(){
    GWT.log("NewStudyFormController : getUsers");
    userService.getUsers(new EmitAsyncCallback<List<User>>() {

            @Override
            public void onSuccess(List<User> result) {
                newStudyFormView.setUsers(result);
            }
        });
    }
}