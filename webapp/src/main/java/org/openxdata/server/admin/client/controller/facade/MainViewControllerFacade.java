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
package org.openxdata.server.admin.client.controller.facade;

import org.openxdata.server.admin.client.controller.MainViewController;
import org.openxdata.server.admin.client.permissions.util.RolesListUtil;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.constants.OpenXDataStackPanelConstants;
import org.openxdata.server.admin.client.view.event.ViewEvent;
import org.openxdata.server.admin.client.view.factory.OpenXDataWidgetFactory;
import org.openxdata.server.admin.model.Role;
import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.admin.model.User;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import org.openxdata.server.admin.model.SettingGroup;

/**
 * This class encapsulates direct calls to the specific
 * <code>MainViewController</code> from the caller and forwards those calls to
 * the correct <code>View Controller</code> to effect an operation.
 * 
 * @author Angel on Dec 17, 2009
 * 
 */
public class MainViewControllerFacade  {
	
	/** Handle to <tt>Widget Factory.</tt> */
	protected static OpenXDataWidgetFactory widgetFactory;
	
	/**
	 * MainViewController that will be used to forward calls.
	 */
	private static MainViewController mainViewControllerInstance;
	
	/**
	 * Loads <code>Studies</code>.
	 * 
	 * @param reload
	 *            parameter to indicate if <code>database</code> call should be
	 *            made in subsequent calls of this method.
	 */
	public static void loadStudies(boolean reload) {
		getMVCInstance().getStudiesViewController().loadStudies(reload);
	}
	
	/**
	 * @return <code>this</code> instance of <code>MainViewController</code>
	 */
	private static MainViewController getMVCInstance() {
		return mainViewControllerInstance;
	}
	

	/**
	 * Loads <code>Reports</code>.
	 * 
	 * @param reload
	 *            parameter to indicate if <code>database</code> call should be
	 *            made in subsequent calls of this method.
	 */
	public static void loadReports(boolean reload) {
		getMVCInstance().getReportsViewController().loadReports(reload);
	}
	
	/**
	 * Saves new, modified or dirty <code>Studies</code>.
	 */
	public static void saveStudies() {
		getMVCInstance().getStudiesViewController().saveStudies();
	}
	
	/**
	 * Saves new, modified or dirty <code>Reports</code>.
	 */
	public static void saveReports() {
		getMVCInstance().getReportsViewController().saveReports();
	}
	
	/**
	 * Loads all the persisted <code>UserStudyMap objects.</code>
	 * 
	 * @param reload
	 *            parameter to determine if a database reload is needed.
	 */
	public static void loadAllUserMappedStudies(boolean reload) {
		getMVCInstance().getStudiesViewController().loadMappedStudies(reload);
	}
	
	/**
	 * Loads all the persisted <code>UserReportGroupMap objects.</code>
	 * 
	 * @param reload
	 *            parameter to determine if a database reload is needed.
	 */
	public static void loadAllUserMappedReportGroups(boolean reload) {
		getMVCInstance().getReportsViewController().loadMappedReportGroups(
		        reload);
	}

	
	
	/**
	 * Saves <code>User Mapped Reports.</code>
	 */
	public static void saveMappedReports() {
		getMVCInstance().getReportsViewController().saveMappedReports();
	}
	
	/**
	 * Loads all the persisted <code>UserFormMap objects.</code>
	 * 
	 * @param reload
	 *            parameter to determine if a database reload is needed.
	 */
	public static void loadAllUserMappedForms(boolean reload) {
		getMVCInstance().getStudiesViewController().loadMappedForms(reload);
	}
	
