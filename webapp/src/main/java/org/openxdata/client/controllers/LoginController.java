package org.openxdata.client.controllers;

import org.openxdata.client.AppMessages;
import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.service.UserServiceAsync;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.client.views.LoginView;
import org.openxdata.server.admin.model.User;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;

public class LoginController extends Controller {

    AppMessages appMessages = GWT.create(AppMessages.class);
    
    public final static EventType LOGIN = new EventType();
    public final static EventType SESSION_TIMEOUT = new EventType();

    private LoginView loginView;
    private UserServiceAsync userService;
    
    public LoginController(UserServiceAsync aUserService) {
        super();
        userService = aUserService;
        registerEventTypes(LOGIN);
        registerEventTypes(SESSION_TIMEOUT);
    }
    
    @Override
    protected void initialize() {
        System.out.println("LoginController initialize");
        loginView = new LoginView(this);
    }

    @Override
    public void handleEvent(AppEvent event) {
        GWT.log("LoginController handleEvent");
        forwardToView(loginView, event);
    }
    
    public void performLogin(String username, String password) {
    	ProgressIndicator.showProgressBar();
        userService.authenticate(username, password,
        new EmitAsyncCallback<User>() {
            @Override
			public void onSuccess(User result) {
                if (result != null) {
                    loginView.close();
                } else {
                	MessageBox.alert(appMessages.error(), appMessages.unsuccessfulLogin(), null);
                    loginView.reset();
                }
                ProgressIndicator.hideProgressBar();
            }
        });
    }
}