package org.openxdata.server.admin.client.view.factory;

import com.google.inject.Provider;
import org.openxdata.server.admin.client.permissions.util.RolesListUtil;
import org.openxdata.server.admin.client.view.widget.OpenXDataToolBar;

/**
 *
 * @author kay
 */
public class ToolBarProvider implements Provider<OpenXDataToolBar> {

        @Override
        public OpenXDataToolBar get() {
                OpenXDataToolBar openxdataToolBar = new OpenXDataToolBar();
                if (RolesListUtil.getInstance().isAdmin()) {
                        openxdataToolBar.instanceOfAdminUser();
                } else {
                        openxdataToolBar.instanceOfUserPermissions();
                }
		return openxdataToolBar;
	}
}
