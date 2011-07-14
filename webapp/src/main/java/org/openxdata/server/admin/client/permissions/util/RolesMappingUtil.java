package org.openxdata.server.admin.client.permissions.util;

import java.util.List;
import java.util.Set;

import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.controller.callback.OpenXDataAsyncCallback;
import org.openxdata.server.admin.client.listeners.RoleMappedListener;
import org.openxdata.server.admin.model.Role;
import org.openxdata.server.admin.model.User;
import org.purc.purcforms.client.util.FormUtil;

import com.google.gwt.user.client.Window;

/**
 * Utility class to check if a role is mapped to any user
 * On completion the <code>RoleMappedListener<code> is sent a notification
 * @author kay
 *
 */
public class RolesMappingUtil {

	RoleMappedListener roleMapListener;

	public RolesMappingUtil(RoleMappedListener listener) {		
		roleMapListener = listener;
		
	}

	/**
	 * Checks if a roles is mapped to any user.And notifies the caller through the
	 * RoleMappedListener listener through the <code>RoleMappedListener</code>
	 * interface
	 * @param role
	 */
	public void checkRoleMapping(final Role role) {
		// Reload the users from the DB
		FormUtil.dlg.setText("Checking User-Role Conflicts...");
		FormUtil.dlg.center();
				
		Context.getUserService().getUsers(
				
				new OpenXDataAsyncCallback<List<User>>() {
					
					@Override
					public void onOtherFailure(Throwable caught) {
					
						FormUtil.dlg.hide();
						Window.alert(caught.getMessage());
						
					}

					@Override
					public void onSuccess(List<User> users) {
						FormUtil.dlg.hide();
						if (users != null) {
					
							for (User user : users) {
								
								Set<Role> roles = user.getRoles();
								for (Role role2 : roles) {
									if (role2.getId() == role.getId()) {
										roleMapListener.isMappedToUser(true);
										return;
									}
								}
							}
							roleMapListener.isMappedToUser(false);
						}
					}});
	}
}
