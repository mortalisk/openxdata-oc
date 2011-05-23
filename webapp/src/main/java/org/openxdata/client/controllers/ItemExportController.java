package org.openxdata.client.controllers;

import org.openxdata.client.AppMessages;
import org.openxdata.client.util.DesignerUtilities;
import org.openxdata.server.admin.client.listeners.GetFileNameDialogEventListener;
import org.openxdata.server.admin.client.view.widget.GetFileNameDialog;
import org.openxdata.server.admin.model.Exportable;

import com.google.gwt.core.client.GWT;

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
	}

	@Override
	public void onSetFileName(String fileName) {
		if (fileName != null && fileName.trim().length() > 0)
			DesignerUtilities.exportEditable(exportable, fileName);

	}
}