	/**
	 * Loads all the persisted <code>UserReportMap objects.</code>
	 * 
	 * @param reload
	 *            parameter to determine if a database reload is needed.
	 */
	public static void loadAllUserMappedReports(boolean reload) {
		getMVCInstance().getReportsViewController().loadMappedReports(reload);
	}

	
	/**
	 * Loads preliminary data for the all the views.
	 */
	public static void loadPreliminaryViewData() {
		
		Utilities.displayNotificationMessage("Loading Data...");
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				
				// The outline below should be changed with caution.
				// Observables do not provide a guarantee on when they will
				// notify observers
				// so we need some objects at the earliest time possible like
				// Permissions, Roles and Users.
				if (RolesListUtil.getPermissionResolver().isViewPermission(
				        "Perm_View_Studies"))
					MainViewControllerFacade.loadStudies(false);
				
				if (RolesListUtil.getPermissionResolver().isViewPermission(
				        "Perm_View_Reports"))
					MainViewControllerFacade.loadReports(false);
				if (RolesListUtil.getPermissionResolver().isViewPermission(
				        "Perm_View_Forms"))
					MainViewControllerFacade.loadAllUserMappedForms(false);
				
				if (RolesListUtil.getPermissionResolver().isViewPermission(
				        "Perm_View_ReportGroups"))
					MainViewControllerFacade
					        .loadAllUserMappedReportGroups(false);
				
				if (RolesListUtil.getPermissionResolver().isViewPermission(
				        "Perm_View_Reports"))
					MainViewControllerFacade.loadAllUserMappedReports(false);
				
				widgetFactory.getOpenXDataToolBar().refresh();
				
				(widgetFactory.getNotificationLabel())
				        .setDefaultText();
			}
		});
	}
	
	/**
	 * Saves all the dirty <tt>editables</tt> in the system.
	 * 
	 * @throws Exception
	 *             if any <tt>exception</tt> occurs.
	 */
	public static void saveData() throws Exception {
		
		Utilities.displayNotificationMessage("Saving Data...");
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				switch (widgetFactory.getOpenXdataStackPanel()
				        .getSelectedIndex()) {
					case OpenXDataStackPanelConstants.INDEX_REPORTS:
						saveReports();
						saveMappedReports();
						break;
				}
				(widgetFactory.getNotificationLabel())
				        .setDefaultText();
				
			}
		});
		
		Window.alert("Data Successfully Saved.");
	}
	
	/**
	 * Refreshes the data in the system by making service layer calls to pull
	 * new data.
	 */
	public static void refreshData() {
		
		Utilities.displayNotificationMessage("Refreshing Data...");
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				switch (widgetFactory.getOpenXdataStackPanel()
				        .getSelectedIndex()) {
					case OpenXDataStackPanelConstants.INDEX_STUDIES:
						if ((widgetFactory.getStudyView())
						        .isInFormDesignMode())
							(widgetFactory.getStudyView())
							        .refreshItem();
						else {
							if (RolesListUtil.getPermissionResolver()
							        .isViewPermission("Perm_View_Studies")) {
								loadStudies(true);
								loadAllUserMappedStudies(true);
							}
						}
						break;

					case OpenXDataStackPanelConstants.INDEX_REPORTS:
						if ((widgetFactory.getReportView())
						        .isInReportDataMode())
							(widgetFactory.getReportView())
							        .refresh();
						else {
							if (RolesListUtil.getPermissionResolver()
							        .isViewPermission("Perm_View_Reports")) {
								loadReports(true);
								loadAllUserMappedReports(true);
							}
						}
						break;
				}
				(widgetFactory.getNotificationLabel())
				        .setDefaultText();
				
			}
		});
	}

        public static void setMVCInstance(MainViewController aThis) {
              mainViewControllerInstance=aThis;
        }
    private final EventBus eventBus;
    
	

	public void onSelectedIndexChanged(final int newIndex) {
		
		Utilities.displayNotificationMessage("Loading Data... ");
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				switch (newIndex) {
					case OpenXDataStackPanelConstants.INDEX_STUDIES:
						if (RolesListUtil.getPermissionResolver()
						        .isViewPermission("Perm_View_Studies")) {
							MainViewControllerFacade
							        .loadAllUserMappedStudies(false);
							MainViewControllerFacade.loadStudies(false);
						}
						widgetFactory.getHorizontalSplitPanel().setRightWidget(
						        widgetFactory.getStudyView());
						break;
					case OpenXDataStackPanelConstants.INDEX_USERS:
                                                eventBus.fireEvent(new ViewEvent<User>(User.class));
						break;
					case OpenXDataStackPanelConstants.INDEX_ROLES:
                                                eventBus.fireEvent(new ViewEvent<Role>(Role.class));
						break;
					case OpenXDataStackPanelConstants.INDEX_TASKS:
                                                eventBus.fireEvent(new ViewEvent<TaskDef>(TaskDef.class));
						break;
					case OpenXDataStackPanelConstants.INDEX_SETTINGS:
                                                 eventBus.fireEvent(new ViewEvent<SettingGroup>(SettingGroup.class));
						break;
					case OpenXDataStackPanelConstants.INDEX_REPORTS:
						if (RolesListUtil.getPermissionResolver()
						        .isViewPermission("Perm_View_Reports")) {
							MainViewControllerFacade
							        .loadAllUserMappedReports(false);
							MainViewControllerFacade.loadReports(false);
						}
						widgetFactory.getHorizontalSplitPanel().setRightWidget(
						        widgetFactory.getReportView());
                                            
						break;
				}
				
				(widgetFactory.getNotificationLabel())
				        .setDefaultText();
			}
		});
	}
	
	
	

	
	/**
	 * Private constructor to avoid outside initialization of this
	 * <tt>class.</tt>
	 */
    @Inject
    public MainViewControllerFacade(EventBus eventBus, OpenXDataWidgetFactory factory) {
        widgetFactory = factory;
        this.eventBus = eventBus;
        
    }

	
	/**
	 * Sets the <tt>Widget Factory.</tt>
	 * 
	 * @param openxdataWidgetFactory
	 *            <tt>Widget Factory to set.</tt>
	 */
	public static void setWidgetFactory(
	        OpenXDataWidgetFactory openxdataWidgetFactory) {
		MainViewControllerFacade.widgetFactory = openxdataWidgetFactory;
		
	}
}
