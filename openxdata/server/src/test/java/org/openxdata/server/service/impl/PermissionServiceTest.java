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
package org.openxdata.server.service.impl;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.service.RoleService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Tests methods in the PermissionService which deal with permissions.
 * 
 * @author daniel
 *
 */
public class PermissionServiceTest extends BaseContextSensitiveTest {
	
	@Autowired
	protected RoleService permissionService;	
	
	@Test
	public void getPermissions_shouldReturnAllPermissions() throws Exception {
		
		List<Permission> permissions = permissionService.getPermissions();
		
		Assert.assertNotNull(permissions);
		Assert.assertEquals(71, permissions.size());
	}
	
	@Test
	public void savePermission_shouldSavePermission() throws Exception {
		final String permissionName = "Permission Name";
		
		List<Permission> permissions = permissionService.getPermissions();
		Assert.assertEquals(71,permissions.size());
		Assert.assertNull(permissionService.getPermission(permissionName));
		
		permissionService.savePermission(new Permission(permissionName));
		
		permissions = permissionService.getPermissions();
		Assert.assertEquals(72,permissionService.getPermissions().size());
		Assert.assertNotNull(permissionService.getPermission(permissionName));
	}
	
	@Test
	public void deletePermission_shouldDeleteGivenPermission() throws Exception {
		
		final String permissionName = "Permission Name";
		
		List<Permission> permissions = permissionService.getPermissions();
		Assert.assertEquals(71,permissions.size());
		Assert.assertNull(permissionService.getPermission(permissionName));
	
		permissionService.savePermission(new Permission(permissionName));
		permissions = permissionService.getPermissions();
		Assert.assertEquals(72,permissions.size());
		
		Permission permission = permissionService.getPermission(permissionName);
		Assert.assertNotNull(permission);

		permissionService.deletePermission(permission);
		
		permissions = permissionService.getPermissions();
		Assert.assertEquals(71,permissions.size());
		Assert.assertNull(permissionService.getPermission(permissionName));
	}
}
