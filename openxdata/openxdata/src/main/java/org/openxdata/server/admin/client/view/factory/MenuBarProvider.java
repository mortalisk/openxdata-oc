package org.openxdata.server.admin.client.view.factory;

import com.google.inject.Provider;
import org.openxdata.server.admin.client.permissions.util.RolesListUtil;
import org.openxdata.server.admin.client.view.bar.OpenXDataMenuBar;

/**
 * 
 * @author kay
 */
public class MenuBarProvider implements Provider<OpenXDataMenuBar> {

	@Override
	public OpenXDataMenuBar get() {
		OpenXDataMenuBar openxdataMenuBar = new OpenXDataMenuBar();

		if (RolesListUtil.getInstance().isAdmin()) {
			openxdataMenuBar.constructMenuBarInstanceOfAdministratorUser();
		} else {
			openxdataMenuBar.constructMenuBarInstanceOfUserWithPermissions();
		}
		return openxdataMenuBar;
	}
}
