package org.openxdata.client.controllers;

import org.openxdata.client.AppMessages;
import org.openxdata.client.Emit;
import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.RefreshablePublisher;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.client.views.UserImportView;
import org.openxdata.server.admin.client.service.UserServiceAsync;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

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
	
	public void downloadFile(final String fileData, final String fileName) {

		ProgressIndicator.showProgressBar();		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
			
				try {

					RequestBuilder postErrorData = new RequestBuilder(RequestBuilder.POST, "filedownload");
					
					postErrorData.sendRequest(fileData, new RequestCallback() {
								@Override
								public void onResponseReceived(Request arg0, Response arg1) {
									Emit.openWindow("filedownload?filename="+fileName+".csv");
								}

								@Override
								public void onError(Request arg0, Throwable arg1) {
									MessageBox.alert(appMessages.error(), arg1.toString(), null);
								}
							});
				} catch (Exception ex) {
					MessageBox.alert(appMessages.error(), ex.toString(), null);
				}
				finally {
					ProgressIndicator.hideProgressBar();
				}
			}
		});
	}
	
}