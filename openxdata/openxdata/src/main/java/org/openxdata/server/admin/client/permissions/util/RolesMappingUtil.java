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
									if (role2.getRoleId() == role.getRoleId()) {
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
