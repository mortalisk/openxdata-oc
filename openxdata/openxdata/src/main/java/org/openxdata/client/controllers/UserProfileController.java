package org.openxdata.client.controllers;

import org.openxdata.client.AppMessages;
import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.RefreshablePublisher;
import org.openxdata.client.service.UserServiceAsync;
import org.openxdata.client.views.UserProfileView;
import org.openxdata.server.admin.model.User;


import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;

public class UserProfileController extends Controller {
    AppMessages appMessages = GWT.create(AppMessages.class); 
    public final static EventType USERPROFILE = new EventType();
    private UserProfileView userProfileView;
    private UserServiceAsync userService;
    
    public UserProfileController(UserServiceAsync aUserService) {
        super();
        registerEventTypes(USERPROFILE);
        userService = aUserService;
    }
    
    @Override
    protected void initialize() {
    	GWT.log("UserProfileController : initialize");
    }

    @Override
    public void handleEvent(final AppEvent event) {
    	GWT.log("UserProfileController : handleEvent");
        EventType type = event.getType();
        if (type == USERPROFILE) {
        	userService.getLoggedInUser(new EmitAsyncCallback<User>() {
                @Override
				public void onSuccess(User result) {
                    event.setData(result);
                    userProfileView = new UserProfileView(UserProfileController.this);
                    forwardToView(userProfileView, event);                    
                }
            });
        }
    }
    
    public void checkPasswordSaveUser(final User user) {
        GWT.log("UserProfileController : checkPasswordSaveUser, user : " + user.getFullName());
        userService.validatePassword(user, new EmitAsyncCallback<Boolean>() {
            @Override
			public void onSuccess(Boolean result) {
                if (result) {
                    saveUserProfile(user);
                } else {
                    userProfileView.displayError(appMessages.oldPasswordNotValid());
                }
            }
        });
    }
    
    public void saveUserProfile(final User user) {
    	GWT.log("UserProfileController : saveUserProfile , user : " + user.getFullName());
        userService.saveUser(user, new EmitAsyncCallback<Void>() {
                @Override
				public void onSuccess(Void result) {
                    MessageBox.alert(appMessages.success(), appMessages.user() + " " + user.getName() + " " + appMessages.profileSaved(), null);
                    RefreshablePublisher.get().publish(
                    		new RefreshableEvent(RefreshableEvent.Type.NAME_CHANGE, user));                    
                    userProfileView.closeWindow();
                }
            });
    }
}
