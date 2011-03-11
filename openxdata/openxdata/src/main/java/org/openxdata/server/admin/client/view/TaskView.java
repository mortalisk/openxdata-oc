/*
 *  Licensed to the OpenXdata Foundation (OXDF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The OXDF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, 
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 *  Copyright 2010 http://www.openxdata.org.
 */
package org.openxdata.server.admin.client.view;

import com.google.gwt.user.client.ui.Widget;
import org.openxdata.server.admin.client.internationalization.OpenXdataConstants;
import org.openxdata.server.admin.client.permissions.util.RolesListUtil;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.event.ItemSelectedEvent;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.TaskDef;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.Inject;
import org.openxdata.server.admin.client.presenter.IPresenter;
import org.openxdata.server.admin.client.presenter.WidgetDisplay;
import org.openxdata.server.admin.client.view.event.EditableEvent;
import org.openxdata.server.admin.client.view.event.PresenterChangeEvent;
import org.openxdata.server.admin.client.view.event.ViewEvent;
import org.openxdata.server.admin.client.view.widget.factory.OpenXDataWidgetFactory;

/**
 * Encapsulates properties of the selected task and lets you manipulate them.
 * 
 * @author daniel
 * @author Angel
 * 
 */
public class TaskView extends OpenXDataBaseView implements IPresenter<WidgetDisplay> {
	
	private static OpenXdataConstants constants = GWT
	        .create(OpenXdataConstants.class);
	
	/** Widget for entering the name of the task. */
	private TextBox txtName;
	
	/** Widget for entering the description of the task. */
	private TextBox txtDescription;
	
	/** Widget for entering the class of the task. */
	private TextBox txtClass;
	
	/** Label to show whether a task is running or not. */
	private Label lblStarted;
	
	/** Widget for scheduling the task. */
	private ScheduleView scheduleView;
	
	/** Widget for displaying the task parameter list. */
        private ParametersView parametersView;
	
	/** The task definition object. */
	private TaskDef taskDef;
	
	/**
	 * Creates a new instance of the task view.
	 * 
	 * @param itemChangeListener
	 *            listener to <tt>Task</tt> property changes.
	 * @param openXDataViewFactory
	 */
	@Inject
	public TaskView(OpenXDataWidgetFactory openXDataViewFactory) {
		super(openXDataViewFactory);
	//	initHandlers();
		setUp();
	}
	
	private void setUp() {
		
		txtName = new TextBox();
		
		txtDescription = new TextBox();
		
		txtClass = new TextBox();
		
		lblStarted = new Label("false");

                parametersView = new ParametersView(eventBus);
		//scheduleView = new ScheduleView();

		
		openxdataStackPanel = widgetFactory.getOpenXdataStackPanel();
		
		// Register this class with Event Dispatchers.
		super.registerWithEventDispatchers();
		
		if (RolesListUtil.getPermissionResolver().isPermission("Tasks")) {
			loadView();
		} else
			loadPermissionLessView();
		
	}
	
