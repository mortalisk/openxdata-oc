package org.openxdata.client.views;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.client.AppMessages;
import org.openxdata.client.Emit;
import org.openxdata.client.Refreshable;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.controllers.UnprocessedDataController;
import org.openxdata.client.model.FormDataSummary;
import org.openxdata.client.model.FormSummary;
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
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class UnprocessedDataView extends View implements Refreshable {
	final AppMessages appMessages = GWT.create(AppMessages.class);
	public static final int PAGE_SIZE = 30;
	
	private Grid<FormDataSummary> grid;
	private CheckBoxSelectionModel<FormDataSummary> sm;
	private AdjustablePagingToolBar toolBar;
	private PagingLoader<PagingLoadResult<FormDataSummary>> loader;
	private Button reprocessButton;
	private Button editButton;
	private Button deleteButton;
	private DashboardWindow window;

	public UnprocessedDataView(Controller controller) {
		super(controller);
	}

	@Override
	protected void initialize() {
		GWT.log("UnprocessedDataView : initialize");

		sm = new CheckBoxSelectionModel<FormDataSummary>();
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(sm.getColumn()); 
		configs.add(new ColumnConfig("id", appMessages.id(), 50));
		configs.add(new ColumnConfig("form", appMessages.form(), 275));
		configs.get(2).setSortable(false);
		configs.add(new ColumnConfig("dateCreated", appMessages.date(), 125));
		configs.add(new ColumnConfig("userName", appMessages.capturer(), 100));
		configs.get(4).setSortable(false);
		configs.add(new ColumnConfig("description", appMessages.description(), 175));
		ColumnModel cm = new ColumnModel(configs);

		toolBar = new AdjustablePagingToolBar(PAGE_SIZE);
		loader = new BasePagingLoader<PagingLoadResult<FormDataSummary>>(
				new RpcProxy<PagingLoadResult<FormDataSummary>>() {
					@Override
					public void load(Object loadConfig, final AsyncCallback<PagingLoadResult<FormDataSummary>> callback) {
						ProgressIndicator.showProgressBar();
						final PagingLoadConfig pagingLoadConfig = (PagingLoadConfig) loadConfig;
						if (pagingLoadConfig.getSortField() == null || pagingLoadConfig.getSortField().trim().equals("")) {
							pagingLoadConfig.setSortField("id");
							pagingLoadConfig.setSortDir(SortDir.ASC);
						}
						pagingLoadConfig.set(RemoteStoreFilterField.PARM_FIELD, "id");
						GWT.log("UnprocessedDataView RpcProxy:load loadConfig pageSize=" + pagingLoadConfig.getLimit()
								+ " sortField=" + pagingLoadConfig.getSortField());
						Scheduler.get().scheduleDeferred(
								new ScheduledCommand() {
									@Override
									public void execute() {
										final UnprocessedDataController controller = (UnprocessedDataController) UnprocessedDataView.this.getController();
										controller.getFormData(pagingLoadConfig, callback);
									}
								});
					}
				});
		loader.setRemoteSort(true);
		toolBar.bind(loader);

		ListStore<FormDataSummary> store = new ListStore<FormDataSummary>(loader);
		grid = new Grid<FormDataSummary>(store, cm);
		grid.setSelectionModel(sm); 
		grid.addPlugin(sm);
		grid.setAutoExpandMax(10000);
		grid.setAutoExpandColumn("form");
		grid.setStripeRows(true);
		grid.setBorders(true);

		grid.addListener(Events.CellDoubleClick,
				new Listener<GridEvent<FormSummary>>() {
					@Override
					public void handleEvent(GridEvent<FormSummary> be) {
						editFormData();
					}
				});
		
		deleteButton = new Button(appMessages.delete());
		deleteButton.setVisible(false);
		deleteButton.addListener(Events.Select, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				deleteFormData();
			}
		});
		
		editButton = new Button(appMessages.editResponse());
		editButton.setVisible(false);
		editButton.addListener(Events.Select, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				editFormData();
			}
		});
		
		reprocessButton = new Button(appMessages.reprocess());
		reprocessButton.addListener(Events.Select, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				reprocessFormData();
			 }
		 });

		// permissions to show/hide buttons
		User user = Registry.get(Emit.LOGGED_IN_USER_NAME);
		if (user != null) {
			if (user.hasPermission(Permission.PERM_DELETE_FORM_DATA)) {
				deleteButton.setVisible(true);
			}
			if (user.hasPermission(Permission.PERM_EDIT_FORM_DATA)) {
				editButton.setVisible(true);
			}
		} else {
			GWT.log("Could not find logged in user, so could not determine permissions");
		}
	}
	
	private void editFormData() {
		final List<FormDataSummary> items = sm.getSelectedItems();
		if (items == null || items.size() == 0) {
			MessageBox.alert(appMessages.manageUnprocessedData(), appMessages.selectDataToEdit(), null);
		} else if (items.size() > 1) {
			MessageBox.alert(appMessages.manageUnprocessedData(), appMessages.selectOnlyOneDataRow(), null);
		} else {
			final FormDataSummary summary = items.get(0);
			ProgressIndicator.showProgressBar();
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					((UnprocessedDataController)UnprocessedDataView.this.getController()).forwardToDataCapture(summary);
				}
			});
		}
	}
	
	private void reprocessFormData() {
		final List<FormDataSummary> items = sm.getSelectedItems();
		if (items == null || items.size() == 0) {
			MessageBox.alert(appMessages.manageUnprocessedData(), appMessages.selectDataToReprocess(), null);
		} else {
			ProgressIndicator.showProgressBar();
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					((UnprocessedDataController)UnprocessedDataView.this.getController()).reprocessFormData(items);
				}
			});
		}
	}
	
	private void deleteFormData() {
		final List<FormDataSummary> items = sm.getSelectedItems();
		if (items == null || items.size() == 0) {
			MessageBox.alert(appMessages.manageUnprocessedData(), appMessages.selectDataToDelete(), null);
		} else {
			MessageBox.confirm(appMessages.manageUnprocessedData(), appMessages.areYouSureDelete(), 
				new Listener<MessageBoxEvent>() {
					@Override
					public void handleEvent(MessageBoxEvent be) {
						if (be.getButtonClicked().getItemId().equals(Dialog.YES)){
							Scheduler.get().scheduleDeferred(new ScheduledCommand() {
								@Override
								public void execute() {
									ProgressIndicator.showProgressBar();
									((UnprocessedDataController)UnprocessedDataView.this.getController()).deleteFormData(items);
								}
							});
						}
					}
				});
		}
	}

	@Override
	protected void handleEvent(AppEvent event) {
		GWT.log("UnprocessedDataView : handleEvent");

		if (event.getType() == UnprocessedDataController.UNPROCESSED_DATA) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					// load the first set of data
					PagingLoadConfig config = new BasePagingLoadConfig(0, PAGE_SIZE);
					loader.load(config);
				}
			});
			window = new DashboardWindow(appMessages.manageUnprocessedData(), 800, 450, false);
			window.setLayout(new FitLayout());
			ContentPanel cp = new ContentPanel();
			cp.setLayout(new FitLayout());
			cp.setHeaderVisible(false);
			cp.setBorders(false);
			toolBar = new AdjustablePagingToolBar(PAGE_SIZE);
			toolBar.bind(loader);
			cp.setBottomComponent(toolBar);
			cp.add(grid);
			window.add(cp);
			window.addButton(editButton);
			window.addButton(deleteButton);
			window.addButton(reprocessButton);
			window.show();
		}
	}

	@Override
	public void refresh(RefreshableEvent event) {
		if (event.getEventType() == RefreshableEvent.Type.REFRESH_UNEXPORTED_DATA) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					toolBar.refresh();
				}
			});
		}
	}
}
