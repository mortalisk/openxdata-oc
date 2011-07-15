package org.openxdata.server.admin.client.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.controller.callback.OpenXDataAsyncCallback;
import org.openxdata.server.admin.client.internationalization.OpenXdataConstants;
import org.openxdata.server.admin.client.listeners.GetFileNameDialogEventListener;
import org.openxdata.server.admin.client.permissions.util.RolesListUtil;
import org.openxdata.server.admin.client.service.StudyManagerServiceAsync;
import org.openxdata.server.admin.client.view.widget.GetFileNameDialog;
import org.openxdata.server.admin.client.view.treeview.StudiesTreeView;
import org.openxdata.server.admin.client.view.widget.OpenXDataButton;
import org.openxdata.server.admin.client.view.widget.OpenXDataFlexTable;
import org.openxdata.server.admin.model.FormDataHeader;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.purc.purcforms.client.util.FormUtil;
import org.zenika.widget.client.datePicker.DatePicker;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.openxdata.server.admin.client.view.event.EditableEvent;
import org.openxdata.server.admin.client.view.event.EventType;
import org.openxdata.server.admin.client.view.event.FormDataHeaderEvent;

/**
 * This widget displays a list of data that has been collected for gives forms,
 * dates and users.
 * 
 * @author daniel
 * 
 */
