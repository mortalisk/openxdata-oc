package org.openxdata.client.views;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.client.AppMessages;
import org.openxdata.client.Refreshable;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.controllers.FormResponsesController;
import org.openxdata.client.model.FormDataBinding;
import org.openxdata.client.model.FormDataSummary;
import org.openxdata.client.model.FormSummary;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.User;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.RowEditorEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.HeaderGroupConfig;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.purc.purcforms.client.model.QuestionDef;

public class FormResponsesView extends View implements Refreshable  {
    final AppMessages appMessages = GWT.create(AppMessages.class);
    public static final int PAGE_SIZE = 10;
	private Grid<FormDataSummary> grid;
	private RowEditor<FormDataSummary> rowEditor;
	private ColumnModel columnModel;
	private PagingToolBar toolBar;
	private PagingLoader<PagingLoadResult<ModelData>> loader;
	private Button exportButton;
	private Button editButton;
	private FormDef formDef;
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
        window = new Window();
        // can;t do anything here because we don't have the column model
        exportButton = new Button(appMessages.exportToCSV());
        exportButton.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
			public void handleEvent(ButtonEvent be) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
					public void execute() {
                    	ProgressIndicator.showProgressBar();
                        FormDefVersion formDefVersion = formDef.getDefaultVersion();
                        String url = GWT.getModuleBaseURL()+ "dataexport?";
                        url += "format=csv";
                        url += "&formId=" + formDefVersion.getFormDefVersionId();
                        url += "&filename=" + formDef.getName()+"-"+formDefVersion.getName();
                        // userId, fromDate, toDate - other params
                        GWT.log("Loading CSV from URL "+url);
                        com.google.gwt.user.client.Window.Location.replace(URL.encode(url));
                        ProgressIndicator.hideProgressBar();
                    }
                });
             }
         });
    }

    private void initializeColumnModel() {
    	GWT.log("FormResponsesView : initializeColumnModel");

        // create column config - defines the column in the grid
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        // create audit fields
        ColumnConfig formId = new ColumnConfig("openxdata_form_data_id", appMessages.id(), 50);
        configs.add(formId);

        ColumnConfig date = new ColumnConfig("openxdata_date_created", appMessages.date(), 125);
        date.setDateTimeFormat(DateTimeFormat.getFormat("d MMM y HH:mm:ss"));
        date.setSortable(false); // FIXME: has to be like this because the column doesn't exist yet in the table (we must add it)
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

    private void preInitialise(ListStore<FormDataSummary> store, ColumnModel columnModel) {
        GWT.log("FormResponsesView : preInitialise");

        grid = new Grid<FormDataSummary>(store, columnModel);
        grid.setStripeRows(true);
        toolBar = new PagingToolBar(PAGE_SIZE);
        
        editButton = new Button(appMessages.editResponse());
        rowEditor = new RowEditor<FormDataSummary>() {
        	// FIXME: nasty code to add a button to the row edit (hopefully there is scope in the future to do it nicely
			@Override
			protected void onRender(Element target, int index) {
				super.onRender(target, index);
				if (btns != null) {
					btns.setLayout(new TableLayout(3));
					editButton.setMinWidth(getMinButtonWidth());
					btns.add(editButton);
					btns.layout(true);
				}
			}
			@Override
			protected void afterRender() {
				super.afterRender();
				if (renderButtons) {
				      btns.setWidth((getMinButtonWidth() * 3) + (5 * 3) + (3 * 4));
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
                			.forwardToDataCapture(formDef, summary.getExportedFormData().getFormData());
                		re.stopEditing(false);
                	}
                });
            }
        });
        rowEditor.setAutoHeight(true);
        grid.addPlugin(rowEditor);
    	
        // this is how we could plug in validation
        /*re.addListener(Events.ValidateEdit, new Listener<RowEditorEvent>() {
            public void handleEvent(RowEditorEvent be) {
                Map<String, Object> changes = be.getChanges();
                System.out.println("changes="+changes);
                FormDataSummary data = (FormDataSummary)be.getRecord().getModel();
            }
        });*/

        rowEditor.addListener(Events.AfterEdit, new Listener<RowEditorEvent>() {
                @Override
				public void handleEvent(final RowEditorEvent be) {
                    GWT.log("Events.AfterEdit");
                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                        @Override
						public void execute() {
                            ((FormResponsesController)FormResponsesView.this.getController())
                                        .saveFormDataResponse(user, be.getRecord(), formDef, formDataBinding.getFormDef());
                        }
                    });
                }
            }
        );
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
                        GWT.log("FormResponsesView RpcProxy:load grid.isViewReady="+grid.isViewReady());
                        final PagingLoadConfig pagingLoadConfig = (PagingLoadConfig)loadConfig;
                        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                            @Override
							public void execute() {
                                final FormResponsesController controller = (FormResponsesController)FormResponsesView.this.getController();
                                controller.getFormDataSummary(formDef, formDataBinding, pagingLoadConfig, callback);
                            }
                        });
                    }
                }
        );
        loader.setRemoteSort(true);
    }

    private void initializePagingToolbar() {
    	GWT.log("FormResponsesView : initializePagingToolbar");

        // initialize the toolbar used for going between pages
        SimpleComboBox<String> pageSize = new SimpleComboBox<String>();
        pageSize.setAutoWidth(true);
        pageSize.setTriggerAction(TriggerAction.ALL);
        pageSize.setEmptyText(appMessages.itemsPerPage(""));
        pageSize.add(appMessages.itemsPerPage("10"));
        pageSize.add(appMessages.itemsPerPage("20"));
        pageSize.add(appMessages.itemsPerPage("30"));
        pageSize.add(appMessages.itemsPerPage("40"));
        pageSize.add(appMessages.itemsPerPage("50"));

        pageSize.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {
            @Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
                String[] pageSizeText = se.getSelectedItem().getValue().split("[^\\d]");
                if (pageSizeText.length > 0) {
                    try {
                        Integer newPageSize = Integer.parseInt(pageSizeText[0]);
                        toolBar.setPageSize(newPageSize);
                        toolBar.refresh();
                    } catch (NumberFormatException e) {
                        GWT.log("Page size "+pageSizeText[0]+" is not a number", e);
                    }
                } else {
                    GWT.log("Page size could not be found in the selected text " + se.getSelectedItem().getValue());
                }
            }
        });

        toolBar.add(new SeparatorToolItem());
        toolBar.add(pageSize);
        toolBar.bind(loader);
    }

    private void initializeColumnEditor(ColumnConfig colConfig, QuestionDef questionDef) {
    	GWT.log("FormResponsesView : initializeColumnEditor");

    	/* Debug code
        System.out.println("DisplayText : " + questionDef.getDisplayText());
        System.out.println("DataType : " + questionDef.getDataType());
        System.out.println("Answer : " + questionDef.getAnswer());
        System.out.println("bind : " + questionDef.getControlNode().getAttribute("bind"));
        */

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
                time.setEditable(false); // avoids them entering invalid data
                time.setFormat(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.TIME_SHORT));
                colConfig.setDateTimeFormat(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.TIME_SHORT));
                colConfig.setEditor(new ListCellEditor(time)); //colConfig.setEditor(new CellEditor(time));s
                //TextField<String> time = new TextField<String>();
                //colConfig.setEditor(new CellEditor(time));
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
                d2.getPropertyEditor().setFormat(DateTimeFormat.getFormat("MM/dd/yyyy HH:mm:ss"));
                colConfig.setEditor(new CellEditor(d2));
                colConfig.setDateTimeFormat(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM));
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
             formDef = formSummary.getFormDefinition();

             preInitialise(new ListStore<FormDataSummary>(), new ColumnModel(new ArrayList<ColumnConfig>()));

             Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                 @Override
				public void execute() {
                     ProgressIndicator.showProgressBar();
                     FormResponsesController controller = (FormResponsesController)FormResponsesView.this.getController();
                     formDataBinding = controller.getFormDataColumnModel(formDef);
                     initializeColumnModel();
                     initializePagingLoader();
                     initializePagingToolbar();
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
        window.add(cp);
        window.addButton(exportButton);
        window.setSize(600, 400);
	    window.show();        
    }

	@Override
	public void refresh(RefreshableEvent event) {
    	GWT.log("FormResponseView : refresh response view");
    	toolBar.refresh();
	} 
	
	public void setUser(User user) {
		this.user = user;
		// now initialise all the components requiring a user to check permissions
        if (!user.hasPermission("Perm_Data_Edit")) {
        	rowEditor.disable();
        	GWT.log("user does not have permission to edit data, so roweditor was disabled");
        }
	}
}