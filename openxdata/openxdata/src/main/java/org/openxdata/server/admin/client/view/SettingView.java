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
import org.openxdata.server.admin.client.permissions.util.RolesListUtil;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.event.ItemSelectedEvent;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.Setting;
import org.openxdata.server.admin.model.SettingGroup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.Inject;
import org.openxdata.server.admin.client.view.event.EditableEvent;
import org.openxdata.server.admin.client.view.widget.factory.OpenXDataWidgetFactory;

/**
 * This widget displays properties of the selected setting and lets you edit
 * them.
 * 
 * @author daniel
 * @author Angel
 * @author Victor
 * 
 */
public class SettingView extends OpenXDataBaseView {
	
	private static OpenXdataConstants constants = GWT
	        .create(OpenXdataConstants.class);
	
	/** Widget for entering the name of the setting. */
	private TextBox txtName;
	
	/** Widget for entering the description of the setting. */
	private TextBox txtDescription;
	
	/** Widget for entering the value of the setting. */
	private TextBox txtValue;
	
	/** Widget for displaying the label or title for setting value. */
	private Label lblValue;
	
	/** The currently selected and displayed setting. */
	private Setting setting;
	
	/** The currently selected and displayed setting group. */
	private SettingGroup settingGroup;
	
	/**
	 * Creates a new instance of the setting view.
	 * 
	 * @param itemChangeListener
	 *            listener to <tt>Setting</tt> property changes.
	 * @param openXDataViewFactory
	 */
        @Inject
	public SettingView(OpenXDataWidgetFactory openXDataViewFactory) {
		super( openXDataViewFactory);
		bindHandlers();
		setUp();
	}
	
	private void setUp() {
		
		txtName = new TextBox();
		
		txtDescription = new TextBox();
		
		txtValue = new TextBox();
		
		lblValue = new Label(constants.label_value());
		
		openxdataStackPanel = widgetFactory.getOpenXdataStackPanel();
		
		// Register this class with Event Dispatchers.
		super.registerWithEventDispatchers();
		//
		// if (RolesListUtil.getPermissionResolver().isPermission("Settings")) {
		// loadView();
		// } else {
		// loadPermissionLessView();
		// }
		if (RolesListUtil.getPermissionResolver().isPermission(
		        Permission.PERM_VIEW_SETTINGS)) {
			loadView();
		} else {
			loadPermissionLessView();
		}
	}
	
	private void loadView() {
		
		Label nameLabel = new Label(constants.label_name());
		Label descLabel = new Label(constants.label_description());
		FlexCellFormatter formatter = table.getFlexCellFormatter();
		
		table.setWidget(0, 0, nameLabel);
		table.setWidget(1, 0, descLabel);
		table.setWidget(2, 0, lblValue);
		
		table.setWidget(0, 1, txtName);
		txtName.setWidth("100%");
		formatter.setHorizontalAlignment(0, 1,
		        HasHorizontalAlignment.ALIGN_LEFT);
		
		table.setWidget(1, 1, txtDescription);
		txtDescription.setWidth("100%");
		formatter.setHorizontalAlignment(1, 1,
		        HasHorizontalAlignment.ALIGN_LEFT);
		
		table.setWidget(2, 1, txtValue);
		txtValue.setWidth("100%");
		formatter.setHorizontalAlignment(2, 1,
		        HasHorizontalAlignment.ALIGN_LEFT);
		
		formatter.setWidth(1, 0, "10%");
		formatter.setWidth(2, 0, "10%");
		formatter.setWidth(3, 0, "10%");
		
		table.getRowFormatter().removeStyleName(0, "FlexTable-Header");
		
		Utilities.maximizeWidget(table);
		Utilities.maximizeWidget(tabs);
		
		tabs.add(table, constants.label_properties());
		
		initWidget(tabs);
		
		setWidth("100%");
		setupEventListeners();
		tabs.selectTab(0);
		
		setEnabled(false);
		lblValue.setVisible(false);
		txtValue.setVisible(false);
		
	}
	
	private void loadPermissionLessView() {
		table.setWidget(
		        0,
		        0,
		        new Label(constants.ascertain_permissionLessView() + "Settings"));
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
					txtValue.setFocus(true);
			}
		});
		txtValue.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent arg0) {
				updateValue();
			}
		});
		txtValue.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				updateValue();
			}
		});
	}
	
	/**
	 * Updates a setting with the new name as typed by the user.
	 * 
	 * @param sender
	 *            the widget having the new name.
	 */
	private void updateName() {
		if (setting != null) {
			setting.setName(txtName.getText());
			eventBus.fireEvent(new EditableEvent<Setting>(setting));
		} else {
			settingGroup.setName(txtName.getText());
                        eventBus.fireEvent(new EditableEvent<SettingGroup>(settingGroup));
		}
	}
	
	/**
	 * Updates a setting with the new description as typed by the user.
	 * 
	 * @param sender
	 *            the widget having the new description.
	 */
	private void updateDescription() {
		if (setting != null) {
			setting.setDescription(txtDescription.getText());
			eventBus.fireEvent(new EditableEvent<Setting>(setting));
		} else {
			settingGroup.setDescription(txtDescription.getText());
			 eventBus.fireEvent(new EditableEvent<SettingGroup>(settingGroup));
		}
	}
	
	/**
	 * Updates a setting with the new value as typed by the user.
	 * 
	 * @param sender
	 *            the widget having the new value.
	 */
	private void updateValue() {
		if (setting != null) {
			setting.setValue(txtValue.getText());
			eventBus.fireEvent(new EditableEvent<Setting>(setting));
		}
	}
	
	/**
	 * @see org.openxdata.server.admin.client.listeners.ItemSelectionListener#onItemSelected(Composite,
	 *      Object)
	 */
	
	public void onItemSelected(Composite sender, Object item) {
		
		setEnabled(item != null);
		
		setting = null;
		settingGroup = null;
		
		if (item instanceof Setting) {
			setting = (Setting) item;
			txtName.setText(setting.getName());
			txtDescription.setText(setting.getDescription());
			txtValue.setText(setting.getValue());
			
			lblValue.setVisible(true);
			txtValue.setVisible(true);
		} else {
			settingGroup = (SettingGroup) item;
			txtName.setText(settingGroup.getName());
			txtDescription.setText(settingGroup.getDescription());
			
			lblValue.setVisible(false);
			txtValue.setVisible(false);
		}
	}
	
	/**
	 * Sets whether to enabled or disable this widget.
	 * 
	 * @param enabled
	 *            set to true to enable, else false.
	 */
	private void setEnabled(boolean enabled) {
		txtName.setEnabled(enabled);
		txtValue.setEnabled(enabled);
		txtDescription.setEnabled(enabled);
	}
	
    private void bindHandlers() {
        ItemSelectedEvent.addHandler(eventBus, new ItemSelectedEvent.Handler<Setting>() {

            @Override
            public void onSelected(Composite sender, Setting item) {
                onItemSelected(sender, item);
            }
        }).forClass(Setting.class);
        ItemSelectedEvent.addHandler(eventBus, new ItemSelectedEvent.Handler<SettingGroup>() {

            @Override
            public void onSelected(Composite sender, SettingGroup item) {
                onItemSelected(sender, item);
            }
        }).forClass(SettingGroup.class);
    }
}
