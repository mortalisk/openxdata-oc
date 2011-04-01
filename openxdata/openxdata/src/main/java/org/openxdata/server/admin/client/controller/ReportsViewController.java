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
import org.openxdata.server.admin.client.view.ReportView;
import org.openxdata.server.admin.client.view.treeview.ReportsTreeView;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.Report;
import org.openxdata.server.admin.model.ReportGroup;
import org.openxdata.server.admin.model.mapping.UserReportGroupMap;
import org.openxdata.server.admin.model.mapping.UserReportMap;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import org.purc.purcforms.client.util.FormUtil;

/**
 * This controller deals with loading and saving of
 * Reports related data to and from the database for the various report related views.
 * 
 * @author Angel
 *
 */
public class ReportsViewController extends OpenXDataObservable implements SaveCompleteListener {

	private ReportView reportView;
	private List<ReportGroup> reportGroups;
	private ReportsTreeView reportsTreeView;
	
	private List<UserReportMap> userMappedReports;
	private List<UserReportGroupMap> userMappedReportGroups;
	
	/**
	 * Constructs an instance of this <tt>class</tt> given a <tt>View</tt> to update with <tt>Reports.</tt>
	 * 
	 * @param reportsTreeView <tt>Tree View</tt> that is observing this <tt>class.</tt>
	 * @param reportView <tt>ReportView</tt> to receive updates from this <tt>class.</tt>
	 * @param userView <tt>UserView</tt> listening for updates from this <tt>class.</tt>
	 */
	public ReportsViewController(ReportsTreeView reportsTreeView, ReportView reportView) {
		this.reportView = reportView;
		this.reportsTreeView = reportsTreeView;
	}

