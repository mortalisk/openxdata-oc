package org.openxdata.server.admin.client.view;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.controller.facade.MainViewControllerFacade;
import org.openxdata.server.admin.client.permissions.util.RolesListUtil;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.constants.OpenXDataStackPanelConstants;
import org.openxdata.server.admin.client.view.event.EditableEvent;
import org.openxdata.server.admin.client.view.event.ItemSelectedEvent;
import org.openxdata.server.admin.client.view.factory.OpenXDataWidgetFactory;
import org.openxdata.server.admin.client.view.listeners.OpenXDataExportImportApplicationEventListener;
import org.openxdata.server.admin.client.view.widget.OpenXDataButton;
import org.openxdata.server.admin.client.view.widget.OpenXDataLabel;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.Report;
import org.openxdata.server.admin.model.ReportGroup;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserReportMap;
import org.purc.purcforms.client.controller.OpenFileDialogEventListener;
import org.purc.purcforms.client.model.OptionDef;
import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.querybuilder.QueryBuilderWidget;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.view.OpenFileDialog;
import org.purc.purcforms.client.xforms.XformBuilder;
import org.purc.purcforms.client.xforms.XformParser;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.Inject;

/**
 * This widget displays properties of the selected report and lets you edit
 * them.
 * 
 * @author daniel
 * @author Angel
 * 
 */