public class StudyDataView extends Composite implements
        GetFileNameDialogEventListener {
	
	private static OpenXdataConstants constants = GWT
	        .create(OpenXdataConstants.class);
	
	private List<FormDataHeader> formDataList;
	private ListBox lbUser = new ListBox(false);
	private ListBox lbForm = new ListBox(false);
	private DatePicker dpToDate = new DatePicker();
	private DatePicker dpFromDate = new DatePicker();
	private FlexTable table = new OpenXDataFlexTable();
	private Button btnExportCsv = new OpenXDataButton(
	        constants.label_export_csv());
	private Button btnAddNew = new OpenXDataButton(constants.label_add_new());
	
	private List<User> users;
	private List<StudyDef> studies;
	private List<FormDefVersion> forms;
	
	/** List of all <tt>UserFormMaps.</tt> */
	private List<UserFormMap> mappedForms;
        private EventBus eventBus;
	
	/**
	 * Constructs an instance of this <tt>Class.</tt>
	 * 
	 * @param itemSelectionListener
	 *            the listener for selection on <tt>{@link StudiesTreeView}</tt>
	 *            change events.
	 */
	public StudyDataView(EventBus eventBus) {
		this.eventBus = eventBus;
		initWidgets();
                initHandlers();
	}
	
	private void initWidgets() {
		if (RolesListUtil.getPermissionResolver().isViewPermission(
		        Permission.PERM_VIEW_FORM_DATA)) {
			table.setWidget(0, 0, new Label(constants.label_form()));
			table.setWidget(0, 1, new Label(constants.label_version()));
			table.setWidget(0, 2, new Label(constants.label_description()));
			table.setWidget(0, 3, new Label("Creator"));
			table.setWidget(0, 4, new Label("Date submitted"));
			table.setWidget(0, 5, new Label("Changed by"));
			table.setWidget(0, 6, new Label("Date changed"));
			table.setWidget(0, 7, new Label(constants.label_action()));
			
			table.setStyleName("cw-FlexTable");
			
			FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
			cellFormatter.setHorizontalAlignment(0, 0,
			        HasHorizontalAlignment.ALIGN_CENTER);
			cellFormatter.setHorizontalAlignment(0, 1,
			        HasHorizontalAlignment.ALIGN_CENTER);
			cellFormatter.setHorizontalAlignment(0, 2,
			        HasHorizontalAlignment.ALIGN_CENTER);
			cellFormatter.setHorizontalAlignment(0, 3,
			        HasHorizontalAlignment.ALIGN_CENTER);
			cellFormatter.setHorizontalAlignment(0, 4,
			        HasHorizontalAlignment.ALIGN_CENTER);
			cellFormatter.setHorizontalAlignment(0, 5,
			        HasHorizontalAlignment.ALIGN_CENTER);
			cellFormatter.setHorizontalAlignment(0, 6,
			        HasHorizontalAlignment.ALIGN_CENTER);
			cellFormatter.setHorizontalAlignment(0, 7,
			        HasHorizontalAlignment.ALIGN_CENTER);
		}
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		// check whether user has permission to view form data
		horizontalPanel.add(new Label(constants.label_form()));
		horizontalPanel.add(lbForm);
		if (RolesListUtil.getPermissionResolver().isViewPermission(
		        Permission.PERM_VIEW_FORM_DATA)) {
			horizontalPanel.add(new Label(constants.label_from()));
			horizontalPanel.add(dpFromDate);
			horizontalPanel.add(new Label(constants.label_to()));
			horizontalPanel.add(dpToDate);
			horizontalPanel.add(new Label(constants.label_user()));
			horizontalPanel.add(lbUser);
			
			Button btn = new OpenXDataButton(constants.label_search());
			
			horizontalPanel.add(btn);
			
			horizontalPanel.setSpacing(5);
			
			btn.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					search();
				}
			});
		}
		
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setSpacing(5);
		verticalPanel.add(horizontalPanel);
		verticalPanel.add(table);
		initWidget(verticalPanel);
		
		FormUtil.maximizeWidget(horizontalPanel);
		FormUtil.maximizeWidget(table);
		FormUtil.maximizeWidget(verticalPanel);
		
		btnExportCsv.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				exportCsv();
			}
		});
		
		btnAddNew.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addNew();
			}
		});
		
		addNewExportButtons(0);
	}
	
	private void loadUsers() {
		lbUser.clear();
		if (users == null)
			return;
		
		lbUser.addItem("", "");
		for (User user : users)
			lbUser.addItem(user.getName(), String.valueOf(user.getId()));
	}
	
	private void loadForms() {
		if (Context.getAuthenticatedUser().hasAdministrativePrivileges()) {
			loadAllForms();
		} else {
			loadFormsAccordingToPermissions();
		}
	}
	
	public void loadAllForms() {
		
		lbForm.clear();
		if (studies == null)
			return;
		
		forms = new ArrayList<FormDefVersion>();
		lbForm.addItem("", "");
		
		for (StudyDef studyDef : studies) {
			for (FormDef formDef : studyDef.getForms()) {
				for (FormDefVersion formDefVersion : formDef.getVersions()) {
					lbForm.addItem(
					        formDef.getName() + "-" + formDefVersion.getName(),
					        String.valueOf(formDefVersion.getId()));
					forms.add(formDefVersion);
				}
			}
		}
	}
	
	public void loadFormsAccordingToPermissions() {
		lbForm.clear();
		if (studies == null) {
			return;
		}
		forms = new ArrayList<FormDefVersion>();
		lbForm.addItem("", "");
		List<UserFormMap> userMappedForms = RolesListUtil
		        .getPermissionResolver().getUserMappedForms(
		                Context.getAuthenticatedUser(), mappedForms);
		for (StudyDef studyDef : studies) {
			for (FormDef formDef : studyDef.getForms()) {
				if (StudyView.hasProperty(formDef, userMappedForms)) {
					for (FormDefVersion formDefVersion : formDef.getVersions()) {
						lbForm.addItem(
						        formDef.getName() + "-"
						                + formDefVersion.getName(), String.valueOf(formDefVersion.getId()));
						forms.add(formDefVersion);
					}
				}
			}
		}
	}
	
	public void search() {
		FormUtil.dlg.setText(constants.label_loading_data());
		FormUtil.dlg.center();
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				try {
					while (table.getRowCount() > 1)
						table.removeRow(1);
					
					StudyManagerServiceAsync service = Context
					        .getStudyManagerService();
					
					service.getFormData(getSelFormId(), getSelUserId(),
					        getSelFromDate(), getSelToDate(),
					        new OpenXDataAsyncCallback<List<FormDataHeader>>() {
						        
						        @Override
						        public void onOtherFailure(Throwable caught) {
							        FormUtil.dlg.hide();
							        Window.alert(caught.getMessage());
						        }
						        
						        @Override
						        public void onSuccess(
						                List<FormDataHeader> formDataList) {
							        loadData(formDataList);
							        FormUtil.dlg.hide();
						        }
					        });
				} catch (Exception ex) {
					FormUtil.dlg.hide();
					FormUtil.displayException(ex);
				}
			}
		});
	}
	
	private Integer getSelFormId() {
		int index = lbForm.getSelectedIndex();
		if (index > 0)
			return forms.get(index - 1).getId();
		return null;
	}
	
	private Integer getSelUserId() {
		int index = lbUser.getSelectedIndex();
		if (index > 0)
			return users.get(index - 1).getId();
		return null;
	}
	
	private Date getSelFromDate() {
		return dpFromDate.getSelectedDate();
	}
	
	private Date getSelToDate() {
		return dpToDate.getSelectedDate();
	}
	
	private void loadData(List<FormDataHeader> formDataList) {
		this.formDataList = formDataList;
		
		if (formDataList == null)
			return;
		
		DateTimeFormat dateFormat = DateTimeFormat.getFormat(Context
		        .getSetting("displayDateTimeFormat", "dd-MM-yyyy hh:mm:ss a"));
		
		int index = 0;
		String description = null;
		for (FormDataHeader formData : formDataList) {
			index++;
			
			description = formData.getDescription();
			if (description == null || description.trim().length() == 0)
				description = constants.label_no_description();
			
			table.setWidget(index, 0,
			        new Label(index + ") " + formData.getFormName()));
			table.setWidget(index, 1, new Label(formData.getVersionName()));
			table.setWidget(index, 2, new Label(description));
			table.setWidget(index, 3, new Label(formData.getCreator()));
			table.setWidget(index, 4,
			        new Label(dateFormat.format(formData.getDateCreated())));
			
			String text = "EMPTY";
			if (formData.getChangedBy() != null)
				text = formData.getChangedBy();
			
			table.setWidget(index, 5, new Label(text));
			
			text = "EMPTY";
			if (formData.getDateChanged() != null)
				text = dateFormat.format(formData.getDateChanged());
			
			table.setWidget(index, 6, new Label(text));
			
			HorizontalPanel panel = new HorizontalPanel();
			Button btn = new OpenXDataButton(constants.label_open());
			panel.add(btn);
			table.setWidget(index, 7, panel);
			table.getCellFormatter().setHorizontalAlignment(index, 7,
			        HasHorizontalAlignment.ALIGN_CENTER);
			
			btn.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Widget sender = (Widget) event.getSource();
					openForm(sender.getParent());
				}
			});
			
			btn = new Button("Delete");
			panel.add(btn);
			
			btn.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Widget sender = (Widget) event.getSource();
					deleteForm(sender.getParent());
				}
			});
		}
		
		addNewExportButtons(index);
	}
	
	private void addNewExportButtons(int index) {
		table.setWidget(++index, 0, btnAddNew);
		FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
		cellFormatter.setHorizontalAlignment(index, 0,
		        HasHorizontalAlignment.ALIGN_CENTER);
		cellFormatter.setColSpan(index, 0, 5);
		
		if (RolesListUtil.getPermissionResolver().isViewPermission(
		        Permission.PERM_VIEW_FORM_DATA)) {
			table.setWidget(index, 1, btnExportCsv);
			cellFormatter.setHorizontalAlignment(index, 1,
			        HasHorizontalAlignment.ALIGN_CENTER);
			cellFormatter.setColSpan(index, 1, 5);
		}
	}
	
	private int getWidgetIndex(Widget widget) {
		int rowCount = table.getRowCount();
		for (int row = 0; row < rowCount; row++) {
			if (widget == table.getWidget(row, 0)
			        || widget == table.getWidget(row, 7))
				return row;
		}
		return -1;
	}
	
	private void openForm(Widget widget) {
		int index = getWidgetIndex(widget) - 1;
		eventBus.fireEvent(new FormDataHeaderEvent(formDataList.get(index)));
	}
	
	private void deleteForm(Widget widget) {
		int index = getWidgetIndex(widget) - 1;
		FormDataHeader formDataHeader = formDataList.get(index);
		
		String desc = formDataHeader.getDescription();
		if (desc == null)
			desc = "";
		if (!Window.confirm("Do you really want to delete all data collected on this form " + desc + " ?"))
			return;
		
		formDataHeader.deleted = true;
        eventBus.fireEvent(new FormDataHeaderEvent(formDataHeader, EventType.DELETE));
		table.removeRow(index + 1); // TODO May need to delete after onSuccess
		                            // callback from the database
		formDataList.remove(index);
	}
	
	public void setUsers(List<User> users) {
		this.users = users;
		loadUsers();
	}
	
	public void setStudies(List<StudyDef> studies) {
		this.studies = studies;
		loadForms();
	}
	
	public void setUserMappedForms(List<UserFormMap> userMappedForms) {
		this.mappedForms = userMappedForms;
		loadForms();
	}
	
	private void exportCsv() {
		if (lbForm.getSelectedIndex() < 1) {
			// TODO add message for internationalization purposes
			Window.alert("Please select the form to export.");
			return;
		}
		
		new GetFileNameDialog(this, constants.label_export_as(),
		        constants.label_export(), "DataExport").center();
	}
	
	private void exportCsv(String fileName) {
		
		final String name = fileName;
		
		FormUtil.dlg.setText(constants.label_exporting_data());
		FormUtil.dlg.center();
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				try {
					Integer formId = getSelFormId();
					if (formId == null) {
						FormUtil.dlg.hide();
						// TODO add message for internationalization purposes
						Window.alert("Please select the form to export.");
						return;
					}
					
					String url = "dataexport?";
					url += "format=csv";
					url += "&formId=" + formId;
					url += "&filename=" + name;
					
					if (getSelUserId() != null)
						url += "&userId=" + getSelUserId();
					
					if (getSelFromDate() != null)
						url += "&fromDate=" + getSelFromDate().getTime();
					
					if (getSelToDate() != null)
						url += "&toDate=" + getSelToDate().getTime();
					
					Window.Location.replace(URL.encode(url));
					
					FormUtil.dlg.hide();
				} catch (Exception ex) {
					FormUtil.dlg.hide();
					FormUtil.displayException(ex);
				}
			}
		});
	}
	
	private void addNew() {
		FormDefVersion formDefVersion = null;
		
		int index = lbForm.getSelectedIndex();
		if (index > 0) {
			formDefVersion = forms.get(index - 1);
		} else {
			Window.alert("Please select the form to add.");
			return;
		}
		if (formDefVersion == null) {
			// TODO add message for internationalization purposes
			Window.alert("Please select the form to add.");
			return;
		}
		eventBus.fireEvent(new FormDataHeaderEvent(formDefVersion));
		//itemSelectionListener.onItemSelected(this, formDefVersion);
	}
	
    @Override
    public void onSetFileName(String fileName) {
        if (fileName != null && fileName.trim().length() > 0) {
            exportCsv(fileName);
        }
    }

    private void initHandlers() {
        EditableEvent.addHandler(eventBus, new EditableEvent.HandlerAdaptor<User>() {

            @Override
            public void onLoaded(List<User> items) {
                setUsers(items);
            }
        }).forClass(User.class);

        EditableEvent.addHandler(eventBus, new EditableEvent.HandlerAdaptor<UserFormMap>() {

            @Override
            public void onLoaded(List<UserFormMap> items) {
              setUserMappedForms(items);
            }
        }).forClass(UserFormMap.class);
        EditableEvent.addHandler(eventBus, new EditableEvent.HandlerAdaptor<StudyDef>() {

            @Override
            public void onLoaded(List<StudyDef> items) {
                setStudies(items);
            }
        }).forClass(StudyDef.class);
    }
}