	private void loadPermissionLessView() {
		table.setWidget(0, 0,
		        new Label(constants.ascertain_permissionLessView() + "Tasks"));
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
		table.setWidget(2, 0, new Label(constants.label_class()));
		table.setWidget(3, 0, new Label(constants.label_started()));
		
		table.setWidget(0, 1, txtName);
		table.setWidget(1, 1, txtDescription);
		table.setWidget(2, 1, txtClass);
		table.setWidget(3, 1, lblStarted);
		
		txtName.setWidth("100%");
		txtDescription.setWidth("100%");
		txtClass.setWidth("100%");
		
		FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
		cellFormatter.setWidth(0, 0, "20%");
		cellFormatter.setColSpan(0, 1, 2);
		cellFormatter.setColSpan(1, 1, 2);
		cellFormatter.setColSpan(2, 1, 2);
		cellFormatter.setColSpan(3, 1, 2);
		
		table.getRowFormatter().removeStyleName(0, "FlexTable-Header");
		Utilities.maximizeWidget(table);
		
		tabs.add(table, constants.label_definition());
		
		if (RolesListUtil.getPermissionResolver().isExtraPermission(
		        Permission.PERM_TASK_SCHEDULING)) {
			tabs.add(scheduleView, constants.label_schedule());
		}
		if (RolesListUtil.getPermissionResolver().isAddPermission(
		        Permission.PERM_TASK_ADDING_PARAMETER)) {
			tabs.add(parametersView, constants.label_parameters());
		}
		
		Utilities.maximizeWidget(tabs);
		
		initWidget(tabs);
		
		setWidth("100%");
		setupEventListeners();
		tabs.selectTab(0);
		
		setEnabled(false);
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
			public void onKeyPress(KeyPressEvent event) {
				updateDescription();
				if (event.getCharCode() == KeyCodes.KEY_ENTER)
					txtClass.setFocus(true);
			}
		});
		
		txtClass.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent arg0) {
				updateClass();
			}
		});
		txtClass.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				updateClass();
			}
		});
	}
	
	/**
	 * Updates a task with the new name as typed by the user.
	 * 
	 * @param sender
	 *            the widget having the new name.
	 */
	private void updateName() {
		taskDef.setName(txtName.getText());
                 eventBus.fireEvent(new EditableEvent<TaskDef>(taskDef));
	}
	
	/**
	 * Updates a task with the new description as typed by the user.
	 * 
	 * @param sender
	 *            the widget having the new description.
	 */
	private void updateDescription() {
		taskDef.setDescription(txtDescription.getText());
                eventBus.fireEvent(new EditableEvent<TaskDef>(taskDef));
	}
	
	/**
	 * Updates a parameter with the new class as typed by the user.
	 * 
	 * @param sender
	 *            the widget having the new class.
	 */
	private void updateClass() {
		taskDef.setTaskClass(txtClass.getText());
                eventBus.fireEvent(new EditableEvent<TaskDef>(taskDef));
	}
	
	/**
	 * @see org.openxdata.server.admin.client.listeners.ItemSelectionListener#onItemSelected(Composite,
	 *      Object)
	 */

	public void onItemSelected(Composite sender, Object item) {
		setEnabled(item != null);
		
		if (item == null)
			clear();
		else {
			taskDef = (TaskDef) item;
			
			txtName.setText(taskDef.getName());
			txtDescription.setText(taskDef.getDescription());
			txtClass.setText(taskDef.getTaskClass());
			lblStarted.setText(taskDef.isRunning() ? "True" : "False");
		}
		
		scheduleView.onItemSelected(taskDef);
		parametersView.onItemSelected(this, taskDef);
	}
	
	/**
	 * Sets focus to the first widget.
	 */
	public void setFocus() {
		txtName.setFocus(true);
		txtName.selectAll();
	}
	
	/**
	 * Sets whether to enabled or disable this widget.
	 * 
	 * @param enabled
	 *            set to true to enable, else false.
	 */
	private void setEnabled(boolean enabled) {
		txtName.setEnabled(enabled);
		txtClass.setEnabled(enabled);
		txtDescription.setEnabled(enabled);
	}
	
	/**
	 * Clears contents of widgets in this view.
	 */
	private void clear() {
		taskDef = null;
		txtName.setText(null);
		txtDescription.setText(null);
		txtClass.setText(null);
	}
	
    private void initHandlers() {
        ItemSelectedEvent.addHandler(eventBus, new ItemSelectedEvent.Handler<TaskDef>() {

            @Override
            public void onSelected(Composite sender, TaskDef item) {
                onItemSelected(sender, item);
            }
        }).forClass(TaskDef.class);

        EditableEvent.addHandler(eventBus, new EditableEvent.HandlerAdaptor<TaskDef>() {

            @Override
            public void onDeleted(TaskDef item) {
                if (taskDef.equals(item))
                    onItemSelected(null, null);
            }
        }).forClass(TaskDef.class);

          GWT.log("Registering View EvenHandler for TaskView");
        ViewEvent.addHandler(eventBus, new ViewEvent.Handler<TaskDef>() {

            @Override
            public void onView() {
                GWT.log("Switching to TaskPresenter");
               eventBus.fireEvent(new PresenterChangeEvent(thisTaskView));
            }
        }).forClass(TaskDef.class);
    }

	public void setEventBus(EventBus eventBus) {
		super.eventBus = eventBus;
		//initHandlers();
	}
    private TaskView thisTaskView = this;

    @Override
    public WidgetDisplay getDisplay() {
        return new WidgetDisplay() {

            @Override
            public Widget asWidget() {
                return thisTaskView;
            }
        };
    }
}
