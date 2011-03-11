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

import com.google.inject.Inject;
import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.controller.facade.MainViewControllerFacade;
import org.openxdata.server.admin.client.controller.observe.OpenXDataObserver;
import org.openxdata.server.admin.client.view.ReportView;
import org.openxdata.server.admin.client.view.StudyView;
import org.openxdata.server.admin.client.view.treeview.ReportsTreeView;
import org.openxdata.server.admin.client.view.treeview.SettingsTreeView;
import org.openxdata.server.admin.client.view.treeview.StudiesTreeView;

/**
 * This controller deals with loading and saving of data, to and from the
 * database, for the various views.
 * 
 * @author daniel
 * @author Mark
 * 
 */
public class MainViewController {
	

	/**
	 * An instance of
	 * {@link org.openxdata.server.admin.client.controller.StudiesViewController}
	 * 
	 * This will handle all movement of data for the views in relation to
	 * loading, saving and movement of Studies related data.
	 */
	private StudiesViewController studiesViewController;
	
	/**
	 * An instance of
	 * {@link org.openxdata.server.admin.client.controller.ReportsViewController}
	 * 
	 * This will handle all movement of data for the views in relation to
	 * loading, saving and movement of Reports related data.
	 */
	private ReportsViewController reportsViewController;
	
	/**
	 * An instance of
	 * {@link org.openxdata.server.admin.client.controller.SettingsViewController}
	 * 
	 * This will handle all data movement for the views in relation to loading,
	 * saving and movement of Settings related data.
	 */
	private SettingsViewController settingsViewController;
	
	/**
	 * Initializes the MainViewController with all the ancillary controllers for
	 * the views.
	 * 
	 * @param studiesTreeView
	 *            Studies tree view that will be loaded on the UI
	 * @param settingsTreeView
	 *            Reports tree view that will be loaded on the UI
	 * @param reportsTreeView
	 *            Reports view that will be loaded on the UI
	 * @param studyView
	 *            Roles tree view that will be loaded on the UI
	 * @param reportView
	 *            View to display Report Properties
         */
        @Inject
        public MainViewController(StudiesTreeView studiesTreeView,
                SettingsTreeView settingsTreeView,
                ReportsTreeView reportsTreeView,
                StudyView studyView,
                ReportView reportView) {

                setupSettingsViewController(settingsTreeView);
                
                setupReportsViewController(reportsTreeView, reportView);
                setupStudiesViewController(studiesTreeView, studyView, reportView);
                MainViewControllerFacade.setMVCInstance(this);

        }
	
	/**
	 * Sets up the {@link SettingsViewController}
	 * 
	 * @param settingsTreeView
	 *            instance of {@link SettingsTreeView} that was created by the
	 *            factory.
	 */
        private void setupSettingsViewController(SettingsTreeView settingsTreeView) {
                settingsViewController = new SettingsViewController(settingsTreeView);

                // Registering Observers
                settingsViewController.addObserver(Context.getContextInstance());
                settingsViewController.addObserver(settingsTreeView);

        }
	

	
	/**
	 * Sets up the {@link ReportsViewController}
	 * 
	 * @param reportsTreeView
	 *            instance of {@link ReportsTreeView} that was created by the
	 *            factory.
	 * @param reportView
	 *            instance of {@link ReportView} that was created by the
	 *            factory.
	 */
	private void setupReportsViewController(
	        ReportsTreeView reportsTreeView,
	        ReportView reportView) {
		
		reportsViewController = new ReportsViewController(reportsTreeView,
		        reportView);
		
		// Registering Observers
		reportsViewController.addObserver(Context.getContextInstance());
		reportsViewController.addObserver((OpenXDataObserver) reportView);
		reportsViewController.addObserver(reportsTreeView);
		
	}
	
	/**
	 * Sets up the {@link StudiesViewController}
	 * 
	 * @param studiesTreeView
	 *            instance of {@link StudiesTreeView} that was created by the
	 *            factory.
	 * @param studyView
	 *            instance of {@link StudyView} that was created by the factory.
	 * @param reportView
	 *            instance of {@link ReportView} that was created by the
	 *            factory.
	 */
	private void setupStudiesViewController(
	        StudiesTreeView studiesTreeView, StudyView studyView,
	        ReportView reportView) {
		
		studiesViewController = new StudiesViewController(studiesTreeView);
		
		// Registering observers
		studiesViewController.addObserver(Context.getContextInstance());
		studiesViewController.addObserver((OpenXDataObserver) studyView);
		studiesViewController.addObserver((OpenXDataObserver) reportView);
		studiesViewController.addObserver(studiesTreeView);
		
	}
	

	
	/**
	 * Returns an instance of the Studies Controller for the application. Should
	 * never be set manually but left to the Main View Controller to build one
	 * in order to have a single one for the whole application
	 * 
	 * @return The StudiesViewController
	 */
	public StudiesViewController getStudiesViewController() {
		return studiesViewController;
	}
	
	/**
	 * Returns an instance of the Reports Controller for the application. Should
	 * never be set manually but left to the Main View Controller to build one
	 * in order to have a single one for the whole application
	 * 
	 * @return The ReportsViewController
	 */
	public ReportsViewController getReportsViewController() {
		return reportsViewController;
	}
	
	/**
	 * Returns an instance of the Settings Controller for the application.
	 * Should never be set manually but left to the Main View Controller to
	 * build one in order to have a single one for the whole application
	 * 
	 * @return The SettingsViewController
	 */
	public SettingsViewController getSettingsViewController() {
		return settingsViewController;
	}
}
