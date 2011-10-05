package org.openxdata.server.admin.client.controller;

import org.openxdata.server.admin.client.controller.facade.MainViewControllerFacade;
import org.openxdata.server.admin.client.view.DatasetView;
import org.openxdata.server.admin.client.view.treeview.DatasetTreeView;

import com.google.inject.Inject;

/**
 * This controller deals with loading and saving of data, to and from the
 * database, for the various views.
 * 
 * @author daniel
 * 
 */
public class MainViewController {
	
	/**
	 * An instance of
	 * {@link org.openxdata.server.admin.client.controller.DatasetViewController}
	 * 
	 * This will handle all movement of data for the views in relation to
	 * loading, saving and movement of Reports related data.
	 */
	private DatasetViewController datasetViewController;
	
	/**
	 * Initializes the MainViewController with all the ancillary controllers for
	 * the views.
	 * 
	 * @param settingsTreeView
	 *            Reports tree view that will be loaded on the UI
	 * @param reportsTreeView
	 *            Reports view that will be loaded on the UI
	 * @param reportView
	 *            View to display Report Properties
         */
        @Inject
        public MainViewController(DatasetTreeView reportsTreeView,
                DatasetView reportView) {
                datasetViewController = new DatasetViewController(reportsTreeView,
		        reportView);
                MainViewControllerFacade.setMVCInstance(this);

        }
	
	/**
	 * Returns an instance of the Reports Controller for the application. Should
	 * never be set manually but left to the Main View Controller to build one
	 * in order to have a single one for the whole application
	 * 
	 * @return The ReportsViewController
	 */
	public DatasetViewController getReportsViewController() {
		return datasetViewController;
	}
	
}
