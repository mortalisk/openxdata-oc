package org.openxdata.client.views;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.client.AppMessages;
import org.openxdata.client.Emit;
import org.openxdata.client.Refreshable;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.controllers.FormListController;
import org.openxdata.client.model.FormSummary;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreFilter;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FormListView extends View implements Refreshable {
	final AppMessages appMessages = GWT.create(AppMessages.class);

	public static final int PAGE_SIZE = 10;
	
	Button export, importButton, newButton, edit, delete, capture,
			browseResponses;
	
	private DashboardPortlet portlet;
	private Grid<FormSummary> grid;
	private ColumnModel cm;
	SearchPagingToolBar<FormSummary> toolBar;
	private PagingLoader<PagingLoadResult<FormSummary>> loader;

	private CheckBox allVersions;
	private CheckBox allForms;
	private List<FormSummary> allFormSummaries = new ArrayList<FormSummary>();

	public FormListView(Controller controller) {
		super(controller);
	}

	@Override
	protected void initialize() {
		GWT.log("FormListView : initialize");
		ProgressIndicator.showProgressBar();

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(new ColumnConfig("id", appMessages.id(), 20));
		configs.add(new ColumnConfig("organisation", appMessages.study(), 290));
		
		GridCellRenderer<FormSummary> nameCellRender = new GridCellRenderer<FormSummary>() {  
		      public String render(FormSummary summary, String property, ColumnData config, int rowIndex, int colIndex,  
		          ListStore<FormSummary> store, Grid<FormSummary> grid) {
		    	  String name = summary.getForm();
		    	  if (summary.getFormVersion() != null) {
		    		  name = summary.getForm() + "   (" + summary.getVersion() + ")";
		    	  }
		    	  
		    	  String style = "color: black";
		    	  if (!summary.isPublished()) {
		    		  style = "color: grey";
		    	  }
		    	  
		    	  return "<span qtitle='" + cm.getColumnById(property).getHeader() + "' qtip='" + name 
		    	  + "' style='" + style + "'>" + name + "</span>";
		      }  
		 };
		
		ColumnConfig nameColConfig = new ColumnConfig("form", appMessages.form(), 570);
		nameColConfig.setRenderer(nameCellRender);
		configs.add(nameColConfig);
		ColumnConfig responsesColConfig = new ColumnConfig("responses",
				appMessages.responses(), 70);
		responsesColConfig.setAlignment(HorizontalAlignment.RIGHT);
		configs.add(responsesColConfig);

		cm = new ColumnModel(configs);
		cm.setHidden(0, true); // hide ID column
		
		toolBar = new SearchPagingToolBar<FormSummary>(PAGE_SIZE);
		loader = new BasePagingLoader<PagingLoadResult<FormSummary>>(
		        new RpcProxy<PagingLoadResult<FormSummary>>() {
			        @Override
			        public void load(Object loadConfig,
			                final AsyncCallback<PagingLoadResult<FormSummary>> callback) {
				        ProgressIndicator.showProgressBar();
				        final PagingLoadConfig pagingLoadConfig = (PagingLoadConfig) loadConfig;
				        if (pagingLoadConfig.getSortField() == null
				                || pagingLoadConfig.getSortField().trim().equals("")) {
					        pagingLoadConfig.setSortField("name");
					        pagingLoadConfig.setSortDir(SortDir.ASC);
				        }
				        pagingLoadConfig.set(RemoteStoreFilterField.PARM_FIELD, "name");
				        pagingLoadConfig.set(RemoteStoreFilterField.PARM_QUERY, toolBar.getSearchFilterValue());
				        GWT.log("FormListListView RpcProxy:load loadConfig pageSize=" + pagingLoadConfig.getLimit()
				                + " sortField=" + pagingLoadConfig.getSortField()
				                + " filter=" + toolBar.getSearchFilterValue());
				        Scheduler.get().scheduleDeferred(
				                new ScheduledCommand() {
					                @Override
					                public void execute() {
						                final FormListController controller = (FormListController) FormListView.this.getController();
						                controller.getForms(pagingLoadConfig, callback);
					                }
				                });
			        }
		        });
		loader.setRemoteSort(true);
		toolBar.bind(loader);


		GroupingView view = new GroupingView();
		view.setShowGroupedColumn(false);

		GroupingStore<FormSummary> store = new GroupingStore<FormSummary>(loader);
		store.groupBy("organisation", true);
		grid = new Grid<FormSummary>(store, cm);
		grid.setAutoExpandColumn("form");
		grid.setAutoExpandMax(10000);
		grid.setStripeRows(true);
		grid.setBorders(true);
		grid.setView(view);
		Registry.register(Emit.GRID, grid);
		
		final StoreFilter<FormSummary> showAllFormVersionsFilter = new StoreFilter<FormSummary>() {
			@Override
            public boolean select(Store<FormSummary> store, FormSummary parent,
                    FormSummary item, String property) {
				if (!allVersions.getValue() && !item.isPublished()) {
					return false;
				} else if (!allForms.getValue() && item.getFormVersion() == null) {
					return false;
				}
				return true;

            }
        };
		store.addFilter(showAllFormVersionsFilter);
		store.applyFilters(null);

		grid.addListener(Events.CellDoubleClick,
				new Listener<GridEvent<FormSummary>>() {
					@Override
					public void handleEvent(GridEvent<FormSummary> be) {
						ColumnConfig col = grid.getColumnModel().getColumn(be.getColIndex());
						if (col.getId().equals("form")) {
							captureData();
						} else if (col.getId().equals("responses")) {
							browseResponses();
						}
					}
				});

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				// load the first set of data
				PagingLoadConfig config = new BasePagingLoadConfig(0, PAGE_SIZE);
				loader.load(config);
			}
		});

		// new
		newButton = new Button(appMessages.newStudyOrForm());
		newButton.addListener(Events.Select, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				newStudyOrForm();
			}
		});
		newButton.hide();

		// edit
		edit = new Button(appMessages.edit());
		edit.addListener(Events.Select, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				editStudyOrForm();
			}
		});
		edit.hide();

		// delete
		delete = new Button(appMessages.delete());
		delete.addListener(Events.Select, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				deleteStudyOrForm();
			}
		});
		delete.hide();

		allVersions = new CheckBox();
		allVersions.setBoxLabel(appMessages.showAllVersions());
		allVersions.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				toggleAllFormVersions();
			}
		});
		allVersions.hide();
		allForms = new CheckBox();
		allForms.setBoxLabel(appMessages.showAllForms());
		allForms.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				toggleAllForms();
			}
		});
		allForms.hide();

		export = new Button(appMessages.exportA());
		export.addListener(Events.Select, new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {
				export();
			}

		});
		export.hide();
		
		importButton = new Button(appMessages.importX());
		importButton.addListener(Events.Select, new Listener<ButtonEvent>(){

			@Override
			public void handleEvent(ButtonEvent be) {
				importItem();
			}
			
		});
		importButton.hide();
		
		capture = new Button(appMessages.captureData());
		capture.addListener(Events.Select, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				captureData();
			}
		});
		capture.hide();

		browseResponses = new Button(appMessages.viewResponses());
		browseResponses.addListener(Events.Select, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				browseResponses();
			}
		});
		browseResponses.hide();

		User loggedInUser = Registry.get(Emit.LOGGED_IN_USER_NAME);
		if (loggedInUser != null) {
			checkLoggedInUserPermissions(cm, loggedInUser);
		} else {
			GWT.log("Could not find logged in user, so could not determine permissions");
		}

		LayoutContainer buttonBar = new LayoutContainer();
		buttonBar.setLayout(new HBoxLayout());
		buttonBar.add(newButton, new HBoxLayoutData(new Margins(5, 5, 0, 0)));
		buttonBar.add(edit, new HBoxLayoutData(new Margins(5, 5, 0, 0)));
		buttonBar.add(delete, new HBoxLayoutData(new Margins(5, 5, 0, 0)));
		buttonBar.add(export, new HBoxLayoutData(new Margins(5, 5, 0, 0)));
		buttonBar.add(importButton, new HBoxLayoutData(new Margins(5, 5, 0, 0)));
		HBoxLayoutData flex = new HBoxLayoutData(new Margins(5, 5, 0, 0));
		flex.setFlex(1);
		buttonBar.add(new Text(), flex);
		buttonBar.add(capture, new HBoxLayoutData(new Margins(5, 5, 0, 0)));
		buttonBar.add(browseResponses, new HBoxLayoutData(new Margins(5, 0, 0, 0)));
		
		LayoutContainer filterBar = new LayoutContainer();
		filterBar.setLayout(new HBoxLayout());
		filterBar.add(new Text(), flex);
		filterBar.add(allVersions);
		filterBar.add(allForms);

		portlet = new DashboardPortlet();
		portlet.setHeading(appMessages.listOfForms());
		ContentPanel cp = new ContentPanel();
		cp.setLayout(new FitLayout());
		cp.setHeaderVisible(false);
		cp.add(grid);
		cp.setBottomComponent(toolBar);
		portlet.add(cp);
		portlet.setBottomComponent(buttonBar);
		portlet.setTopComponent(filterBar);
	}
	
	private void checkLoggedInUserPermissions(ColumnModel cm, User loggedInUser) {
		if (loggedInUser.hasPermission(Permission.PERM_ADD_STUDIES, Permission.PERM_ADD_FORMS,
				Permission.PERM_ADD_FORM_VERSIONS)) {
			
			newButton.show();
			allVersions.show();
			allForms.show();
			showPublishedColumn(cm, false);
		}
		if (loggedInUser.hasPermission(Permission.PERM_EDIT_STUDIES, Permission.PERM_EDIT_FORMS,
				Permission.PERM_EDIT_FORM_VERSIONS)) {
			
			edit.show();
			allVersions.show();
			allForms.show();
			showPublishedColumn(cm, false);
		}
		if (loggedInUser.hasPermission(Permission.PERM_DELETE_STUDIES, Permission.PERM_DELETE_FORMS,
				Permission.PERM_DELETE_FORM_VERSIONS)) {
			
			delete.show();
			allVersions.show();
			allForms.show();
			showPublishedColumn(cm, false);
		}
		
		if(loggedInUser.hasPermission(Permission.PERM_EXPORT_STUDIES, Permission.PERM_IMPORT_STUDIES)){
			export.show();
			importButton.show();
		}
		if (loggedInUser.hasPermission(Permission.PERM_ADD_FORM_DATA)) {
			capture.show();
		}
		if (loggedInUser.hasPermission(Permission.PERM_VIEW_FORM_DATA)) {
			browseResponses.show();
		}
	}

	private void showPublishedColumn(ColumnModel cm, boolean hide) {
		cm.setHidden(4, hide);
	}

	protected void export() {
		if (grid.getSelectionModel().getSelectedItem() != null) {
			ProgressIndicator.showProgressBar();
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					FormSummary summary = grid.getSelectionModel().getSelectedItem();
					if (summary.getFormVersion() != null) {
						FormListController controller = (FormListController) getController();
						controller.forwardToItemExportController(summary);
					} else {
						MessageBox.alert(appMessages.listOfForms(), appMessages.noFormVersion(), null);
						ProgressIndicator.hideProgressBar();
					}
				}
			});
		} else {
			MessageBox.alert(appMessages.listOfForms(),
					appMessages.formMustBeSelected(), null);
		}
	}
	
	protected void importItem() {
		ProgressIndicator.showProgressBar();
		FormListController controller = (FormListController) getController();
		if (grid.getSelectionModel().getSelectedItem() != null) {
			FormSummary summary = grid.getSelectionModel().getSelectedItem();
			if(summary.getFormVersion() != null){
				controller.forwardToItemImportController(summary.getFormVersion());				
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
	
	public List<FormSummary> createFormSummaries(FormDef formDef) {
		List<FormSummary> summaries = new ArrayList<FormSummary>();
		if (formDef.getVersions() == null || formDef.getVersions().size() == 0) {
			FormSummary formSummary = new FormSummary(formDef);
			formSummary.setResponses("0");
			summaries.add(formSummary);
		} else {
			for (final FormDefVersion formVersion : formDef.getVersions()) {
				if (formVersion != null) {
					FormSummary formSummary = getFormSummary(String.valueOf(formVersion.getId()));
					if (formSummary == null) {
						formSummary = new FormSummary(formVersion);
						summaries.add(formSummary);
					}
					formSummary.setStatus(appMessages.loading());
					formSummary.setResponses(appMessages.loading());
					// get response data
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							((FormListController) FormListView.this.getController())
									.hasFormData(formVersion);
						}
					});
				}
			}
		}
		return summaries;
	}
	
	public void setAllFormSummaries(List<FormSummary> formSummaries) {
		this.allFormSummaries = formSummaries;
	}
	
	private void addFormDef(ListStore<FormSummary> store, FormDef formDef) {
		List<FormSummary> newSummaries = createFormSummaries(formDef);
		for (final FormSummary fs : newSummaries) {
			FormSummary formSummary = getFormSummary(fs.getId());
			if (formSummary == null) {
				allFormSummaries.add(fs);
				store.add(fs);
			} else {
				store.update(fs);
			}
		}
	}

	private void newStudyOrForm() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				FormSummary formSummary = grid.getSelectionModel().getSelectedItem();
				FormListController controller = (FormListController) getController();
				controller.forwardToNewStudyFormWizard(formSummary);
			}
		});
	}

	private void editStudyOrForm() {
		if (grid.getSelectionModel().getSelectedItem() != null) {
			ProgressIndicator.showProgressBar();
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					FormSummary formSummary = grid.getSelectionModel().getSelectedItem();
					if (formSummary.getFormVersion() != null) {
						FormListController controller = (FormListController) getController();
						controller.forwardToEditStudyFormController(formSummary);
					} else {
						MessageBox.alert(appMessages.listOfForms(), appMessages.noFormVersion(), null);
						ProgressIndicator.hideProgressBar();
					}
				}
			});
		} else {
			MessageBox.alert(appMessages.listOfForms(),
					appMessages.formMustBeSelected(), null);
		}
	}

	private void deleteStudyOrForm() {
		if (grid.getSelectionModel().getSelectedItem() != null) {
			ProgressIndicator.showProgressBar();
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					FormListController controller = (FormListController) getController();
					FormSummary formSummary = grid.getSelectionModel().getSelectedItem();
					controller.forwardToDeleteStudyFormController(formSummary);
				}
			});
		} else {
			MessageBox.alert(appMessages.listOfForms(),
					appMessages.formMustBeSelected(), null);
		}
	}

	private void toggleAllFormVersions() {
		ProgressIndicator.showProgressBar();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				grid.getStore().applyFilters(null);
			}
		});
		ProgressIndicator.hideProgressBar();
	}

	private void toggleAllForms() {
		ProgressIndicator.showProgressBar();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				grid.getStore().applyFilters(null);
			}
		});
		ProgressIndicator.hideProgressBar();
	}

	private void captureData() {
		if (grid.getSelectionModel().getSelectedItem() != null) {
			ProgressIndicator.showProgressBar();
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					FormDefVersion formVersion = grid.getSelectionModel()
							.getSelectedItem().getFormVersion();
					if (formVersion != null) {
						FormListController controller = (FormListController) getController();
						controller.forwardToDataCapture(formVersion);
					} else {
						MessageBox.alert(appMessages.listOfForms(), appMessages.noFormVersion(), null);
						ProgressIndicator.hideProgressBar();
					}
				}
			});
		} else {
			MessageBox.alert(appMessages.listOfForms(), appMessages.formMustBeSelected(), null);
		}
	}

	private void browseResponses() {
		FormSummary summary = grid.getSelectionModel().getSelectedItem();
		if (summary != null) {
			if (!summary.getResponses().equals("0")) {
				ProgressIndicator.showProgressBar();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						FormSummary summary = grid.getSelectionModel()
								.getSelectedItem();
						FormListController controller = (FormListController) getController();
						controller.forwardToFormResponses(summary);
					}
				});
			} else {
				MessageBox.alert(appMessages.listOfForms(), appMessages.noResponses(), null);
				ProgressIndicator.hideProgressBar();
			}
		} else {
			MessageBox.alert(appMessages.listOfForms(), appMessages.formMustBeSelected(), null);
		}
	}

	public void setNumberOfFormResponses(FormDefVersion formDefVersion, Integer numberOfResponses) {
		FormSummary formSummary = getFormSummary(String.valueOf(formDefVersion.getId()));
		if (formSummary != null) {
			formSummary.setResponses(String.valueOf(numberOfResponses));
		}
		grid.getView().refresh(false);
	}

	public void setFormStatus(FormDef formDef, Boolean active) {
		FormSummary formSummary = getFormSummary(String.valueOf(formDef.getId()));
		if (formSummary != null) {
			if (active) {
				formSummary.setStatus(appMessages.active());
			} else {
				formSummary.setStatus(appMessages.design());
			}
		}
		grid.getView().refresh(false);
	}

	FormSummary getFormSummary(String formSummaryId) {
		for (FormSummary formSummary : allFormSummaries) {
			if (formSummaryId.equals(formSummary.getId())) {
				return formSummary;
			}
		}
		GWT.log("WARN: no form summary found id="+formSummaryId);
		return null;
	}

	@Override
	protected void handleEvent(AppEvent event) {
		GWT.log("FormListView : handleEvent");
		if (event.getType() == FormListController.FORMLIST) {
			Portal portal = Registry.get(Emit.PORTAL);
			portal.add(portlet, 0);
			portlet.maximise();
		}
	}

	@Override
	public void refresh(RefreshableEvent event) {
		GWT.log("Refreshing...");
		if (event.getEventType() == RefreshableEvent.Type.CAPTURE) {
			ListStore<FormSummary> store = grid.getStore();
			FormData data = event.getData();
			for (final FormSummary summary : store.getModels()) {
				FormDefVersion formDefVer = summary.getFormVersion();
				if (data.getFormDefVersionId().equals(
						formDefVer.getId())) {
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							((FormListController) FormListView.this
									.getController()).hasFormData(summary.getFormVersion());
						}
					});
					break;
				}
			}
		} else if (event.getEventType() == RefreshableEvent.Type.UPDATE_STUDY) {
			StudyDef study = event.getData();
			ListStore<FormSummary> store = grid.getStore();
			for (FormDef form : study.getForms()) {
				for (FormDefVersion formVersion : form.getVersions()) {
					FormSummary summary = getFormSummary(String.valueOf(formVersion.getId()));
					if (summary != null) {
						summary.setFormVersion(formVersion);
						store.update(summary);
					}
				}
			}

		} else if (event.getEventType() == RefreshableEvent.Type.CREATE_STUDY) {
			StudyDef study = (StudyDef)event.getData();			
			ListStore<FormSummary> store = grid.getStore();
			if (study.getForms() != null) {
				for (FormDef form : study.getForms()) {
					addFormDef(store, form);
				}
			}

		} else if (event.getEventType() == RefreshableEvent.Type.DELETE) {
			ListStore<FormSummary> store = grid.getStore();
			for (final FormSummary summary : store.getModels()) {
				if (event.getData() instanceof FormDef) {
					if (summary.getFormDefinition() == event.getData()) {
						store.remove(summary);
					}
				} else if (event.getData() instanceof StudyDef) {
					if (summary.getFormDefinition().getStudy() == event.getData()) {
						store.remove(summary);
					}
				} else if (event.getData() instanceof FormDefVersion) {
					if (summary.getFormVersion() == event.getData()) {
						store.remove(summary);
						break; // no chance to have more than one
					}
				}
			}
		}
	}
}
