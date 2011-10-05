package org.openxdata.server.service.impl;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openxdata.server.admin.model.Role;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;
import org.openxdata.server.service.RoleService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests methods in the PermissionService which deal with roles.
 * 
 * @author daniel
 * 
 */
public class RoleServiceTest extends BaseContextSensitiveTest {
	
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
	
	@Test
	public void getMappedRoles() throws Exception {
		PagingLoadConfig config = new PagingLoadConfig(0,10);
		PagingLoadResult<Role> mappedRoles = roleService.getMappedRoles(1, config);
		List<Role> data = mappedRoles.getData();
		Assert.assertEquals(1, data.size());
		Assert.assertEquals("Role_Administrator", data.get(0).getName());
	}
	
	@Test
	public void getUnMappedRoles() throws Exception {
		PagingLoadConfig config = new PagingLoadConfig(0,10);
		PagingLoadResult<Role> mappedRoles = roleService.getUnMappedRoles(1, config);
		List<Role> data = mappedRoles.getData();
		Assert.assertEquals(1, data.size());
		Assert.assertEquals("Role_DataCapturer", data.get(0).getName());
	}
}
