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
import org.openxdata.server.admin.model.Role;
import org.openxdata.server.service.RoleService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Tests methods in the PermissionService which deal with roles.
 * 
 * @author daniel
 *
 */
public class RoleServiceTest extends BaseContextSensitiveTest{
	
	@Autowired
	protected RoleService roleService;
	
	@Test
	public void getRoles_shouldReturnAllRoles() throws Exception {
		
		List<Role> roles = roleService.getRoles();
		
		Assert.assertNotNull(roles);
		Assert.assertEquals(2, roles.size());
		Assert.assertEquals("Role_Administrator", roles.get(0).getName());
	}
	
	@Test
	public void saveRole_shouldSaveRole() throws Exception {
		Assert.assertEquals(2,roleService.getRoles().size());
		
		roleService.saveRole(new Role("Role Name"));
		
		Assert.assertEquals(3,roleService.getRoles().size());
	}
	
	@Test
	public void deleteRole_shouldNotDeleteAdminRole() throws Exception {
		List<Role> roles = roleService.getRoles();
		Assert.assertEquals(2,roles.size());
		
		Role role = roles.get(0);
		roleService.deleteRole(role);
		
		Assert.assertEquals(2,roleService.getRoles().size());
	}
	
	@Test
	public void deleteRole_shouldDeleteGivenRole() throws Exception {
		
		List<Role> roles = roleService.getRoles();
		Assert.assertEquals(2,roles.size());
		
		final String roleName = "Role Name";
		roleService.saveRole(new Role(roleName));
		roles = roleService.getRoles();
		Assert.assertEquals(3,roles.size());
		
		Role role = roles.get(0);
		if(!role.getName().equals(roleName))
			role = roles.get(1);
		
		roleService.deleteRole(role);
		
		roles = roleService.getRoles();
		Assert.assertEquals(2,roles.size());
		Assert.assertNotSame(roleName,roles.get(0).getName());
	}
}
