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

import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.Role;
import org.openxdata.server.dao.PermissionDAO;
import org.openxdata.server.dao.RoleDAO;
import org.openxdata.server.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation for <code>Permission Service</code>.
 * 
 * @author Angel
 *
 */
@Service("roleService")
@Transactional
public class RoleServiceImpl implements RoleService {

	@Autowired
    private RoleDAO roleDAO;
	
	@Autowired
	private PermissionDAO permissionDAO;

    public void setRoleDAO(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }

    @Override
    @Secured("Perm_Delete_Roles")
	public void deleteRole(Role role) {
        roleDAO.deleteRole(role);
    }

    @Override
	@Transactional(readOnly=true)
	@Secured("Perm_View_Roles")
    public List<Role> getRoles() {
        return roleDAO.getRoles();
    }

    @Override
    @Secured("Perm_View_Roles")
	public List<Role> getRolesByName(String name) {
        return roleDAO.getRolesByName(name);
    }

    @Override
    @Secured("Perm_Add_Roles")
	public void saveRole(Role role) {
        roleDAO.saveRole(role);
    }

    @Override
	@Transactional(readOnly=true)
	// note: no security because it is used during authentication
    public List<Permission> getPermissions() {
        return permissionDAO.getPermissions();
    }

    @Override
    @Secured("Perm_Delete_Permissions")
	public void deletePermission(Permission permission) {
    	permissionDAO.deletePermission(permission);
    }

    @Override
    @Secured("Perm_Add_Permissions")
	public void savePermission(Permission permission) {
    	permissionDAO.savePermission(permission);
    }
    
    @Override
	@Transactional(readOnly=true)
    public Permission getPermission(String permissionName) {
    	return permissionDAO.getPermission(permissionName);
    }
}
