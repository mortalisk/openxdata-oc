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

import org.openxdata.server.admin.client.internationalization.OpenXdataConstants;
import org.openxdata.server.admin.client.view.widget.OpenXDataButton;
import org.openxdata.server.admin.client.view.widget.OpenXDataFlexTable;
import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.admin.model.TaskParam;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.openxdata.server.admin.client.view.event.EditableEvent;

/**
 * This widget displays a list of parameters for the selected task and lets you
 * edit them.
 * 
 * @author daniel
 * 
 */
public class ParametersView extends Composite implements ClickHandler {
	
	/** Table to arrange parameter names and their values. */
	private FlexTable table = new OpenXDataFlexTable();
	
	/** Main widget. */
	private VerticalPanel panel = new VerticalPanel();
	
	/** Button to add new parameters. */
	private Button addNewParameter;
	
	/** The task definition object whose parameters we are displaying. */
	private TaskDef taskDef;
	
	private static OpenXdataConstants constants = GWT
	        .create(OpenXdataConstants.class);
        private final EventBus eventBus;


        @Inject
	ParametersView(EventBus eventBus) {
		addNewParameter = new OpenXDataButton(constants.label_add_parameter());
		table.setWidget(0, 0, new Label(constants.label_name()));
		table.setWidget(0, 1, new Label(constants.label_value()));
		table.setWidget(0, 2, new Label(constants.label_action()));
		
		FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
		cellFormatter.setWidth(0, 2, "10%");
		table.setStyleName("cw-FlexTable");
		
		panel.add(table);
		panel.add(addNewParameter);
		
		addNewParameter.addClickHandler(this);
		addNewParameter.setEnabled(false);
		
		initWidget(panel);
               this.eventBus = eventBus;
               initHandlers();
                
	}
	
	/**
	 * Called when on wants to add a new parameter or remove an existing one.
	 */
	@Override
	public void onClick(ClickEvent event) {
		Object sender = event.getSource();
		if (taskDef == null)
			return;
		
		if (sender.equals(addNewParameter)) {
			TaskParam param = new TaskParam(taskDef, "", "");
			taskDef.addParam(param);
			taskDef.setDirty(true);
			addParam(param);
		} else {
			// Removing an existing parameter.
			int rowCount = table.getRowCount();
			for (int row = 0; row < rowCount; row++) {
				if (sender.equals(table.getWidget(row, 2))) {
					table.removeRow(row);
					taskDef.deleteParamAt(row - 1);
					taskDef.setDirty(true);
					break;
				}
			}
		}
	}
	
	/**
	 * Adds a new parameter row to the parameters table.
	 * 
	 * @param param
	 *            the parameter object.
	 */
	private void addParam(TaskParam param) {
		int row = table.getRowCount();
		
		TextBox txtName = new TextBox();
		TextBox txtValue = new TextBox();
		
		txtName.setText(param.getName());
		txtValue.setText(param.getValue());
		
		table.setWidget(row, 0, txtName);
		table.setWidget(row, 1, txtValue);
		Button removeParameter = new OpenXDataButton(constants.label_remove());
		removeParameter.addClickHandler(this);
		table.setWidget(row, 2, removeParameter);
		
		table.getFlexCellFormatter().setWidth(row, 2, "10%");
		table.getWidget(row, 0).setWidth("100%");
		table.getWidget(row, 1).setWidth("100%");
		
		txtName.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				Widget sender = (Widget) event.getSource();
				updateName(sender);
			}
		});
		txtName.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				Widget sender = (Widget) event.getSource();
				updateName(sender);
			}
		});
		
		txtValue.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				Widget sender = (Widget) event.getSource();
				updateValue(sender);
			}
		});
		
		txtValue.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				Widget sender = (Widget) event.getSource();
				updateValue(sender);
			}
		});
	}
	
	/**
	 * Updates a parameter with the new name as typed by the user.
	 * 
	 * @param sender
	 *            the widget having the new name.
	 */
	private void updateName(Widget sender) {
		int index = getWidgetIndex(sender) - 1;
		TaskParam param = taskDef.getParamAt(index);
		param.setName(((TextBox) sender).getText());
		taskDef.setDirty(true);
	}
	
	/**
	 * Updates a parameter with the new value as typed by the user.
	 * 
	 * @param sender
	 *            the widget having the new value.
	 */
	private void updateValue(Widget sender) {
		int index = getWidgetIndex(sender) - 1;
		TaskParam param = taskDef.getParamAt(index);
		param.setValue(((TextBox) sender).getText());
		taskDef.setDirty(true);
	}
	
	/**
	 * Gets the index of a given widget in the table.
	 * 
	 * @param widget
	 *            the widget whose index to get.
	 * @return the index of the widget.
	 */
	private int getWidgetIndex(Widget widget) {
		int rowCount = table.getRowCount();
		for (int row = 0; row < rowCount; row++) {
			if (widget == table.getWidget(row, 0)
			        || widget == table.getWidget(row, 1))
				return row;
		}
		return -1;
	}
	
	
	
	public void onItemSelected(Composite sender, Object item) {
		this.taskDef = (TaskDef) item;
		
		// Remove all table rows and leave one for the heading
		while (table.getRowCount() > 1)
			table.removeRow(1);
		
		// Allow adding of new parameters only if we have a task and which has
		// parameters.
		addNewParameter.setEnabled(taskDef != null
		        && taskDef.getParameters() != null);
		
		if (taskDef == null || taskDef.getParameters() == null)
			return;
		
		// Add one table row for each parameter in the task.
		for (TaskParam param : taskDef.getParameters())
			addParam(param);
	}

    private void initHandlers() {
        EditableEvent.addHandler(eventBus, new EditableEvent.HandlerAdaptor<TaskDef>() {

            @Override
            public void onChange(TaskDef item) {
                onItemSelected(null, item);
            }

            @Override
            public void onDeleted(TaskDef item) {
                onItemSelected(null, null);
            }
        }).forClass(TaskDef.class);
    }
}
