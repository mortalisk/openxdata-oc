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
package org.openxdata.server.admin.client.controller;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.controller.callback.OpenXDataAsyncCallback;
import org.openxdata.server.admin.client.controller.callback.SaveAsyncCallback;
import org.openxdata.server.admin.client.controller.observe.OpenXDataObservable;
import org.openxdata.server.admin.client.listeners.SaveCompleteListener;
import org.openxdata.server.admin.client.locale.OpenXdataText;
import org.openxdata.server.admin.client.locale.TextConstants;
import org.openxdata.server.admin.client.util.AsyncCallBackUtil;
import org.openxdata.server.admin.client.util.MainViewControllerUtil;
import org.openxdata.server.admin.client.view.treeview.SettingsTreeView;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.Setting;
import org.openxdata.server.admin.model.SettingGroup;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import org.purc.purcforms.client.util.FormUtil;

/**
 * This controller deals with loading and saving of Settings groups and Settings related data
 * to and from the database for the various Settings groups and settings related views.
 * 
 * @author Angel
 *
 */
public class SettingsViewController extends OpenXDataObservable implements SaveCompleteListener {

	private List<SettingGroup> settingGroups;
	
	private SettingsTreeView settingsTreeView;
	
	/**
	 * Constructs an instance of this <tt>class</tt> given a <tt>View</tt> to update with <tt>Tasks.</tt>
	 * @param settingsTreeView <tt>View</tt> that is observing this <tt>Class.</tt>
	 */
	public SettingsViewController(SettingsTreeView settingsTreeView) {
		this.settingsTreeView = settingsTreeView;
	}

	/**
	 * Loads settings from the database.
	 * 
	 * @param reload set to false if you want to use the cached settings, if any, without
	 *        having to reload them from the database.
	 */
	public void loadSettings(boolean reload){
		if(settingGroups != null && !reload)
			return;

		//FormUtil.dlg.setText("loading Settings");
		FormUtil.dlg.setText(OpenXdataText.get(TextConstants.LOADING_SETTINGS));
		
		FormUtil.dlg.center();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				try{
					Context.getSettingService().getSettings(new OpenXDataAsyncCallback<List<SettingGroup>>() {
						@Override
						public void onOtherFailure(Throwable caught) {
							FormUtil.dlg.hide();
							AsyncCallBackUtil.handleGenericOpenXDataException(caught);
						}

						@Override
						public void onSuccess(List<SettingGroup> result) {
							settingGroups = result;
							
							setChanged();
						    notifyObservers(settingGroups, SettingGroup.class);
						    
							FormUtil.dlg.hide();
							
							//Removing erratic background windows
							MainViewControllerUtil.removeAllProgressWindows();
						}
					});
				}
				catch(Exception ex){
					FormUtil.dlg.hide();
					FormUtil.displayException(ex);
				}	
			}
		});
	}
	
	/**
	 * Saves new and modified settings to the database.
	 */
	public void saveSettings(){
		if(settingGroups == null)
			return;

		if(!settingsTreeView.isValidSettingsList())
			return;

		List<Editable> deletedList = new ArrayList<Editable>();
		List<Setting> deletedSettings = settingsTreeView.getDeletedSettings();
		List<SettingGroup> deletedSettingGroups = settingsTreeView.getDeletedSettingGroups();

		int count = (deletedSettings != null ? deletedSettings.size() : 0);
		count += (MainViewControllerUtil.getDirtyCount(settingGroups) + (deletedSettingGroups != null ? deletedSettingGroups.size() : 0));

		for(Setting setting : deletedSettings)
			deletedList.add(setting);
		
		for(SettingGroup grp : deletedSettingGroups)
			deletedList.add(grp);
		
		SaveAsyncCallback callback = new SaveAsyncCallback(count,
				OpenXdataText.get(TextConstants.SETTINGS_SAVED_SUCCESSFULLY),OpenXdataText.get(TextConstants.PROBLEM_SAVING_SETTINGS),settingGroups, deletedList, this);

		//Save new and modified settings and their groups.
		for(SettingGroup grp : settingGroups) {
			if(!grp.isDirty())
				continue;
			else{
				callback.setCurrentItem(grp);
				MainViewControllerUtil.setEditableProperties(grp);
			}

			Context.getSettingService().saveSettingGroup(grp, callback);
		}

		deleteObjects(deletedSettings, deletedSettingGroups, callback);
	}

	/**
	 * Deletes Settings and Settings Groups.
	 * 
	 * @param deletedSettings List of settings to delete.
	 * @param deletedSettingGroups List of Setting Groups to delete.
	 * @param callback 
	 */
	private void deleteObjects(List<Setting> deletedSettings, List<SettingGroup> deletedSettingGroups, SaveAsyncCallback callback) {
		
		//Save deleted setting groups.
		if(deletedSettingGroups != null){
			for(SettingGroup grp : deletedSettingGroups) {
				callback.setCurrentItem(grp);
				Context.getSettingService().deleteSettingGroup(grp, callback);
			}
			
			deletedSettingGroups.clear();
		}

		//Save deleted settings.
		if(deletedSettings != null){
			for(Setting setting : deletedSettings) {
				callback.setCurrentItem(setting);
				Context.getSettingService().deleteSetting(setting, callback);
			}
			
			deletedSettings.clear();
		}
	}

    @Override
	public void onSaveComplete(List<? extends Editable> modifiedList, List<? extends Editable> deletedList) {
    	MainViewControllerUtil.onSaveComplete(modifiedList, deletedList);
    	loadSettings(true);
	}
}