public class ReportView extends OpenXDataBaseView implements
        SelectionHandler<Integer>,
        OpenXDataExportImportApplicationEventListener,
        OpenFileDialogEventListener {
	
	/** The report definition object. */
	private Report report;
	
	/** The report group object. */
	private ReportGroup reportGroup;
	
	/** Widget for entering the name of the report or group. */
	private TextBox txtName;
	
	/** Widget for entering the description of the report or group. */
	private TextBox txtDescription;
	
	/**
	 * List of form versions from which to select the one to use for the report.
	 */
	private ListBox lbForms;
		
	/**
	 * Widget for displaying status and selection of the report definition file.
	 */
	private HorizontalPanel panelDef;
				
	/** List of forms versions. */
	private List<FormDefVersion> forms;
	
	/** The index of the report design tab. */
	private final int TAB_INDEX_DESIGN = 1;
	
	/** The index of report data display tab. */
	private final int TAB_INDEX_DATA = 2;
	
	private boolean reportDefChanged = false;
	
	private Label lblFormSource;
		
	/** Button for clicking to select the report definiton file. */
	private Button btnRptDef;
	
	/** Label for the report definition file. */
	private Label lblReportFile;
	
	/** The query builder widget. */
	private QueryBuilderWidget queryBuilder;
		
	/**
	 * Creates a new instance of the report view.
	 * 
	 * @param itemChangeListener
	 *            listener to <tt>Report</tt> property changes.
	 * @param openXDataViewFactory
	 */
        @Inject
	public ReportView(OpenXDataWidgetFactory openXDataViewFactory) {
		super(openXDataViewFactory);
		bindHandlers();
		setUp();
	}
	
	private void setUp() {
				
		txtName = new TextBox();
		
		txtDescription = new TextBox();
		
		lbForms = new ListBox(false);
		
		lblFormSource = new OpenXDataLabel("Form Source");
								
		panelDef = new HorizontalPanel();
														
		btnRptDef = new OpenXDataButton("Select");
						
		lblReportFile = new OpenXDataLabel("Report definition file");
				
		queryBuilder = new QueryBuilderWidget();
						
		openxdataStackPanel = widgetFactory.getOpenXdataStackPanel();
		
		// Register this class with Event Dispatchers.
		super.registerWithEventDispatchers();
		
		if (RolesListUtil.getPermissionResolver().isPermission(
		        Permission.PERM_VIEW_REPORTS)) {
			loadView();
		} else {
			loadPermissionLessView();
		}
		
		enableReportProperties(false);
	}
	
	private void loadPermissionLessView() {
		table.setWidget(0, 0,
		        new Label(constants.ascertain_permissionLessView() + "Reports"));
		FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
		cellFormatter.setWidth(0, 0, "20%");
		table.setStyleName("cw-FlexTable");
		
		Utilities.maximizeWidget(table);
		
		tabs.add(table, constants.ascertain_permissionTab());
		Utilities.maximizeWidget(tabs);
		
		tabs.selectTab(0);
		
		initWidget(tabs);
		
		setWidth("100%");
		
	}
	
	private void loadView() {
		table.setWidget(0, 0, new Label(constants.label_name()));
		table.setWidget(1, 0, new Label(constants.label_description()));
		table.setWidget(2, 0, lblFormSource);
		table.setWidget(3, 0, lblReportFile);
				
		table.setWidget(0, 1, txtName);
		table.setWidget(1, 1, txtDescription);
		table.setWidget(2, 1, lbForms);
		
		panelDef.add(btnRptDef);
		panelDef.setCellWidth(btnRptDef, "20%");
		table.setWidget(3, 1, panelDef);
				
		txtName.setWidth("100%");
		txtDescription.setWidth("100%");
		lbForms.setWidth("100%");
		panelDef.setWidth("100%");
		
		FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
		cellFormatter.setWidth(0, 0, "20%");
		table.getRowFormatter().removeStyleName(0, "FlexTable-Header");
		
		Utilities.maximizeWidget(table);
		
		if (RolesListUtil.getPermissionResolver().isPermission("Reports")) {
			tabs.add(table, constants.label_properties());
		}
		
		if (RolesListUtil.getPermissionResolver().isExtraPermission(
		        Permission.PERM_REPORT_QUERY_BUILDER)) {
			tabs.add(queryBuilder, "Fields");
		}
		
		Utilities.maximizeWidget(tabs);
		
		initWidget(tabs);
		
		setWidth("100%");
		setupEventListeners();
		tabs.selectTab(0);
		tabs.addSelectionHandler(this);
				
		queryBuilder.hideDebugTabs();
		
	}
	
	/**
	 * Sets up event listeners.
	 */
	private void setupEventListeners() {
		txtName.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent arg0) {
				updateName();
			}
		});
		txtName.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				updateName();
				if (event.getCharCode() == KeyCodes.KEY_ENTER)
					txtDescription.setFocus(true);
			}
		});
		
		txtDescription.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent arg0) {
				updateDescription();
			}
		});
		txtDescription.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent arg0) {
				updateDescription();
			}
		});
				
		lbForms.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				setReportForm();
			}
		});
		
		final OpenFileDialogEventListener eventListener = this;
		btnRptDef.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				OpenFileDialog dlg = new OpenFileDialog(eventListener,
				        "formopen");
				dlg.center();
			}
		});
		
	}
	
	/**
	 * Sets the Report Form.
	 */
	private void setReportForm() {
		if (report != null) {
			
			FormDefVersion formDefVersion = forms.get(lbForms
			        .getSelectedIndex() - 1);
			if (formDefVersion.getXform() != null) {
				report.setFormDefVersionId(formDefVersion.getFormDefVersionId());
				report.setDirty(true);
				queryBuilder.setXform(getXform(formDefVersion));
			} else {
				Utilities
				        .displayMessage("The Form you selected doesnot have questions in it. Please Add questions by going to Studies > Design for the selected Form.");
				lbForms.setSelectedIndex(-1);
				return;
			}
		}
	}
	
	/**
	 * Updates a report or group with the new name as typed by the user.
	 * 
	 * @param sender
	 *            the widget having the new name.
	 */
	private void updateName() {
		if (report != null) {
			report.setName(txtName.getText());
                        eventBus.fireEvent(new EditableEvent<Report>(report));
		} else {
			reportGroup.setName(txtName.getText());
                        eventBus.fireEvent(new EditableEvent<ReportGroup>(reportGroup));
		}
	}
	
	/**
	 * Updates a report or group with the new description as typed by the user.
	 * 
	 * @param sender
	 *            the widget having the new description.
	 */
	private void updateDescription() {
		if (report != null) {
			report.setDescription(txtDescription.getText());
			 eventBus.fireEvent(new EditableEvent<Report>(report));
		} else {
			reportGroup.setDescription(txtDescription.getText());
			 eventBus.fireEvent(new EditableEvent<ReportGroup>(reportGroup));
		}
	}
	
	public void onItemSelected(Composite sender, Object item) {
		report = null;
		reportGroup = null;
		enableProperties(true);
		if (item instanceof Report) {
			report = (Report) item;
			txtName.setText(report.getName());
			txtDescription.setText(report.getDescription());
			String def = report.getDefinition();
			if (def != null && def.trim().length() == 0)
				def = null;
			lbForms.setSelectedIndex(getFormIndex(report.getFormDefVersionId()));
			
			FormDefVersion formDefVersion = getFormDefVersion(report
			        .getFormDefVersionId());
			if (formDefVersion != null)
				queryBuilder.setXform(getXform(formDefVersion));
			
			queryBuilder.setQueryDef(report.getQueryDefinition());
			queryBuilder.setSql(report.getQuerySql());

			enableReportProperties(true);

			getTypeAndTitle();

		} else {
			reportGroup = (ReportGroup) item;
			txtName.setText(reportGroup.getName());
			txtDescription.setText(reportGroup.getDescription());

			queryBuilder.setXform(null);
			queryBuilder.setQueryDef(null);
			queryBuilder.setSql(null);

			enableReportProperties(false);

		}

		if (tabs.getTabBar().getSelectedTab() == TAB_INDEX_DESIGN)
			queryBuilder.load();
	}
	
	/**
	 * Enables or disables editing of report properties.
	 * 
	 * @param enabled
	 *            set to true to enable, else false.
	 */
	private void enableReportProperties(boolean enabled) {
		lbForms.setVisible(enabled);
		lblFormSource.setVisible(enabled);
		lblReportFile.setVisible(enabled);
		panelDef.setVisible(enabled);
	}
	
	/**
	 * Sets the list of available studies.
	 * 
	 * @param studies
	 *            the study list.
	 */
	public void setStudies(List<StudyDef> studies) {
		forms = loadForms(lbForms, studies);
	}
	
	/**
	 * Fills a list box with a form versions in a list of studies.
	 * 
	 * @param lbForm
	 *            the list box.
	 * @param studies
	 *            the study list.
	 * @return the loaded form versions list.
	 */
	public List<FormDefVersion> loadForms(ListBox lbForm, List<StudyDef> studies) {
		lbForm.clear();
		List<FormDefVersion> forms = new ArrayList<FormDefVersion>();
		
		if (studies == null)
			return forms;
		
		lbForm.addItem("", "");
		for (StudyDef studyDef : studies) {
			for (FormDef formDef : studyDef.getForms()) {
				for (FormDefVersion formDefVersion : formDef.getVersions()) {
					lbForm.addItem(
					        formDef.getName() + "-" + formDefVersion.getName(),
					        String.valueOf(formDefVersion.getFormDefVersionId()));
					forms.add(formDefVersion);
				}
			}
		}
		
		return forms;
	}
	
	/**
	 * Gets the FormDefVersion of a given form version id.
	 * 
	 * @param formDefVersionId
	 *            the form version id.
	 * @return the FormDefVersion.
	 */
	public FormDefVersion getFormDefVersion(Integer formDefVersionId) {
		if (formDefVersionId == null)
			return null;
		
		for (FormDefVersion formDefVersion : forms) {
			if (formDefVersion.getFormDefVersionId() == formDefVersionId)
				return formDefVersion;
		}
		return null;
	}
	
	/**
	 * 
	 * @param formDefVersion
	 * @return
	 */
	public String getXform(FormDefVersion formDefVersion) {
		// TODO This needs to be done more efficiently by not rebuilding the
		// xform every time we change to a different report.
		org.purc.purcforms.client.model.FormDef formDef = XformParser
		        .fromXform2FormDef(formDefVersion.getXform());
		
		QuestionDef qtnDef = new QuestionDef(formDef.getPageAt(0));
		qtnDef.setId(formDef.getQuestionCount() + 1);
		qtnDef.setBinding("openxdata_user_name");
		qtnDef.setDataType(QuestionDef.QTN_TYPE_LIST_EXCLUSIVE);
		qtnDef.setText("User");
		formDef.addQuestion(qtnDef);
		
		int index = 0;
		List<User> users = Context.getUsers();
		for (User user : users)
			qtnDef.addOption(new OptionDef(++index, user.getName(), user
			        .getName(), qtnDef));
		
		return FormUtil.formatXml(XformBuilder.fromFormDef2Xform(formDef));
	}
	
	/**
	 * Gets the list box index of a given form version id.
	 * 
	 * @param formDefVersionId
	 *            the form version id.
	 * @return the list box index of the form version.
	 */
	public int getFormIndex(Integer formDefVersionId) {
		if (formDefVersionId == null)
			return -1;
		
		for (int index = 0; index < forms.size(); index++) {
			FormDefVersion formDefVersion = forms.get(index);
			if (formDefVersion.getFormDefVersionId() == formDefVersionId)
				return index + 1;
		}
		return -1;
	}
	
	/**
	 * Gets the sql statement for the report.
	 * 
	 * @return the report sql statement.
	 */
	public String getSql() {
		if (tabs.getTabBar().getSelectedTab() != TAB_INDEX_DESIGN
		        && report != null)
			return report.getQuerySql();
		return queryBuilder.getSql();
	}
	
	/**
	 * Saves values from the widgets to the report definition object.
	 */
	public void commitChanges(boolean optimize) {
		// If we have the design tab selected, we are assuming user has made
		// changes to the report
		if (!optimize || tabs.getTabBar().getSelectedTab() == TAB_INDEX_DESIGN) {
			if (report != null) {
				report.setQueryDefinition(queryBuilder.getQueryDef());
				report.setQuerySql(queryBuilder.getSql());
				report.setDirty(true);
			}
		}
	}
	
	@Override
	public void onSelection(SelectionEvent<Integer> event) {
		Integer selectedIndex = event.getSelectedItem();
		if (selectedIndex == TAB_INDEX_DESIGN)
			queryBuilder.load();
		else if (selectedIndex == TAB_INDEX_DATA) {
			if (reportDefChanged)
				commitChanges(false);
		}
		
		reportDefChanged = (selectedIndex == TAB_INDEX_DESIGN);
	}
	
	/**
	 * @see org.purc.purcforms.client.controller.OpenFileDialogEventListener#onSetFileContents(String)
	 */
	@Override
	public void onSetFileContents(String contents) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
		        "formopen");
		
		try {
			builder.sendRequest(null, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request,
				        Response response) {
					String contents = response.getText();
					if (contents != null && contents.trim().length() > 0) {
						report.setDefinition(contents);
						 eventBus.fireEvent(new EditableEvent<Report>(report));
					}
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					exception.printStackTrace();
					Window.alert(exception.getMessage());
				}
			});
		} catch (RequestException ex) {
			ex.printStackTrace();
			Window.alert(ex.getMessage());
		}
	}
	
	/**
	 * Gets the report type and title as one string.
	 */
	private void getTypeAndTitle() {
		try {
			
			String values = report.getParamValues();
			if (values == null || values.trim().length() == 0)
				return;
			int pos1 = values.indexOf('|');
			
			pos1++;
			int pos2 = values.indexOf('|', pos1);
			if (pos2 < 0)
				pos2 = values.length();
			
			if (pos2 == values.length())
				return;
			
			pos1 = pos2 + 1;
			pos2 = values.indexOf('|', pos1);
			if (pos2 < 0)
				pos2 = values.length();
			
			if (pos2 == values.length())
				return;
			
			pos1 = pos2 + 1;
			pos2 = values.indexOf('|', pos1);
			if (pos2 < 0)
				pos2 = values.length();
			
			if (pos2 == values.length())
				return;
			
			pos1 = pos2 + 1;
			pos2 = values.indexOf('|', pos1);
			if (pos2 < 0)
				pos2 = values.length();
			
			if (pos2 == values.length())
				return;
			
			pos1 = pos2 + 1;
			pos2 = values.indexOf('|', pos1);
			if (pos2 < 0)
				pos2 = values.length();
		} catch (Exception ex) {
		}
	}
	
	@Override
	public void exportAsPdf() {
	}
	
	/**
	 * @return
	 */
	public List<UserReportMap> getDeletedUserMappedReportGroups() {
		return null;
	}
	
	@Override
	public void onExport() {
		exportAsPdf();
		
	}
	
	@Override
	public void onImport() {
		// do nothing
		
	}
	
	@Override
	public void onOpen() {
		// do nothing
		
	}
	
	@Override
	public void onDeleteItem() {
		// do nothing
		
	}
	
	@Override
	public void onNewChildItem() {
		// do nothing
		
	}
	
	@Override
	public void onNewItem() {
		// do nothing
		
	}
	
	@Override
	public void onRefresh() {
		MainViewControllerFacade.refreshData();
		
	}
	
	@Override
	public void onSave() {
		if (openxdataStackPanel.getSelectedIndex() == OpenXDataStackPanelConstants.INDEX_REPORTS) {
			MainViewControllerFacade.saveReports();
		}
	}

        private void clearPropeties(){
                txtName.setText(null);
        }

        private void enableProperties(boolean enabled){
                txtName.setEnabled(enabled);
                txtDescription.setEnabled(enabled);
        }
	
    private void bindHandlers() {

        ItemSelectedEvent.addHandler(eventBus, new ItemSelectedEvent.Handler<Report>() {

            @Override
            public void onSelected(Composite sender, Report item) {
                onItemSelected(sender, item);
            }
        }).forClass(Report.class);

        ItemSelectedEvent.addHandler(eventBus, new ItemSelectedEvent.Handler<ReportGroup>() {

            @Override
            public void onSelected(Composite sender, ReportGroup item) {
                onItemSelected(sender, item);
            }
        }).forClass(ReportGroup.class);

        EditableEvent.addHandler(eventBus, new EditableEvent.HandlerAdaptor<ReportGroup>() {

            @Override
            public void onDeleted(ReportGroup item) {
                if (item.equals(reportGroup)) {
                    reportGroup = null;
                    clearPropeties();
                    enableProperties(false);
                    enableReportProperties(false);
                }
            }
        }).forClass(ReportGroup.class);
        EditableEvent.addHandler(eventBus, new EditableEvent.HandlerAdaptor<StudyDef>() {

            @Override
            public void onLoaded(List<StudyDef> items) {
                setStudies(items);
            }
        }).forClass(StudyDef.class);
    }
}
