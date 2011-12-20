package org.openxdata.client.views;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openxdata.client.AppMessages;
import org.openxdata.client.Emit;
import org.openxdata.client.Refreshable;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.controllers.FormResponsesController;
import org.openxdata.client.model.FormDataBinding;
import org.openxdata.client.model.FormDataSummary;
import org.openxdata.client.model.FormSummary;
import org.openxdata.client.model.UserSummary;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.User;
import org.purc.purcforms.client.model.QuestionDef;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.RowEditorEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.HeaderGroupConfig;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FormResponsesView extends View implements Refreshable  {
    final AppMessages appMessages = GWT.create(AppMessages.class);
    public static final int PAGE_SIZE = 10;

    private LayoutContainer searchPanel;
    private Button searchButton;
    private DateField searchTo;
    private DateField searchFrom;
    private ComboBox<UserSummary> searchUser;
    
	private Grid<FormDataSummary> grid;
	private RowEditor<FormDataSummary> rowEditor;
	private ColumnModel columnModel;
	private AdjustablePagingToolBar toolBar;
	private PagingLoader<PagingLoadResult<ModelData>> loader;
	private Button exportButton;
	private Button editButton;
	private Button deleteButton;
	private FormDefVersion formVersion;
	private FormDataBinding formDataBinding;
	private User user;
	private Window window;

    public FormResponsesView(Controller controller) {
        super(controller);
    }

    @Override
    protected void initialize() {
    	Registry.register("FormResponsesView", this);
        GWT.log("FormResponsesView : initialize");
        
        // setup search panel
        searchTo = new DateField();
        searchTo.setWidth(100);
        searchFrom = new DateField();
        searchFrom.setWidth(100);
        searchUser = new ComboBox<UserSummary>();
        searchUser.setWidth(200);
        searchUser.setName("name");
        searchUser.setForceSelection(true);
        searchUser.setEmptyText(appMessages.selectADataCapturer());
        searchUser.setDisplayField("name");
        searchUser.setPageSize(10);
        searchUser.setMinChars(2);
        searchUser.setTriggerAction(TriggerAction.ALL);
        RpcProxy<PagingLoadResult<UserSummary>> proxy = new RpcProxy<PagingLoadResult<UserSummary>>() {
			@Override
			protected void load(Object loadConfig, final AsyncCallback<PagingLoadResult<UserSummary>> callback) {
				final PagingLoadConfig pagingLoadConfig = (PagingLoadConfig)loadConfig;
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
					public void execute() {
                    	pagingLoadConfig.set(RemoteStoreFilterField.PARM_FIELD, "name");
                    	((FormResponsesController)FormResponsesView.this.getController()).getUserSummary(pagingLoadConfig, callback);
                    }
				});
			}
		};

		ListLoader<PagingLoadResult<UserSummary>> userLoader = new BasePagingLoader<PagingLoadResult<UserSummary>>(proxy);
		userLoader.setRemoteSort(true);
		ListStore<UserSummary> store = new ListStore<UserSummary>(userLoader);
		searchUser.setStore(store);
		searchPanel = new LayoutContainer();
		searchPanel.setLayout(new ColumnLayout());
		searchPanel.add(createFormContainer(appMessages.from(), searchFrom));
		searchPanel.add(createFormContainer(appMessages.to(), searchTo));
		searchPanel.add(createFormContainer(appMessages.capturer(), searchUser));
        
		searchButton = new Button(appMessages.search());
		searchButton.setStyleAttribute("paddingLeft", "10px"); 
     	searchButton.setStyleAttribute("paddingTop", "10px"); 
     	searchButton.addListener(Events.Select, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				searchResponses(); 
			}
		});
		
        // can't do anything here because we don't have the column model
        exportButton = new Button(appMessages.exportToCSV());
        exportButton.setStyleAttribute("paddingLeft", "10px");
        exportButton.setStyleAttribute("paddingTop", "10px");
        exportButton.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
			public void handleEvent(ButtonEvent be) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
					public void execute() {
                    	ProgressIndicator.showProgressBar();
                    	FormDef formDef = formVersion.getFormDef();
                        String url = GWT.getModuleBaseURL()+ "dataexport?";
                        url += "format=csv";
                        url += "&formId=" + formVersion.getId();
                        if (searchFrom.getValue() != null) {
                        	url += "&fromDate="+searchFrom.getValue().getTime();
                        }
                        if (searchTo.getValue() != null) {
                        	url += "&toDate="+searchTo.getValue().getTime();
                        }
                        if (searchUser.getValue() != null) {
                        	url += "&userId="+((UserSummary)searchUser.getValue()).getId();
                        }
                        String fileName = formDef.getName()+"-"+formVersion.getName();
                        fileName = fileName.replace(" ", "_").replaceAll("[^\\w]", "");
                        String newFileName = fileName;
                        if (fileName.length() > 50) {
                        	newFileName = fileName.substring(0, 24) + fileName.substring((fileName.length()-25));
                        }
                        url += "&filename=" + newFileName;
                        GWT.log("Loading CSV from URL "+URL.encode(url));
                        Emit.openWindow(url);
                        ProgressIndicator.hideProgressBar();
                    }
                });
             }
         });
        
        searchPanel.add(searchButton);
    }
    
    private Component createFormContainer(String label, TextField<?> field) {
    	LayoutContainer panel = new LayoutContainer();
    	panel.setStyleAttribute("paddingLeft", "10px");
    	panel.add(new Label(label));
    	panel.add(field);
    	return panel;
    }

    private void initializeColumnModel() {
    	GWT.log("FormResponsesView : initializeColumnModel");

        // create column config - defines the column in the grid
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        // create audit fields
        ColumnConfig formId = new ColumnConfig("openxdata_form_data_id", appMessages.id(), 50);
        configs.add(formId);

        ColumnConfig date = new ColumnConfig("openxdata_form_data_date_created", appMessages.date(), 125);
        date.setDateTimeFormat(DateTimeFormat.getFormat("d MMM y HH:mm:ss"));
        date.setSortable(true);
        date.setToolTip("Date When Exported"); 
        configs.add(date);

        configs.add(new ColumnConfig("openxdata_user_name", appMessages.capturer(), 60));

        // create the dynamic (form based) columns
        int columnCount = 0;
        for (String property : formDataBinding.getQuestionBindingKeys()) {
            ColumnConfig colConfig = new ColumnConfig(property, property, 150);
            colConfig.setToolTip(formDataBinding.getQuestionText(property));
            initializeColumnEditor(colConfig, formDataBinding.getQuestionDef(property));
            configs.add(colConfig);
            columnCount++;
        }

        columnModel = new ColumnModel(configs);
        columnModel.addHeaderGroup(0, 0, new HeaderGroupConfig(appMessages.auditFields(), 1, 3));
        columnModel.addHeaderGroup(0, 3, new HeaderGroupConfig(appMessages.responseDataFields(), 1, columnCount));
    }

    private void preInitialise(final ListStore<FormDataSummary> store, ColumnModel columnModel) {
        GWT.log("FormResponsesView : preInitialise");

        searchTo.reset();
        searchFrom.reset();
        searchUser.reset(); 
        
        grid = new Grid<FormDataSummary>(store, columnModel);
        grid.setStripeRows(true);
        toolBar = new AdjustablePagingToolBar(PAGE_SIZE);
        
        final int numBtns = 4;
        editButton = new Button(appMessages.editResponse());
        deleteButton = new Button(appMessages.delete());
        deleteButton.setVisible(false);
        rowEditor = new RowEditor<FormDataSummary>() {
        	// FIXME: nasty code to add a button to the row edit (hopefully there is scope in the future to do it nicely
			@Override
			protected void onRender(Element target, int index) {
				super.onRender(target, index);
				if (btns != null) {
					btns.remove(cancelBtn);
					btns.remove(saveBtn);
					btns.setLayout(new TableLayout(numBtns));
					editButton.setMinWidth(getMinButtonWidth());
					deleteButton.setMinWidth(getMinButtonWidth());
					btns.add(editButton);
					btns.add(saveBtn);
					btns.add(cancelBtn);
					btns.add(deleteButton);
					btns.layout(true);
				}
			}
			@Override
			protected void afterRender() {
				super.afterRender();
				if (renderButtons) {
					btns.setWidth((getMinButtonWidth() * numBtns) + (5 * numBtns) + (3 * (numBtns+1)));
				}
			}
        };
        
        final RowEditor<FormDataSummary> re = rowEditor;
        editButton.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
			public void handleEvent(ButtonEvent be) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                	@Override
					public void execute() {
                		ProgressIndicator.showProgressBar();
                		FormDataSummary summary = grid.getSelectionModel().getSelectedItem();
                		((FormResponsesController)FormResponsesView.this.getController())
                			.forwardToDataCapture(formVersion, summary.getExportedFormData().getFormData());
                		re.stopEditing(false);
                	}
                });
            }
        });
        deleteButton.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
			public void handleEvent(ButtonEvent be) {
            	deleteFormData(store, re);
            }
        });
        rowEditor.setAutoHeight(true);
        grid.addPlugin(rowEditor);

        rowEditor.addListener(Events.AfterEdit, new Listener<RowEditorEvent>() {
                @Override
				public void handleEvent(final RowEditorEvent be) {
                    GWT.log("Events.AfterEdit");
                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                        @Override
						public void execute() {
                            ((FormResponsesController)FormResponsesView.this.getController())
                                        .saveFormDataResponse(user, be.getRecord(), formVersion, formDataBinding.getFormDef());
                        }
                    });
                }
            }
        );
    }
    
    private void deleteFormData(final ListStore<FormDataSummary> store,
			final RowEditor<FormDataSummary> re) {
		MessageBox.confirm("Delete data", 
    			"Are you sure you want to delete this form response?", 
    			new Listener<MessageBoxEvent>() {
			@Override
			public void handleEvent(MessageBoxEvent be) {
				if (be.getButtonClicked().getItemId().equals(Dialog.YES)){
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							ProgressIndicator.showProgressBar();
							int count = grid.getStore().getCount();

							FormDataSummary summary = grid.getSelectionModel().getSelectedItem();
							((FormResponsesController)FormResponsesView.this.getController())
							.deleteFormData(user, summary.getExportedFormData().getFormData());
							re.stopEditing(false);
							
							if (count <= 1) {
								MessageBox.info("No more data", 
										"There are no more responses.", 
										new Listener<MessageBoxEvent>() {
											@Override
											public void handleEvent(
													MessageBoxEvent be) {
												window.hide();
											}
										});
							}
						}
					});
				}
			}
		});
	}

    private void initializeGrid() {
    	GWT.log("FormResponsesView : initializeGrid");

        ListStore<FormDataSummary> store = new ListStore<FormDataSummary>(loader);
        grid.reconfigure(store, columnModel);
        grid.setStateful(true);
        grid.setStateId("formresponses");

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
			public void execute() {
                // load the first set of data
                PagingLoadConfig config = new BasePagingLoadConfig(0, PAGE_SIZE);
                loader.load(config);
            }
        });
    }

    private void initializePagingLoader() {
    	GWT.log("FormResponsesView : initializePagingLoader");

        // initialise paging loader (aka place where the data will be loaded from)
        loader = new BasePagingLoader<PagingLoadResult<ModelData>>(
                new RpcProxy<PagingLoadResult<FormDataSummary>>() {
                    @Override
                    public void load(Object loadConfig, final AsyncCallback<PagingLoadResult<FormDataSummary>> callback) {
                        final PagingLoadConfig pagingLoadConfig = (PagingLoadConfig)loadConfig;
                        pagingLoadConfig.setLimit(toolBar.getPageSize()); //we need to manually set page size because we allow it to be altered
                        GWT.log("FormResponsesView RpcProxy:load loadConfig pageSize="+pagingLoadConfig.getLimit());
                        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                            @Override
							public void execute() {
                                final FormResponsesController controller = (FormResponsesController)FormResponsesView.this.getController();
                                Date startDate = null;
                                Date endDate = null;
                                String userId = null;
                                if (searchFrom.getValue() != null) {
                                	startDate = searchFrom.getValue();
                                }
                                if (searchTo.getValue() != null) {
                                	endDate = searchTo.getValue();
                                }
                                if (searchUser.getValue() != null) {
                                	userId = ((UserSummary)searchUser.getValue()).getId();  
                                }  
                                controller.getSearchFormDataSummary(formVersion, formDataBinding, pagingLoadConfig, callback,   
                                                startDate, endDate, userId); 
                            }
                        });
                    }
                }
        );
        loader.setRemoteSort(true);
        toolBar.bind(loader);
    }

    private void initializeColumnEditor(ColumnConfig colConfig, QuestionDef questionDef) {
    	GWT.log("FormResponsesView : initializeColumnEditor");

        switch(questionDef.getDataType()) {
            case QuestionDef.QTN_TYPE_TEXT:
                TextField<String> text = new TextField<String>();
                colConfig.setEditor(new CellEditor(text));
                break;
            case QuestionDef.QTN_TYPE_NUMERIC:
                NumberField number = new NumberField();
                number.setPropertyEditorType(Integer.class);
                colConfig.setEditor(new CellEditor(number));
                break;
            case QuestionDef.QTN_TYPE_DECIMAL:
                NumberField dec = new NumberField();
                dec.getPropertyEditor().setFormat(NumberFormat.getDecimalFormat());
                colConfig.setEditor(new CellEditor(dec));
                colConfig.setNumberFormat(NumberFormat.getDecimalFormat());
                break;
            case QuestionDef.QTN_TYPE_DATE:
                DateField d = new DateField();
                d.getPropertyEditor().setFormat(DateTimeFormat.getFormat("MM/dd/yyyy"));
                colConfig.setEditor(new CellEditor(d));
                colConfig.setDateTimeFormat(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM));
                break;
            case QuestionDef.QTN_TYPE_TIME:
                TimeField time = new TimeField();
                time.setIncrement(1);
                time.setEditable(false); // avoids them entering invalid data
                time.setFormat(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.TIME_SHORT));
                colConfig.setDateTimeFormat(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.TIME_SHORT));
                colConfig.setEditor(new ListCellEditor(time)); //colConfig.setEditor(new CellEditor(time));s
                break;
                // This is a question with a list of options where not more than one option can be selected at a time.
            case QuestionDef.QTN_TYPE_LIST_EXCLUSIVE:
                ListCellEditor editor = new ListCellEditor(new SimpleComboBox<String>());
                editor.setOptions(questionDef);
                colConfig.setEditor(editor);
                break;
                // This is a question with a list of options where more than one option can be selected at a time.
            case QuestionDef.QTN_TYPE_LIST_MULTIPLE:
                //ListCellEditor multiListEditor = new ListCellEditor(new ListField<SimpleListData>());
            	ListCellEditor multiListEditor = new ListCellEditor(new CheckBoxGroup());
                multiListEditor.setOptions(questionDef);
                colConfig.setEditor(multiListEditor);
                break;
                // Date and Time question type. This has both the date and time components
            case QuestionDef.QTN_TYPE_DATE_TIME:
                DateField d2 = new DateField();
                d2.getPropertyEditor().setFormat(DateTimeFormat.getFormat("d MMM y HH:mm:ss"));
                colConfig.setEditor(new CellEditor(d2));
                colConfig.setDateTimeFormat(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM));
                break;
                // Question with true and false answers.
            case QuestionDef.QTN_TYPE_BOOLEAN:
                ListCellEditor boolEditor = new ListCellEditor(new SimpleComboBox<String>());
                boolEditor.setOptions(QuestionDef.TRUE_VALUE,QuestionDef.FALSE_VALUE);
                colConfig.setEditor(boolEditor);
                break;
                // Question with repeat sets of questions.
            case QuestionDef.QTN_TYPE_REPEAT:
                break;
                // Question with image.
            case QuestionDef.QTN_TYPE_IMAGE:
                break;
                // Question with recorded video.
            case QuestionDef.QTN_TYPE_VIDEO:
                break;
                // Question with recoded audio.
            case QuestionDef.QTN_TYPE_AUDIO:
                break;
            case QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC:
                // FIXME: don't support this totally yet ...
                ListCellEditor dynListEditor = new ListCellEditor(new SimpleComboBox<String>());
                dynListEditor.setDynamicOptions(questionDef);
                colConfig.setEditor(dynListEditor);
                break;
        }
    }

    @Override
    protected void handleEvent(AppEvent event) {
    	GWT.log("FormResponsesView : handleEvent");

        if (event.getType() == FormResponsesController.BROWSE) {
             GWT.log("handling browse event "+System.currentTimeMillis());
             ProgressIndicator.showProgressBar();

             FormSummary formSummary = event.getData();
             formVersion = formSummary.getFormVersion();
             final FormDef formDef = formSummary.getFormDefinition();

             preInitialise(new ListStore<FormDataSummary>(), new ColumnModel(new ArrayList<ColumnConfig>()));

             Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                 @Override
				public void execute() {
                     ProgressIndicator.showProgressBar();
                     FormResponsesController controller = (FormResponsesController)FormResponsesView.this.getController();
                     formDataBinding = controller.getFormDataColumnModel(formVersion);
                     initializeColumnModel();
                     initializePagingLoader();
                     initializeGrid();
                     controller.getUser();
                     ProgressIndicator.hideProgressBar();
                 }
             });

	   	     initializeWindow(appMessages.browseResponses() + " '"+formDef.getName()+"'", grid, toolBar);
          }
    }

    private void initializeWindow(String title, Component component, Component bottomComponent) {
    	GWT.log("FormResponsesView : createWindow");
        window = new Window();
        window.setModal(true);
        window.setPlain(true);
        window.setHeading(title);
        window.setMaximizable(true);
        window.setDraggable(true);
        window.setResizable(true);
        window.setScrollMode(Scroll.AUTO);
        window.setLayout(new FitLayout());
        ContentPanel cp = new ContentPanel();
        cp.setLayout(new FitLayout());
        cp.setHeaderVisible(false);
        cp.setBorders(false);
        cp.setBottomComponent(bottomComponent);
        cp.add(component);
        LayoutContainer searchComponent = new LayoutContainer();
        searchComponent.add(searchPanel);
        searchComponent.setStyleAttribute("paddingBottom", "10px");
        window.setTopComponent(searchComponent);
        window.add(cp);
        window.addButton(exportButton);
        window.setSize(600, 450);
	    window.show();        
    }

	@Override
	public void refresh(RefreshableEvent event) {
    	GWT.log("FormResponseView : refresh response view");
    	if (RefreshableEvent.Type.DELETE == event.getEventType()
    			&& event.getData() instanceof FormData){
    		ListStore<FormDataSummary> store = grid.getStore();
			for (final FormDataSummary summary : store.getModels()) {
				if (summary.getExportedFormData().getFormData() == event.getData()) {
					store.remove(summary);
				}
			}
    	} else {
    		toolBar.refresh();
    	}
	} 
	
	public void setUser(User user) {
		this.user = user;
		// now initialise all the components requiring a user to check permissions
        if (!user.hasPermission(Permission.PERM_DATA_EDIT)) {
        	rowEditor.disable();
        	GWT.log("user does not have permission to edit data, so roweditor was disabled");
        }
        
        if (user.hasPermission(Permission.PERM_DELETE_FORM_DATA)){
        	deleteButton.setVisible(true);
        }
	}
	
	private void searchResponses() { 
	 	GWT.log("handling search response request "+System.currentTimeMillis()); 
	 	ProgressIndicator.showProgressBar(); 

	 	Scheduler.get().scheduleDeferred(new ScheduledCommand() { 
	 		@Override 
	 		public void execute() { 
	 			loader.load(); 
	 		} 
	 	}); 
	 } 
}