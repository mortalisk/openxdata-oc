package org.openxdata.client.controllers;

import org.openxdata.client.AppMessages;
import org.openxdata.client.util.DesignerUtilities;
import org.openxdata.server.admin.client.listeners.GetFileNameDialogEventListener;
import org.openxdata.server.admin.client.view.widget.GetFileNameDialog;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.StudyDef;

import com.google.gwt.core.client.GWT;

/**
 * Controller for views that export Items.
 * 
 */
public class ItemExportController implements
		GetFileNameDialogEventListener {

	private Editable editable;
	AppMessages appMessages = GWT.create(AppMessages.class);

	GetFileNameDialog getFileNameDialog;

	public ItemExportController() {
		super();

	}

	public void loadExportDialog(Editable editable) {
		this.editable = editable;
		String defaultName = getName(editable);
		defaultName.replace(" ", "");
		getFileNameDialog = new GetFileNameDialog(this,
				appMessages.exportAs(), appMessages.exportA(), defaultName);
		getFileNameDialog.center();
	}

	private String getName(Editable editable) {
		if(editable instanceof StudyDef)
			return ((StudyDef)editable).getName();
		if(editable instanceof FormDef)
			return ((FormDef)editable).getName();
		if(editable instanceof FormDefVersion)
			return ((FormDefVersion)editable).getName();
		
		return "";
	}

	@Override
	public void onSetFileName(String fileName) {
		if (fileName != null && fileName.trim().length() > 0)
			DesignerUtilities.exportEditable(editable, fileName);

	}
}
