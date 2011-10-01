package org.openxdata.client.views;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.client.AppMessages;
import org.openxdata.client.Emit;
import org.openxdata.client.Refreshable;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.controllers.UserListController;
import org.openxdata.client.model.UserSummary;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.User;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class UserListView extends View implements Refreshable {
	final AppMessages appMessages = GWT.create(AppMessages.class);
	
	public static final int PAGE_SIZE = 20;

	private Button importButton;
	private Button newButton;
	private Button editButton;
	
	private DashboardPortlet portlet;
	private Grid<UserSummary> grid;
	SearchPagingToolBar<UserSummary> toolBar;
	private PagingLoader<PagingLoadResult<UserSummary>> loader;
	private ColumnModel cm;

	public UserListView(Controller controller) {
		super(controller);
	}

	@Override
	protected void initialize() {
		GWT.log("UserListView : initialize");
		ProgressIndicator.showProgressBar();

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(new ColumnConfig("ID", "id", 20));
		configs.add(new ColumnConfig("name", "user name", 175));
		configs.add(new ColumnConfig("firstName", "first name", 175));
		configs.add(new ColumnConfig("middleName", "middle name", 100));
		configs.add(new ColumnConfig("lastName", "last name", 175));
		configs.add(new ColumnConfig("status", "status", 100));
		configs.add(new ColumnConfig("email", "email", 250));
		configs.add(new ColumnConfig("phoneNo", "phone", 100));
		cm = new ColumnModel(configs);
		cm.setHidden(0, true); // hide ID column
		
		toolBar = new SearchPagingToolBar<UserSummary>(PAGE_SIZE);
		loader = new BasePagingLoader<PagingLoadResult<UserSummary>>(
                new RpcProxy<PagingLoadResult<UserSummary>>() {
                    @Override
                    public void load(Object loadConfig, final AsyncCallback<PagingLoadResult<UserSummary>> callback) {
                    	ProgressIndicator.showProgressBar();
                        final PagingLoadConfig pagingLoadConfig = (PagingLoadConfig)loadConfig;
                        if (pagingLoadConfig.getSortField() == null || pagingLoadConfig.getSortField().trim().equals("")) {
                        	pagingLoadConfig.setSortField("name");
                        	pagingLoadConfig.setSortDir(SortDir.ASC);
                        }
                        GWT.log("sortField="+pagingLoadConfig.getSortField());
                        pagingLoadConfig.set(RemoteStoreFilterField.PARM_FIELD, "name");
                        pagingLoadConfig.set(RemoteStoreFilterField.PARM_QUERY, toolBar.getSearchFilterValue());
                        GWT.log("UserListView RpcProxy:load loadConfig pageSize="+pagingLoadConfig.getLimit()+" sortField="+pagingLoadConfig.getSortField()+" filter="+toolBar.getSearchFilterValue());
                        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                            @Override
							public void execute() {
                                final UserListController controller = (UserListController)UserListView.this.getController();
                                controller.getUsers(pagingLoadConfig, callback);
                            }
                        });
                    }
                }
        );
        loader.setRemoteSort(true);
        toolBar.bind(loader);
        
		ListStore<UserSummary> store = new ListStore<UserSummary>(loader);
		grid = new Grid<UserSummary>(store, cm);
		grid.setAutoExpandColumn("name");
		grid.setAutoExpandMax(10000);
		grid.setStripeRows(true);
		grid.setBorders(true);
		

		// new
		newButton = new Button("New");
		newButton.addListener(Events.Select, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				newUser();
			}
		});
		newButton.hide();

		// edit
		editButton = new Button("Edit");
		editButton.addListener(Events.Select, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				editUser();
			}
		});
		editButton.hide();
		
		importButton = new Button("Import");
		importButton.addListener(Events.Select, new Listener<ButtonEvent>(){

			@Override
			public void handleEvent(ButtonEvent be) {
				importItem();
			}
			
		});
		importButton.hide();
		
		User loggedInUser = Registry.get(Emit.LOGGED_IN_USER_NAME);
		if (loggedInUser != null) {
			checkLoggedInUserPermissions(cm, loggedInUser);
		} else {
			GWT.log("Could not find logged in user, so could not determine permissions");
		}

		LayoutContainer buttonBar = new LayoutContainer();
		buttonBar.setLayout(new HBoxLayout());
		buttonBar.add(newButton, new HBoxLayoutData(new Margins(5, 5, 0, 0)));
		buttonBar.add(editButton, new HBoxLayoutData(new Margins(5, 5, 0, 0)));
		buttonBar.add(importButton, new HBoxLayoutData(new Margins(5, 5, 0, 0)));
		
		//LayoutContainer filterBar = new LayoutContainer();
		//filterBar.setLayout(new HBoxLayout());
		//filterBar.add(filterField, new HBoxLayoutData(new Margins(5, 5, 0, 0)));

		portlet = new DashboardPortlet();
		portlet.setHeading("List of Users");
		ContentPanel cp = new ContentPanel();
		cp.setLayout(new FitLayout());
		cp.setHeaderVisible(false);
		cp.add(grid);
		//cp.setTopComponent(filterBar);
		cp.setBottomComponent(toolBar);
		portlet.add(cp);
		portlet.setBottomComponent(buttonBar);
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
			public void execute() {
                // load the first set of data
                PagingLoadConfig config = new BasePagingLoadConfig(0, PAGE_SIZE);
                loader.load(config);
            }
        });
	}
	
	private void checkLoggedInUserPermissions(ColumnModel cm, User loggedInUser) {
		if (loggedInUser.hasPermission(Permission.PERM_ADD_USERS)) {
			newButton.show();
		}
		if (loggedInUser.hasPermission(Permission.PERM_EDIT_USERS)) {
			editButton.show();
		}
		//if(loggedInUser.hasPermission(Permission.PERM_IMPORT_USERS)){
		//	importButton.show();
		//}
	}
	
	protected void importItem() {
		ProgressIndicator.showProgressBar();
		UserListController controller = (UserListController) getController();
		if (grid.getSelectionModel().getSelectedItem() != null) {
			UserSummary summary = grid.getSelectionModel().getSelectedItem();
			if(summary.getUser() != null){
				controller.forwardToItemImportController(summary.getUser());				
			}
			else{
				MessageBox.alert(appMessages.listOfForms(), appMessages.noFormVersion(), null);
				ProgressIndicator.hideProgressBar();
			}
		}
		else{
			controller.forwardToItemImportController(null);
		}
	}
	
	private void newUser() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				UserListController controller = (UserListController) getController();
				controller.forwardToNewWizard();
			}
		});
	}

	private void editUser() {
		if (grid.getSelectionModel().getSelectedItem() != null) {
			ProgressIndicator.showProgressBar();
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					UserSummary userSummary = grid.getSelectionModel().getSelectedItem();
					UserListController controller = (UserListController) getController();
					controller.forwardToEditWizard(userSummary);
				}
			});
		} else {
			MessageBox.alert("List of Users", "A User must be selected", null);
		}
	}

	@Override
	protected void handleEvent(AppEvent event) {
		GWT.log("FormListView : handleEvent");
		if (event.getType() == UserListController.USERLIST) {
			Portal portal = Registry.get(Emit.PORTAL);
			portal.add(portlet, 0);
			portlet.collapse();
		}
	}

	@Override
	public void refresh(RefreshableEvent event) {
		GWT.log("Refreshing...");
		if (event.getEventType() == RefreshableEvent.Type.UPDATE_USER) {
			User user = event.getData();
			ListStore<UserSummary> store = grid.getStore();
			UserSummary summary = getUserSummary(user.getId());
			if (summary != null) {
				summary.updateUser(user);
				store.update(summary);
			}
		} else if (event.getEventType() == RefreshableEvent.Type.CREATE_USER) {
			User user = event.getData();			
			ListStore<UserSummary> store = grid.getStore();
			UserSummary summary = new UserSummary(user);
			store.add(summary);
		}
	}
	
	UserSummary getUserSummary(int userId) {
		String userIdStr = String.valueOf(userId);
		for (UserSummary userSummary : grid.getStore().getModels()) {
			if (userIdStr.equals(userSummary.getId())) {
				return userSummary;
			}
		}
		GWT.log("ERROR: no user summary found id="+userId);
		return null;
	}
}
