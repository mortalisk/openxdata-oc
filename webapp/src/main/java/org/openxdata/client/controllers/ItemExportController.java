package org.openxdata.client.controllers;

import org.openxdata.client.AppMessages;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.client.listeners.GetFileNameDialogEventListener;
import org.openxdata.server.admin.client.view.widget.GetFileNameDialog;
import org.openxdata.server.admin.model.Exportable;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;

/**
 * Controller for views that export Items.
 * 
 */
public class ItemExportController implements
		GetFileNameDialogEventListener {

	private Exportable exportable;
	AppMessages appMessages = GWT.create(AppMessages.class);

	GetFileNameDialog getFileNameDialog;

	public ItemExportController() {
		super();

	}

	public void loadExportDialog(Exportable exportable) {
		this.exportable = exportable;
		String defaultName = exportable.getName();
		
		defaultName.replace(" ", "");
		getFileNameDialog = new GetFileNameDialog(this,
				appMessages.exportAs(), appMessages.exportA(), defaultName);
		getFileNameDialog.center();
		
		ProgressIndicator.hideProgressBar();
	}

	@Override
	public void onSetFileName(String fileName) {
		if (fileName != null && fileName.trim().length() > 0)
			exportEditable(exportable, fileName);

	}
	
	/**
	 * Exports the given Editable. Right now we only export studies.
	 * 
	 * @param editable Editable to export.
	 * @param fileName File Name for the item to export.
	 */
	void exportEditable(final Exportable exportable, final String fileName){

		ProgressIndicator.showProgressBar();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				try{
					Integer id = exportable.getId();
					String type = exportable.getType();

					String url = "studyexport?";
					url += "type=" + type;
					url += "&id=" + id;
					url += "&filename=" + fileName;

					Window.Location.replace(URL.encode(url));

					ProgressIndicator.hideProgressBar();	
				}
				catch(Exception ex){
					MessageBox.alert(appMessages.error(), appMessages.exportError(), null);
					ProgressIndicator.hideProgressBar();	
				}	
			}
		});
	}
}
