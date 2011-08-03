package org.openxdata.server.admin.client.view.treeview;

import org.openxdata.server.admin.client.presenter.tree.UsersListPresenter;
import org.openxdata.server.admin.client.view.listeners.OpenXDataExportImportApplicationEventListener;
import org.openxdata.server.admin.model.User;
import org.purc.purcforms.client.controller.OpenFileDialogEventListener;
import org.purc.purcforms.client.view.OpenFileDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;

/**
 *
 * @author kay
 */
public class UsersTreeDisplay extends BaseTreeDisplay<User> implements
		UsersListPresenter.Display, OpenFileDialogEventListener,
		OpenXDataExportImportApplicationEventListener {

    @Override
    public String getDisplayLabel(User user) {
        return user.getName();
    }

    @Override
    protected String getTooltip(User user) {
        return user.getFullName();
    }

    @Override
    protected String getTreeName() {
        return displayName;

    }

    @Override
    protected ImageResource getImage(User item) {
        return images.user();
    }
    
    @Override
    public void onImport() {
    	OpenFileDialog dlg = new OpenFileDialog(this,"import?type=user");
		dlg.center();
    }
    
    @Override
    public void onExport() {
    	// do nothing
    }
    
    @Override
    public void exportAsPdf() {
    	// do nothing
    }
    
    @Override
    public void onSetFileContents(String contents) {
    	if (contents != null && !contents.isEmpty()){
    		if (contents.startsWith("Error")){
    			Window.alert(contents);
    		} else {
	    		String url = GWT.getHostPageBaseURL() + "import?filename=" + contents;
	    		Window.open(URL.encode(url), "_blank", "");
    		}
    	}
    }
    
    @Override
    public void onOpen() {
    	// do nothing
    }
}
