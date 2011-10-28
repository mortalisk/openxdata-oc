package org.openxdata.client.views;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.client.AppMessages;
import org.openxdata.client.controllers.UserImportController;
import org.openxdata.client.util.CSVLoadResultReader;
import org.openxdata.client.util.ProgressIndicator;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.MemoryProxy;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public class UserImportView extends View {

	AppMessages appMessages = GWT.create(AppMessages.class);
	private UploadFileFormPanel formPanel;
	private DashboardWindow window;
	private Button importButton;
	private Button cancelButton;
	
	private int numberOfUsers;
	
	MessageBox progressWindow;

	public UserImportView(Controller controller) {
		super(controller);
	}

	@Override
	protected void initialize() {
		formPanel = new UploadFileFormPanel() {
			@Override
            public void handleUploadedFile(String fileContents) {
				String[] users = fileContents.split("\n");
				numberOfUsers = users.length-1;
				((UserImportController)controller).importUsers(fileContents);
            }
		};
		formPanel.initialize();
		
		importButton = new Button(appMessages.importX());
		importButton.setEnabled(false);
		importButton.addListener(Events.Select, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						((UploadFileFormPanel)formPanel).uploadFile();
						progressWindow = MessageBox.wait(appMessages.importUsers(), appMessages.importUsersWait(), appMessages.importing());
					}
				});
			}
		});
		formPanel.addButton(importButton);
		
		cancelButton = new Button(appMessages.cancel());
		cancelButton.addListener(Events.Select, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						window.closeWindow();
					}
				});
			}
		});
		formPanel.addButton(cancelButton);
		
		FormButtonBinding binding = new FormButtonBinding(formPanel);
		binding.addButton(importButton);
		importButton.setType("submit");
	}

	@Override
	protected void handleEvent(AppEvent event) {
		if (event.getType() == UserImportController.IMPORTUSERS) {
			window = new DashboardWindow(appMessages.importUsers(), 500, 125);
			window.add(formPanel);
			window.show();
		}
	}
	
	public void importSuccess() {
		ProgressIndicator.hideProgressBar();
		window.closeWindow();
		progressWindow.close();
		MessageBox.info(appMessages.importUsers(), appMessages.importUserSuccess(numberOfUsers), null);
	}
	
	public void importError(String errorData) {
		ProgressIndicator.hideProgressBar();
		progressWindow.close();
		window.closeWindow();
		String[] errorUsers = errorData.split("\n");
		int numberOfErrorUsers = errorUsers.length-1; // minus 1 for the heading
	    Dialog d = new Dialog();
	    d.setBodyStyle("fontSize:larger;");
	    d.addStyleName("x-window-dlg");
	    d.setHeading("Import Users");
	    d.addText(appMessages.importUserSuccess(numberOfUsers-numberOfErrorUsers));
	    d.addText(appMessages.importUserError(numberOfErrorUsers));
	    
	    // defines the xml structure  
	    ModelType type = new ModelType();    
	    type.addField("name");
	    type.addField("firstName"); 
	    type.addField("middleName");
	    type.addField("lastName");
	    type.addField("phoneNo");
	    type.addField("email");
	    type.addField("clearTextPassword");
	    type.addField("roles");
	    type.addField("formPermissions");
	    type.addField("studyPermissions");
	    type.addField("error messages");
	    
	    List<ColumnConfig> columns = new ArrayList<ColumnConfig>();  
	    columns.add(new ColumnConfig("name", appMessages.name(), 175));  
	    columns.add(new ColumnConfig("error messages", appMessages.errorMessages(), 300));
	    ColumnModel cm = new ColumnModel(columns);
	    
	    MemoryProxy<String> proxy = new MemoryProxy<String>(errorData);  
	    CSVLoadResultReader<ListLoadResult<ModelData>> reader = new CSVLoadResultReader<ListLoadResult<ModelData>>(type);  
	    final BaseListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy, reader);  

	    ListStore<ModelData> store = new ListStore<ModelData>(loader);
	    final Grid<ModelData> grid = new Grid<ModelData>(store, cm);
	    grid.setBorders(false);
	    //grid.setAutoExpandColumn("error messages");
	    grid.setStripeRows(true);
	    
	    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				loader.load();
			}
		});
	    
	    ContentPanel panel = new ContentPanel();
	    panel.setHeaderVisible(false);
	    panel.setBorders(false);
	    panel.setLayout(new FitLayout());
	    panel.add(grid);
	    panel.setSize(500, 200);
	    panel.setScrollMode(Scroll.AUTO);
	    
	    d.add(panel);
	    d.setSize(530, 320);
	    d.setClosable(false);
	    d.setHideOnButtonClick(true);
		d.show();
	}
}