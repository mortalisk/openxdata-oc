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

import java.util.List;

import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.controller.callback.OpenXDataAsyncCallback;
import org.openxdata.server.admin.client.controller.callback.SaveAsyncCallback;
import org.openxdata.server.admin.client.listeners.SaveCompleteListener;
import org.openxdata.server.admin.client.locale.OpenXdataText;
import org.openxdata.server.admin.client.locale.TextConstants;
import org.openxdata.server.admin.client.util.AsyncCallBackUtil;
import org.openxdata.server.admin.client.util.MainViewControllerUtil;
import org.openxdata.server.admin.client.view.treeview.StudiesTreeView;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.admin.model.mapping.UserStudyMap;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import org.purc.purcforms.client.util.FormUtil;

/**
 * This controller deals with loading and saving of Studies and mapped studies related data
 * to and from the database for the various Study and Mapped Studies related views.
 * 
 * @author Angel
 *
 */
public class StudiesViewController implements SaveCompleteListener {
	
	private StudiesTreeView studiesTreeView;	

	private List<StudyDef> studies;
	private List<UserFormMap> userMappedForms;
	private List<UserStudyMap> userMappedStudies;	
	
	/**
	 * Constructs an instance of this <tt>class</tt> given a <tt>View</tt> to update with <tt>Studies.</tt>
	 * 
	 * @param studiesTreeView <tt>View</tt> that is observing this <tt>class.</tt>
	 * @param userView <tt>UserView</tt> to receive updates from this <tt>class.</tt>
	 */
	public StudiesViewController(StudiesTreeView studiesTreeView) {
		this.studiesTreeView = studiesTreeView;

	}

	/**
	 * Loads studies from the database.
	 * 
	 * @param reload set to false if you want to use the cached studies, if any, without
	 *        having to reload them from the database.
	 */
	public void loadStudies(boolean reload){
		if(studies != null && !reload)
			return;

		FormUtil.dlg.setText(OpenXdataText.get(TextConstants.LOADING_STUDIES));
		FormUtil.dlg.center();
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				try{
					Context.getStudyManagerService().getStudies(new OpenXDataAsyncCallback<List<StudyDef>>() {
						@Override
						public void onOtherFailure(Throwable caught) {
							FormUtil.dlg.hide();
							AsyncCallBackUtil.handleGenericOpenXDataException(caught);
						}

						@Override
						public void onSuccess(List<StudyDef> result) {
							studies = result;
							//setChanged();
						    studiesTreeView.updateStudies( result);
						    
							FormUtil.dlg.hide();
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
	 * Loads mapped studies from the database.
	 * The idea of loading all at once is to avoid multiple database 
	 * calls that might be expensive
	 * 
	 * @param reload set to false if you want to use the cached studies, if any, without
	 *        having to reload them from the database.
	 */
	public void loadMappedStudies(boolean reload){
		if(userMappedStudies != null && !reload)
			return;
		
		FormUtil.dlg.setText(OpenXdataText.get(TextConstants.LOADING_MAPPED_STUDIES));
		FormUtil.dlg.center();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				try{
					Context.getStudyManagerService().getUserMappedStudies(new OpenXDataAsyncCallback<List<UserStudyMap>>() {
						@Override
						public void onOtherFailure(Throwable caught) {
							FormUtil.dlg.hide();
							AsyncCallBackUtil.handleGenericOpenXDataException(caught);
						}

						@Override
						public void onSuccess(List<UserStudyMap> result) {
							userMappedStudies = result;
//							setChanged();
						        studiesTreeView.updateUserMappedStudies(result);
							
							FormUtil.dlg.hide();
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
	 * Loads all <code>User Mapped Forms</code> from the database.
	 * @param reload Flag to indicate if database call should be made.
	 */
	public void loadMappedForms(boolean reload){
		if(userMappedForms != null && !reload)
			return;
		
		FormUtil.dlg.setText("Loading Mapped Forms");
		FormUtil.dlg.center();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				try{
					Context.getStudyManagerService().getUserMappedForms(new OpenXDataAsyncCallback<List<UserFormMap>>() {
						@Override
						public void onOtherFailure(Throwable caught) {
							FormUtil.dlg.hide();
							AsyncCallBackUtil.handleGenericOpenXDataException(caught);
						}

						@Override
						public void onSuccess(List<UserFormMap> mappedForms) {
							userMappedForms = mappedForms;							
							
//							setChanged();
						        studiesTreeView.updateUserMappedForms( mappedForms);
							
							FormUtil.dlg.hide();
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
	 * Saves new and modified studies to the database.
	 */
	public void saveStudies(){
		if(studies == null)
			return;

		if(!studiesTreeView.isValidStudyList())
			return;

		List<StudyDef> deletedStudies = studiesTreeView.getDeletedStudies();
		
		SaveAsyncCallback callback = new SaveAsyncCallback(MainViewControllerUtil.getDirtyCount(studies) + (deletedStudies != null ? deletedStudies.size() : 0),
				OpenXdataText.get(TextConstants.STUDIES_SAVED_SUCCESSFULLY),OpenXdataText.get(TextConstants.PROBLEM_SAVING_STUDIES),studies,deletedStudies,this);

		//Save new and modified studies.
		for(StudyDef study : studies){
			if(!study.isDirty())
				continue;
			else{
				callback.setCurrentItem(study);
				MainViewControllerUtil.setEditableProperties(study);
			}

			Context.getStudyManagerService().saveStudy(study, callback);
		}

		//Save deleted studies.
		if(deletedStudies != null){
			for(StudyDef xStudy : deletedStudies){
				callback.setCurrentItem(xStudy);
				Context.getStudyManagerService().deleteStudy(xStudy, callback);
			}

			deletedStudies.clear();
		}

	}
	
    @Override
	public void onSaveComplete(List<? extends Editable> modifiedList, List<? extends Editable> deletedList) {
		
    	MainViewControllerUtil.onSaveComplete(modifiedList, deletedList);
    	
    	// Reload objects.
        loadStudies(true);
        loadMappedStudies(true);
        loadMappedForms(true);
	}
}
