package org.openxdata.client.controllers;

import org.openxdata.client.AppMessages;
import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.RefreshablePublisher;
import org.openxdata.client.views.UserImportView;
import org.openxdata.server.admin.client.service.UserServiceAsync;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.google.gwt.core.client.GWT;

public class UserImportController extends Controller {

    AppMessages appMessages = GWT.create(AppMessages.class);
    
    public final static EventType IMPORTUSERS = new EventType();

    private UserImportView userImportView;
    
    private UserServiceAsync userService;
    
    public UserImportController(UserServiceAsync aUserService) {
        super();
        userService = aUserService;
        registerEventTypes(IMPORTUSERS);
    }
    
    @Override
    protected void initialize() {
    	GWT.log("UserImportController : initialize");
        userImportView = new UserImportView(this);
    }

    @Override
    public void handleEvent(AppEvent event) {
        GWT.log("UserImportController : handleEvent");
        if(event.getType() == IMPORTUSERS){
        	forwardToView(userImportView, event);        	
        }
    }
    
	public void importUsers(String fileContents) {
		userService.importUsers(fileContents, new EmitAsyncCallback<String>() {
            @Override
            public void onSuccess(String errorData) {
            	RefreshablePublisher.get().publish(new RefreshableEvent(RefreshableEvent.Type.REFRESH_USERLIST));
            	if (errorData == null) {
            		userImportView.importSuccess();
            	} else {
            		userImportView.importError(errorData);
            	}
            }
        });
	}
}