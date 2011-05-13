package org.openxdata.client.controllers;

import java.util.List;

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
import com.extjs.gxt.ui.client.mvc.Controller;
import com.google.gwt.core.client.GWT;
import org.openxdata.client.service.UserServiceAsync;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.admin.model.mapping.UserStudyMap;

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
        studyService.getStudies(new EmitAsyncCallback<List<StudyDef>>() {

            @Override
            public void onSuccess(List<StudyDef> result) {
                newStudyFormView.setStudies(result);
            }
        });
    }
    public void getForms(){
        GWT.log("FormListController : getStudies");
        formService.getFormsForCurrentUser(new EmitAsyncCallback<List<FormDef>>() {

            @Override
            public void onSuccess(List<FormDef> result) {
                newStudyFormView.setForms(result);
            }
        });
    }
    public void saveStudy(final StudyDef study){
        GWT.log("NewStudyFormController : saveStudies");
        studyService.saveStudy(study, new EmitAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
//                newStudyFormView.saveUserStudyMap();
                newStudyFormView.closeWindow();
                RefreshablePublisher.get().publish(new RefreshableEvent(RefreshableEvent.Type.CREATE_STUDY, study));
            }
        });
    }
    public void saveUserMappedStudy(UserStudyMap studyMap){
        GWT.log("NewStudyFormController : saveUsermappedStudies");
        studyService.saveUserMappedStudy(studyMap,new EmitAsyncCallback<Void>() {

            @Override
            public void onSuccess(Void result) {
                GWT.log("Successfully saves mapped study");
            }
        });
    }
    public void saveUserMappedForm(UserFormMap map){
        GWT.log("NewStudyFormController : saveUsermappedForms");
        formService.saveUserMappedForm(map,new EmitAsyncCallback<Void>() {

            @Override
            public void onSuccess(Void result) {
               GWT.log("Successfully saves mapped form");
            }
        });
    }
    public void getUserMappedStudies() {
        GWT.log("NewStudyFormController : saveUsermappedStudies");
        studyService.getUserMappedStudies(new EmitAsyncCallback<List<UserStudyMap>>() {

            @Override
            public void onSuccess(List<UserStudyMap> result) {
                newStudyFormView.setUserMappedStudies(result);
            }
        });
    }
    public void deleteUserMappedStudy(UserStudyMap map){
        GWT.log("NewStudyFormController : deleteUsermappedStudies");
        studyService.deleteUserMappedStudy(map,new EmitAsyncCallback<Void>() {

            @Override
            public void onSuccess(Void result) {
                GWT.log("Successfully deleted user mapped study");
            }
        });
    }
    public void getUserMappedForms(){
        GWT.log("NewStudyFormController : getUserMappedForms");
        formService.getUserMappedForms(new EmitAsyncCallback<List<UserFormMap>>() {

            @Override
            public void onSuccess(List<UserFormMap> result) {
                newStudyFormView.setUserMappedForms(result);
            }
        });
    }
    public void deleteUserMappedForm(UserFormMap map){
        GWT.log("NewStudyFormController : deleteUsermappedforms");
        formService.deleteUserMappedForm(map,new EmitAsyncCallback<Void>() {

            @Override
            public void onSuccess(Void result) {
                GWT.log("Successfully deleted user mapped form");
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