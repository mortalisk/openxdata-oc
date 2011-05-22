package org.openxdata.client.controllers;

import org.openxdata.client.AppMessages;
import org.openxdata.client.Emit;
import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.service.UserServiceAsync;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.client.views.LoginView;
import org.openxdata.client.views.ReLoginView;
import org.openxdata.server.admin.model.User;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;

public class LoginController extends Controller {

    AppMessages appMessages = GWT.create(AppMessages.class);
    
    public final static EventType LOGIN = new EventType();
    public final static EventType SESSIONTIMEOUT = new EventType();
    public final static EventType CHECKADMINPASS = new EventType();

    private LoginView loginView;
    private ReLoginView reLoginView;
    
    private UserServiceAsync userService;
    
    public LoginController(UserServiceAsync aUserService) {
        super();
        userService = aUserService;
        registerEventTypes(LOGIN);
        registerEventTypes(SESSIONTIMEOUT);
        registerEventTypes(CHECKADMINPASS);
    }
    
    @Override
    protected void initialize() {
    	GWT.log("LoginController : initialize");
        loginView = new LoginView(this);
        reLoginView = new ReLoginView(this);
    }

    @Override
    public void handleEvent(AppEvent event) {
        GWT.log("LoginController : handleEvent");
        if(event.getType() == CHECKADMINPASS){
        	checkAdminUserProperties();
        }
        else{
        	forwardToView(loginView, event);        	
        }
    }
    
	public void performLogin(String username, String password) {
    	ProgressIndicator.showProgressBar();
        userService.authenticate(username, password,
        new EmitAsyncCallback<User>() {
            @Override
			public void onSuccess(User result) {
                if (result != null) {
                	if(reLoginView.isVisible()){
                		reLoginView.hide();
                		ProgressIndicator.hideProgressBar();
                		return;
                	}
                	
                    loginView.close();
                    checkAdminUserProperties();
                } else {
                	MessageBox.alert(appMessages.error(), appMessages.unsuccessfulLogin(), null);
                    loginView.reset();
                }
                ProgressIndicator.hideProgressBar();
            }
        });
    }
	
	protected void checkAdminUserProperties() {
		User user = Registry.get(Emit.LOGGED_IN_USER_NAME);
		if (user.getName().equals("admin")) {
			userService.getUser("admin",
					new EmitAsyncCallback<User>() {

						@Override
						public void onSuccess(User user) {
							checkIfAdminUserChangedDefaultPassword(user);
						}

					});
		}
	}
	
	private void checkIfAdminUserChangedDefaultPassword(User user) {
		userService.checkIfUserChangedPassword(user,
				new EmitAsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean passwordChanged) {
						displayInformation(passwordChanged);
					}
				});
	}

	protected void displayInformation(Boolean passwordChanged) {
		if(!passwordChanged){
			Dispatcher.get().dispatch(UserProfileController.PASSWORDCHANGE);
		}
	}

	public Dialog getReloginView(){
		if(this.reLoginView == null)
			reLoginView = new ReLoginView(this);
		return reLoginView;
	}
}