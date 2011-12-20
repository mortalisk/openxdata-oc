package org.openxdata.client.controllers;

import java.util.Map;

import org.openxdata.client.AppMessages;
import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.RefreshablePublisher;
import org.openxdata.client.views.NewStudyFormView;
import org.openxdata.server.admin.client.service.FormServiceAsync;
import org.openxdata.server.admin.client.service.StudyServiceAsync;
import org.openxdata.server.admin.client.service.UserServiceAsync;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.StudyDef;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;

public class NewStudyFormController extends Controller {
    AppMessages appMessages = GWT.create(AppMessages.class);
    private StudyServiceAsync studyService;
    private FormServiceAsync formService;
    private UserServiceAsync userService;
    private NewStudyFormView newStudyFormView;

    public final static EventType NEWSTUDYFORM = new EventType();
    
	private UserFormAccessController userFormAccessController;
	private UserStudyAccessController userStudyAccessController;

    public NewStudyFormController(FormServiceAsync aFormService,StudyServiceAsync aStudyService, UserServiceAsync aUserService) {
        super();
        studyService = aStudyService;
        formService = aFormService;
        userService = aUserService;
        registerEventTypes(NEWSTUDYFORM);
        userFormAccessController = new UserFormAccessController(userService);
    	userStudyAccessController = new UserStudyAccessController(userService);
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
    
    public void saveStudy(final StudyDef study, final boolean launchDesigner, final boolean triggerRefreshEvent) {
        GWT.log("NewStudyFormController : saveStudies");
        studyService.saveStudy(study, new EmitAsyncCallback<StudyDef>() {
            @Override
            public void onSuccess(StudyDef result) {
            	studyService.getStudy(result.getId(), new EmitAsyncCallback<StudyDef>() {
		            @Override
		            public void onSuccess(StudyDef result) {
		            	GWT.log("saved studyDef id="+result.getId()+" name="+result.getName()+" forms="+result.getForms());
		                newStudyFormView.closeWindow();
		                if (triggerRefreshEvent) {
		                	RefreshablePublisher.get().publish(new RefreshableEvent(RefreshableEvent.Type.CREATE_STUDY, result));
		                }
		                if (launchDesigner) {
		                	newStudyFormView.launchFormDesigner(result);
		                } else {
		                	MessageBox.info(appMessages.success(), appMessages.saveSuccess(), null);
		                }
		            }
            	});
            }
        });
    }
    
	public UserFormAccessController getUserFormAccessController() {
		return userFormAccessController;
	}
	
	public UserStudyAccessController getUserStudyAccessController() {
		return userStudyAccessController;
	}
	
	public void setFormForAccessControl(FormDef form) {
		userFormAccessController.setForm(form);
	}

	public void setStudyForAccessControl(StudyDef study) {
		userStudyAccessController.setStudy(study);
	}
}