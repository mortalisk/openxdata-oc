package org.openxdata.server.admin.client.controller;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.controller.callback.OpenXDataAsyncCallback;
import org.openxdata.server.admin.client.controller.callback.SaveAsyncCallback;
import org.openxdata.server.admin.client.listeners.SaveCompleteListener;
import org.openxdata.server.admin.client.internationalization.OpenXdataText;
import org.openxdata.server.admin.client.listeners.TextConstants;
import org.openxdata.server.admin.client.util.AsyncCallBackUtil;
import org.openxdata.server.admin.client.util.MainViewControllerUtil;
import org.openxdata.server.admin.client.view.DatasetView;
import org.openxdata.server.admin.client.view.treeview.DatasetTreeView;
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
 * Datasets related data to and from the database for the various dataset related views.
 * 
 * @author Angel
 *
 */
public class DatasetViewController  implements SaveCompleteListener {

	private DatasetView datasetView;
	private List<ReportGroup> datasetGroups;
	private DatasetTreeView datasetTreeView;
	
	private List<UserReportMap> userMappedDatasets;
	private List<UserReportGroupMap> userMappedDatasetGroups;
	
	/**
	 * Constructs an instance of this <tt>class</tt> given a <tt>View</tt> to update with <tt>Reports.</tt>
	 * 
	 * @param datasetTreeView <tt>Tree View</tt> that is observing this <tt>class.</tt>
	 * @param datasetView <tt>ReportView</tt> to receive updates from this <tt>class.</tt>
	 * @param userView <tt>UserView</tt> listening for updates from this <tt>class.</tt>
	 */
	public DatasetViewController(DatasetTreeView datasetTreeView, DatasetView datasetView) {
		this.datasetView = datasetView;
		this.datasetTreeView = datasetTreeView;
	}

	/**
	 * Loads datasets from the database.
	 * 
	 * @param reload set to false if you want to use the cached datasets, if any, without
	 *        having to reload them from the database.
	 */
	public void loadReports(boolean reload){
		if(datasetGroups != null && !reload)
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
							datasetGroups = result;
						    datasetTreeView.updateReportGroups(result);
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
	 * Loads mapped datasets from the database.
	 * The idea of loading all at once is to avoid multiple database 
	 * calls that might be expensive
	 * 
	 * @param reload set to false if you want to use the cached datasets, if any, without
	 *        having to reload them from the database.
	 */
	public void loadMappedReports(boolean reload){
		if(userMappedDatasets != null && !reload)
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
							userMappedDatasets = mappedReports;
							
							datasetTreeView.updateUserMappedReports(mappedReports);
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
	 * Saves new and modified datasets to the database.
	 */
	public void saveReports(){
		if(datasetGroups == null)
			return;

		datasetView.commitChanges(true);

		List<Editable> deletedList = new ArrayList<Editable>();
		List<Report> deletedReports = datasetTreeView.getDeletedReports();
		List<ReportGroup> deletedReportGroups = datasetTreeView.getDeletedReportGroups();

		int count = (deletedReports != null ? deletedReports.size() : 0);
		count += (MainViewControllerUtil.getDirtyCount(datasetGroups) + 
				(deletedReportGroups != null ? deletedReportGroups.size() : 0));

		for(Report rpt : deletedReports)
			deletedList.add(rpt);

		for(ReportGroup grp : deletedReportGroups)
			deletedList.add(grp);
		
		SaveAsyncCallback callback = new SaveAsyncCallback(count,
				OpenXdataText.get(TextConstants.REPORTS_SAVED_SUCCESSFULLY),OpenXdataText.get(TextConstants.PROBLEM_SAVING_REPORTS),datasetGroups,deletedList,this);

		//Save new and modified reports and groups.
		for(int i=0; i<datasetGroups.size(); i++){
			ReportGroup reportGroup = datasetGroups.get(i);
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
	 * Save new and modified mapped datasets back to the 
	 * database
	 */
	public void saveMappedReports() {
		if(userMappedDatasets == null)
			return;
		
		List<UserReportMap> deletedMappedReports = (List<UserReportMap>) datasetView.getDeletedUserMappedReportGroups();
		SaveAsyncCallback callback = new SaveAsyncCallback(
				MainViewControllerUtil.getDirtyCount(userMappedDatasets) + 
				(deletedMappedReports != null ? deletedMappedReports.size() : 0),
				OpenXdataText.get(TextConstants.MAPPED_REPORTS_SAVED_SUCCESSFULLY),OpenXdataText.get(TextConstants.PROBLEM_SAVING_MAPPED_REPORTS),datasetGroups,
				deletedMappedReports,this);

		for(int i=0; i < userMappedDatasets.size(); i++){
			
			UserReportMap map = userMappedDatasets.get(i);
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
	 * Loads mapped datasets from the database.
	 * The idea of loading all at once is to avoid multiple database  calls that might be expensive
	 * 
	 * @param reload set to false if you want to use the cached datasets, if any, without  having to reload them from the database.
	 */
	public void loadMappedReportGroups(boolean reload){
		if(userMappedDatasetGroups != null && !reload)
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
							userMappedDatasetGroups = mappedReportGroups;
							
							datasetTreeView.updateUserMappedReportGroups(mappedReportGroups);
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
