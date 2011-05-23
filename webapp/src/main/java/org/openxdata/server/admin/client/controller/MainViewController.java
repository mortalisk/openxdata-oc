package org.openxdata.server.admin.client.controller;

import com.google.inject.Inject;
import org.openxdata.server.admin.client.controller.facade.MainViewControllerFacade;
import org.openxdata.server.admin.client.view.DatasetView;
import org.openxdata.server.admin.client.view.StudyView;
import org.openxdata.server.admin.client.view.treeview.DatasetTreeView;
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
                DatasetTreeView reportsTreeView,
                StudyView studyView,
                DatasetView reportView) {
                studiesViewController = new StudiesViewController(studiesTreeView);
                datasetViewController = new DatasetViewController(reportsTreeView,
		        reportView);
                MainViewControllerFacade.setMVCInstance(this);

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
	public DatasetViewController getReportsViewController() {
		return datasetViewController;
	}
	
}
