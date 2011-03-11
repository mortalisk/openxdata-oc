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
package org.openxdata.server.admin.client.view.treeview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.openxdata.server.admin.client.controller.facade.MainViewControllerFacade;
import org.openxdata.server.admin.client.controller.observe.OpenXDataObservable;
import org.openxdata.server.admin.client.controller.observe.SettingsObserver;
import org.openxdata.server.admin.client.permissions.UIViewLabels;
import org.openxdata.server.admin.client.permissions.util.RolesListUtil;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.constants.OpenXDataStackPanelConstants;
import org.openxdata.server.admin.client.view.listeners.OpenXDataViewApplicationEventListener;
import org.openxdata.server.admin.client.view.treeview.listeners.ExtendedContextInitMenuListener;
import org.openxdata.server.admin.client.view.widget.CompositeTreeItem;
import org.openxdata.server.admin.client.view.widget.TreeItemWidget;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.Setting;
import org.openxdata.server.admin.model.SettingGroup;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.inject.Inject;
import org.openxdata.server.admin.client.view.event.EditableEvent;
import org.openxdata.server.admin.client.view.widget.factory.OpenXDataWidgetFactory;

/**
 * This widget displays a list of settings and their groups in a tree view.
 * 
 * @author daniel
 * @author Victor
 * 
 */