	/**
	 * Loads reports from the database.
	 * 
	 * @param reload set to false if you want to use the cached reports, if any, without
	 *        having to reload them from the database.
	 */
	public void loadReports(boolean reload){
		if(reportGroups != null && !reload)
			return;
		
		FormUtil.dlg.setText(OpenXdataText.get(TextConstants.LOADING_REPORTS));
		FormUtil.dlg.center();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				try{
					Context.getReportService().getReports(new OpenXDataAsyncCallback<List<ReportGroup>>() {
						@Override
						public void onOtherFailure(Throwable caught) {
							FormUtil.dlg.hide();
							AsyncCallBackUtil.handleGenericOpenXDataException(caught);
						}

						@Override
						public void onSuccess(List<ReportGroup> result) {
							reportGroups = result;
							
							setChanged();
						    notifyObservers(reportGroups, ReportGroup.class);
						    
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
	 * Loads mapped reports from the database.
	 * The idea of loading all at once is to avoid multiple database 
	 * calls that might be expensive
	 * 
	 * @param reload set to false if you want to use the cached reports, if any, without
	 *        having to reload them from the database.
	 */
	public void loadMappedReports(boolean reload){
		if(userMappedReports != null && !reload)
			return;
		
		FormUtil.dlg.setText(OpenXdataText.get(TextConstants.LOADING_MAPPED_REPORTS));
		FormUtil.dlg.center();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				try{
					Context.getReportService().getUserMappedReports(new OpenXDataAsyncCallback<List<UserReportMap>>() {
						@Override
						public void onOtherFailure(Throwable caught) {
							FormUtil.dlg.hide();
							AsyncCallBackUtil.handleGenericOpenXDataException(caught);
						}

						@Override
						public void onSuccess(List<UserReportMap> mappedReports) {
							userMappedReports = mappedReports;
							
							setChanged();
						    notifyObservers(userMappedReports, UserReportMap.class);
							
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
	 * Saves new and modified reports to the database.
	 */
	public void saveReports(){
		if(reportGroups == null)
			return;

		reportView.commitChanges(true);

		List<Editable> deletedList = new ArrayList<Editable>();
		List<Report> deletedReports = reportsTreeView.getDeletedReports();
		List<ReportGroup> deletedReportGroups = reportsTreeView.getDeletedReportGroups();

		int count = (deletedReports != null ? deletedReports.size() : 0);
		count += (MainViewControllerUtil.getDirtyCount(reportGroups) + 
				(deletedReportGroups != null ? deletedReportGroups.size() : 0));

		for(Report rpt : deletedReports)
			deletedList.add(rpt);

		for(ReportGroup grp : deletedReportGroups)
			deletedList.add(grp);
		
		SaveAsyncCallback callback = new SaveAsyncCallback(count,
				OpenXdataText.get(TextConstants.REPORTS_SAVED_SUCCESSFULLY),OpenXdataText.get(TextConstants.PROBLEM_SAVING_REPORTS),reportGroups,deletedList,this);

		//Save new and modified reports and groups.
		for(int i=0; i<reportGroups.size(); i++){
			ReportGroup reportGroup = reportGroups.get(i);
			if(!reportGroup.isDirty())
				continue;
			else{
				callback.setCurrentItem(reportGroup);
				MainViewControllerUtil.setEditableProperties(reportGroup);
			}

			Context.getReportService().saveReportGroup(reportGroup, callback);
		}

		deleteObjects(deletedReports, deletedReportGroups, callback);
	}

	/**
	 * @param deletedReports
	 * @param deletedReportGroups
	 * @param callback
	 */
	private void deleteObjects(List<Report> deletedReports, List<ReportGroup> deletedReportGroups, SaveAsyncCallback callback) {
		//Save deleted report groups.
		if(deletedReportGroups != null){
			for(ReportGroup group : deletedReportGroups) {
				callback.setCurrentItem(group);
				Context.getReportService().deleteReportGroup(group, callback);
			}

			deletedReportGroups.clear();
		}

		//Save deleted reports.
		if(deletedReports != null){
			for(Report rpt : deletedReports) {
				callback.setCurrentItem(rpt);
				Context.getReportService().deleteReport(rpt, callback);
			}
			deletedReports.clear();
		}
	}
	
	/**
	 * Save new and modified mapped reports back to the 
	 * database
	 */
	public void saveMappedReports() {
		if(userMappedReports == null)
			return;
		
		List<UserReportMap> deletedMappedReports = (List<UserReportMap>) reportView.getDeletedUserMappedReportGroups();
		SaveAsyncCallback callback = new SaveAsyncCallback(
				MainViewControllerUtil.getDirtyCount(userMappedReports) + 
				(deletedMappedReports != null ? deletedMappedReports.size() : 0),
				OpenXdataText.get(TextConstants.MAPPED_REPORTS_SAVED_SUCCESSFULLY),OpenXdataText.get(TextConstants.PROBLEM_SAVING_MAPPED_REPORTS),reportGroups,
				deletedMappedReports,this);

		for(int i=0; i < userMappedReports.size(); i++){
			
			UserReportMap map = userMappedReports.get(i);
			if(!map.isDirty())
				continue;
			else{
				callback.setCurrentItem(map);
				MainViewControllerUtil.setEditableProperties(map);
			}

			Context.getReportService().saveUserMappedReport(map, callback);
		}
		
		if(deletedMappedReports != null){
			for(UserReportMap map : deletedMappedReports){
				callback.setCurrentItem(map);
				Context.getReportService().deleteUserMappedReport(map, callback);
			}
		}
		
	}

    @Override
	public void onSaveComplete(List<? extends Editable> modifiedList, List<? extends Editable> deletedList) {
    	MainViewControllerUtil.onSaveComplete(modifiedList, deletedList);
    	loadReports(true);
        loadMappedReports(true);
        loadMappedReportGroups(true);
	}

	/**
	 * Loads mapped reports from the database.
	 * The idea of loading all at once is to avoid multiple database  calls that might be expensive
	 * 
	 * @param reload set to false if you want to use the cached reports, if any, without  having to reload them from the database.
	 */
	public void loadMappedReportGroups(boolean reload){
		if(userMappedReportGroups != null && !reload)
			return;
		
		FormUtil.dlg.setText(OpenXdataText.get(TextConstants.LOADING_MAPPED_REPORTS));
		FormUtil.dlg.center();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				try{
					Context.getReportService().getUserMappedReportGroups(new OpenXDataAsyncCallback<List<UserReportGroupMap>>() {
						@Override
						public void onOtherFailure(Throwable caught) {
							FormUtil.dlg.hide();
							AsyncCallBackUtil.handleGenericOpenXDataException(caught);
						}

						@Override
						public void onSuccess(List<UserReportGroupMap> mappedReportGroups) {
							userMappedReportGroups = mappedReportGroups;
							
							setChanged();
						    notifyObservers(userMappedReportGroups, UserReportGroupMap.class);
							
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

}
