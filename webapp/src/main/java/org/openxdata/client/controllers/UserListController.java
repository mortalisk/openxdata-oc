package org.openxdata.client.controllers;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.client.AppMessages;
import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.RefreshablePublisher;
import org.openxdata.client.model.UserSummary;
import org.openxdata.client.util.PagingUtil;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.client.views.UserListView;
import org.openxdata.server.admin.client.service.UserServiceAsync;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.paging.PagingLoadResult;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class UserListController extends Controller {
    AppMessages appMessages = GWT.create(AppMessages.class); 
    public final static EventType USERLIST = new EventType();

    private UserListView userListView;
    private UserServiceAsync userService;
    
    public UserListController(UserServiceAsync aUserService) {
        super();
        userService = aUserService;
        registerEventTypes(USERLIST);
    }
    
    @Override
    protected void initialize() {
        GWT.log("FormListController : initialize");
        userListView = new UserListView(this);
        RefreshablePublisher.get().subscribe(RefreshableEvent.Type.UPDATE_USER, userListView);
        RefreshablePublisher.get().subscribe(RefreshableEvent.Type.CREATE_USER, userListView);
    }

    @Override
    public void handleEvent(AppEvent event) {
    	GWT.log("FormListController : handleEvent");
        EventType type = event.getType();
        if (type == USERLIST) {
            forwardToView(userListView, event);
        }
    }
    
    public void getUsers(final PagingLoadConfig pagingLoadConfig, 
            final AsyncCallback<com.extjs.gxt.ui.client.data.PagingLoadResult<UserSummary>> callback) {
    	
    	userService.getUsers(PagingUtil.createPagingLoadConfig(pagingLoadConfig), 
    			new EmitAsyncCallback<PagingLoadResult<User>>() {
            @Override
			public void onSuccess(PagingLoadResult<User> result) {
                List<UserSummary> results = new ArrayList<UserSummary>();
                List<User> data = result.getData();
                for (User d : data) {
                    results.add(new UserSummary(d));
                }
                ProgressIndicator.hideProgressBar();
                callback.onSuccess(new BasePagingLoadResult<UserSummary>(results, result.getOffset(), result.getTotalLength()));
            }
    	});
    }
    
    public void forwardToNewWizard() {
    	GWT.log("UserListController : forwardToNewWizard");
        Dispatcher dispatcher = Dispatcher.get();
        AppEvent event = new AppEvent(NewEditUserController.NEWUSER);
        GWT.log("dispatching event="+event);
    	dispatcher.dispatch(event);
    }
    
    public void forwardToEditWizard(UserSummary userSummary){
       	GWT.log("UserListController : forwardToEditWizard");
        Dispatcher dispatcher = Dispatcher.get();
        AppEvent event = new AppEvent(NewEditUserController.EDITUSER);
        event.setData("user", userSummary.getUser());
    	dispatcher.dispatch(event);
    }

	public void forwardToItemImportController() {
		GWT.log("UserListController : forwardToItemImportController");
        Dispatcher dispatcher = Dispatcher.get();
        AppEvent event = new AppEvent(UserImportController.IMPORTUSERS);
        event.setData("editable", null);
    	dispatcher.dispatch(event);
	}
}