public class SettingsTreeView extends OpenXDataBaseTreeView implements
        ExtendedContextInitMenuListener, OpenXDataViewApplicationEventListener,
        SettingsObserver {
	
	/** The list of deleted settings. */
	private List<Setting> deletedSettings;
	
	/** The list of setting groups. */
	private List<SettingGroup> settingGroups;
	
	/** The list of deleted setting groups. */
	private List<SettingGroup> deletedSettingGroups;
	
	/**
	 * Creates a new instance of the settings list widget.
	 * 
	 * @param openXDataViewFactory
	 * 
	 * @param images
	 *            the tree item images.
	 * @param permissionResolver
	 * @param mainView
	 */
        @Inject
	public SettingsTreeView(OpenXDataWidgetFactory openXDataViewFactory) {
		super("Settings", openXDataViewFactory);
                initHandlers();
	}
	
	@Override
	protected void setUp() {
		
		// Register this class with Event Dispatchers.
		super.registerWithEventDispatchers();
		
		openxdataStackPanel = widgetFactory.getOpenXdataStackPanel();
		
		// Initialize the Tree View
		tree = new Tree(images);
		tree.ensureSelectedItemVisible();
		
		// Setting Scroll Panel properties.
		scrollPanel.setWidget(tree);
		scrollPanel.setWidth("100%");
		scrollPanel.setHeight("100");
		
		// Make this class the Listener
		tree.addSelectionHandler(this);
		
		// Initialize the ScrollPanel to be the main widget for the Tree View
		initWidget(scrollPanel);
		
		// Initialize the Context Menu.
		popup = initContextMenu(this);
		
		// Maximize this widget
		Utilities.maximizeWidget(this);
	}
	
	/**
	 * Deletes the selected setting.
	 */
	@Override
	public void deleteSelectedItem() {
		TreeItem item = tree.getSelectedItem();
		if (item == null) {
			Window.alert(constants.label_deletesettingselect());
			return;
		}
		
		// TODO add message for internationalization purposes
		if (!Window.confirm(constants.label_deletesettingconfirmation()))
			return;
		
		if (item.getUserObject() instanceof Setting) {
			Setting setting = (Setting) item.getUserObject();
			deletedSettings.add(setting);
			setting.getSettingGroup().removeSetting(setting);
		} else {
			deletedSettingGroups.add((SettingGroup) item.getUserObject());
			settingGroups.remove(item.getUserObject());
		}
		
		if (item.getParentItem() == null)
			Utilities.removeRootItem(tree, item);
		else
			item.remove();
	}
	
	/**
	 * Loads setting groups and their contained child groups and settings.
	 * 
	 * @param settingGroups
	 *            the list of setting groups.
	 */
	public void loadSettingGroups(List<SettingGroup> settingGroups) {
		this.settingGroups = settingGroups;
		deletedSettings = new Vector<Setting>();
		deletedSettingGroups = new Vector<SettingGroup>();
		
		if (isLoadData()) {
			tree.clear();
			if (RolesListUtil.getPermissionResolver().isPermission(
			        Permission.PERM_VIEW_SETTINGSGROUP)) {
				TreeItem root;
				SettingGroup settingGroup;
				for (int i = 0; i < settingGroups.size(); i++) {
					settingGroup = settingGroups.get(i);
					if (settingGroup.getParentSettingGroup() == null) {
						root = new CompositeTreeItem(new TreeItemWidget(
						        images.lookup(), settingGroup.getName(), popup));
						root.setTitle(settingGroup.getDescription());
						root.setUserObject(settingGroup);
						tree.addItem(root);
						
						loadSettingGroup(settingGroup, root);
					}
				}
			}
		}
	}
	
	/**
	 * Loads a settings group with as a child of a given tree item.
	 * 
	 * @param settingGroup
	 *            the setting group.
	 * @param parent
	 *            the parent tree item.
	 */
	private void loadSettingGroup(SettingGroup settingGroup, TreeItem parent) {
		if (settingGroup.getSettings() != null) {
			for (Setting setting : settingGroup.getSettings()) {
				TreeItem item = new CompositeTreeItem(new TreeItemWidget(
				        images.filtersgroup(), setting.getName(), popup));
				
				item.setTitle(settingGroup.getDescription());
				item.setUserObject(setting);
				parent.addItem(item);
			}
		}
		
		if (settingGroup.getGroups() != null) {
			for (SettingGroup stgGroup : settingGroup.getGroups()) {
				TreeItem item = new CompositeTreeItem(new TreeItemWidget(
				        images.lookup(), stgGroup.getName(), popup));
				
				item.setTitle(stgGroup.getDescription());
				item.setUserObject(stgGroup);
				parent.addItem(item);
				
				loadSettingGroup(stgGroup, item);
			}
		}
	}
	
	/**
	 * Adds a new setting to the tree.
	 */
	public void addNewSetting() {
		TreeItem item = tree.getSelectedItem();
		if (item == null)
			return;
		
		if (!(item.getUserObject() instanceof SettingGroup)) {
			Window.alert("Please add the setting to a group.");
			return;
		}
		
		Setting setting = new Setting("New Setting");
		TreeItem root = new CompositeTreeItem(new TreeItemWidget(
		        images.filtersgroup(), setting.getName(), popup));
		root.setUserObject(setting);
		setting.setDirty(true);
		
		SettingGroup settingGroup = (SettingGroup) item.getUserObject();
		item.addItem(root);
		settingGroup.addSetting(setting);
		settingGroup.setDirty(true);
		setting.setSettingGroup(settingGroup);
		item.setState(true);
		
		tree.setSelectedItem(root);
	}
	
	/**
	 * Adds a new setting group to the tree.
	 */
	public void addNewSettingGroup() {
		SettingGroup settingGroup = new SettingGroup("New Setting Group");
		TreeItem root = new CompositeTreeItem(new TreeItemWidget(
		        images.lookup(), settingGroup.getName(), popup));
		root.setUserObject(settingGroup);
		settingGroup.setDirty(true);
		
		tree.addItem(root);
		settingGroups.add(settingGroup);
		tree.setSelectedItem(root);
	}
	
	/**
	 * Adds a new setting group as a child of the selected item which should be
	 * another setting group.
	 */
	@Override
	public void addNewItem() {
		TreeItem parent = tree.getSelectedItem();
		
		if (parent != null && parent.getUserObject() instanceof Setting) {
			Window.alert("Please add a group to another group instead of setting.");
			return;
		}
		
		SettingGroup settingGroup = new SettingGroup("New Setting Group");
		TreeItem root = new CompositeTreeItem(new TreeItemWidget(
		        images.lookup(), settingGroup.getName(), popup));
		root.setUserObject(settingGroup);
		settingGroup.setDirty(true);
		
		if (parent != null) {
			parent.addItem(root);
			settingGroup.setParentSettingGroup((SettingGroup) parent
			        .getUserObject());
			
			if (!settingGroup.getParentSettingGroup().isNew())
				settingGroups.add(settingGroup);
			else
				settingGroup.getParentSettingGroup().addSettingGroup(
				        settingGroup);
			
			parent.setState(true);
		} else {
			tree.addItem(root);
			settingGroups.add(settingGroup);
		}
		
		tree.setSelectedItem(root);
	}
	
	@Override
	public void changeEditableProperties(Object item) {
		TreeItem treeItem = tree.getSelectedItem();
		if (item == null)
			return; // How can this happen?
			
		if (item instanceof Setting) {
			Setting setting = (Setting) item;
			treeItem.setWidget(new TreeItemWidget(images.filtersgroup(),
			        setting.getName(), popup));
			treeItem.setTitle(setting.getDescription());
			setting.setDirty(true);
		} else {
			SettingGroup settingGroup = (SettingGroup) item;
			treeItem.setWidget(new TreeItemWidget(images.lookup(), settingGroup
			        .getName(), popup));
			treeItem.setTitle(settingGroup.getDescription());
			settingGroup.setDirty(true);
		}
	}
	
	/**
	 * Gets the list of deleted settings.
	 * 
	 * @return the deleted settings list.
	 */
	public List<Setting> getDeletedSettings() {
		return deletedSettings;
	}
	
	/**
	 * Gets a list of deleted setting groups.
	 * 
	 * @return the deleted setting groups list.
	 */
	public List<SettingGroup> getDeletedSettingGroups() {
		return deletedSettingGroups;
	}
	
	/**
	 * Checks if the current list of settings is valid for saving.
	 * 
	 * @return true if valid, else false.
	 */
	public boolean isValidSettingsList() {
		Map<String, String> map = new HashMap<String, String>();
		int index = tree.getItemCount();
		
		for (int j = 0; j < index; j++) {
			if (map.containsKey(tree.getItem(j).getText().toLowerCase())) {
				
				tree.setSelectedItem(tree.getItem(j));
				Window.alert(constants.label_existingsetting()
				        + tree.getItem(j).getText());
				return false;
			} else
				map.put(tree.getItem(j).getText().toLowerCase(), tree
				        .getItem(j).getText());
		}
		return true;
		
	}
	
	@Override
	UIViewLabels getContextMenuLabels() {
		UIViewLabels labels = new UIViewLabels();
		
		labels.setAddLabel(constants.label_addnewsettinggroup());
		labels.setDeleteLabel(constants.label_deletesetting());
		labels.setAddChildItemLabel(constants.label_addnewsetting());
		return labels;
	}
	
	@Override
	public void addNewChildItem() {
		addNewSetting();
		
	}
	
	@Override
	public void update(OpenXDataObservable observable,
	        Object observedModelObjects) {
		// do nothing
	}
	
	@Override
	public void updateSettingGroups(OpenXDataObservable observable,
	        List<SettingGroup> settingGroups) {
		this.settingGroups = settingGroups;
		loadSettingGroups(settingGroups);
		
	}
	
	@Override
	public void onDeleteItem() {
		if (openxdataStackPanel.getSelectedIndex() == OpenXDataStackPanelConstants.INDEX_SETTINGS) {
			if (RolesListUtil.getPermissionResolver().isDeleteSettings()) {
				deleteSelectedItem();
			} else {
				Window.alert("You do not have sufficient priviledges to delete Setting Groups and Settings! Contact your system administrator");
			}
		}
	}
	
	@Override
	public void onNewChildItem() {
		if (openxdataStackPanel.getSelectedIndex() == OpenXDataStackPanelConstants.INDEX_SETTINGS) {
			if (RolesListUtil.getPermissionResolver().isAddSettings()) {
				addNewChildItem();
			} else {
				Window.alert("You do not have sufficient priviledges to add Settings Groups and Settings! Contact your system administrator");
			}
		}
		
	}
	
	@Override
	public void onNewItem() {
		if (openxdataStackPanel.getSelectedIndex() == OpenXDataStackPanelConstants.INDEX_SETTINGS) {
			if (RolesListUtil.getPermissionResolver().isAddSettings()) {
				addNewItem();
			} else {
				Window.alert("You do not have sufficient priviledges to add Settings Groups and Settings! Contact your system administrator");
			}
		}
		
	}
	
	@Override
	public void onRefresh() {
		MainViewControllerFacade.refreshData();
		
	}
	
	@Override
	public void onSave() {
		if (openxdataStackPanel.getSelectedIndex() == OpenXDataStackPanelConstants.INDEX_SETTINGS) {
			MainViewControllerFacade.saveSettings();
		}
		
	}

     private void initHandlers() {
        EditableEvent.HandlerAdaptor<Setting> settingChangeHandler = new EditableEvent.HandlerAdaptor<Setting>() {

            @Override
            public void onChange(Setting item) {
                changeEditableProperties(item);
            }
        };
        EditableEvent.addHandler(eventBus, settingChangeHandler).forClass(Setting.class);

        EditableEvent.addHandler(eventBus, new EditableEvent.HandlerAdaptor<SettingGroup>() {

            @Override
            public void onChange(SettingGroup item) {
                changeEditableProperties(item);
            }
        }).forClass(SettingGroup.class);

    }
}
