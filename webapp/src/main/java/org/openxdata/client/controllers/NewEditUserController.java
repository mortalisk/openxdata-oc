package org.openxdata.client.controllers;

import org.openxdata.client.AppMessages;
import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.RefreshablePublisher;
import org.openxdata.client.views.NewEditUserView;
import org.openxdata.server.admin.client.service.FormServiceAsync;
import org.openxdata.server.admin.client.service.RoleServiceAsync;
import org.openxdata.server.admin.client.service.StudyServiceAsync;
import org.openxdata.server.admin.client.service.UserServiceAsync;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.UserNotFoundException;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class NewEditUserController extends Controller {
    AppMessages appMessages = GWT.create(AppMessages.class);
    private UserServiceAsync userService;
    private RoleServiceAsync roleService;
    private FormServiceAsync formService;
    private StudyServiceAsync studyService;
    private NewEditUserView newUserView;

    public final static EventType NEWUSER = new EventType();
    public final static EventType EDITUSER = new EventType();
    
    private RoleUserAccessController roleUserAccessController;
    private FormUserAccessController formUserAccessController;
    private StudyUserAccessController studyUserAccessController;

    public NewEditUserController(UserServiceAsync aUserService, RoleServiceAsync aRoleService, StudyServiceAsync aStudyService, FormServiceAsync aFormService) {
        super();
        userService = aUserService;
        roleService = aRoleService;
        formService = aFormService;
        studyService = aStudyService;
        registerEventTypes(NEWUSER);
        registerEventTypes(EDITUSER);
        roleUserAccessController = new RoleUserAccessController(roleService);
        formUserAccessController = new FormUserAccessController(formService);
        studyUserAccessController = new StudyUserAccessController(studyService);
    }
    
    @Override
    protected void initialize() {
    	GWT.log("NewUserController : initialize");
    }

    @Override
    public void handleEvent(AppEvent event) {
    	GWT.log("NewUserController : handleEvent");
        EventType type = event.getType();
        if (type == NEWUSER) {
        	newUserView = new NewEditUserView(type, this);
            forwardToView(newUserView, event);
        }
        if (type == EDITUSER) {
        	newUserView = new NewEditUserView(type, this);
            forwardToView(newUserView, event);
        }
    }

    public void isUserNameUnique(final String userName) {
        GWT.log("NewUserController : getCheckUserNameUnique");
        userService.getUser(userName, new AsyncCallback<User>() {
            @Override
            public void onSuccess(User result) {
            	newUserView.setUserNameUnique(userName, false);
            }
            @Override
            public void onFailure(Throwable throwable) {
            	if (throwable instanceof UserNotFoundException) {
            		newUserView.setUserNameUnique(userName, true);
            	}
            }
        });
    }

	public void isEmailUnique(final String email) {
		GWT.log("NewUserController : getCheckEmailUnique ");
		userService.findUserByEmail(email, new AsyncCallback<User>() {
			@Override
			public void onSuccess(User result) {
				newUserView.setEmailUnique(email, false);
			}
			@Override
			public void onFailure(Throwable throwable) {
				if (throwable instanceof UserNotFoundException) {
					newUserView.setEmailUnique(email, true);
				}
			}
		});
	}

    public void saveUser(final User user, final boolean triggerRefreshEvent, final boolean notifyMe) {
        GWT.log("NewUserController : saveUser");
        userService.saveUser(user, new EmitAsyncCallback<User>() {
            @Override
            public void onSuccess(User result) {
                if (triggerRefreshEvent) {
                	if (user.getId() == 0) {
                		RefreshablePublisher.get().publish(new RefreshableEvent(RefreshableEvent.Type.CREATE_USER, result));
                	} else {
                		RefreshablePublisher.get().publish(new RefreshableEvent(RefreshableEvent.Type.UPDATE_USER, result));
                	}
                }
                setUserForAccessMapping(result);
                if (notifyMe) {
                	newUserView.saved(result);
                }
            }
        });
    }
    
    public void getUser(String username) {
    	userService.getUser(username, new EmitAsyncCallback<User>() {
            @Override
            public void onSuccess(User result) {
                setUserForAccessMapping(result);
                newUserView.loaded(result);
            }
        });
    }
    
    public RoleUserAccessController getRoleUserAccessController() {
    	return roleUserAccessController;
    }
    public FormUserAccessController getFormUserAccessController() {
    	return formUserAccessController;
    }
    public StudyUserAccessController getStudyUserAccessController() {
    	return studyUserAccessController;
    }
    
    public void setUserForAccessMapping(User user) {
    	roleUserAccessController.setUser(user);
        formUserAccessController.setUser(user);
        studyUserAccessController.setUser(user);
    }
}