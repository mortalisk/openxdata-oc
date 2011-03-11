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
package org.openxdata.server.dao.hibernate;

import java.util.List;

import org.openxdata.server.admin.model.Role;
import org.openxdata.server.dao.RoleDAO;
import org.springframework.stereotype.Repository;

/**
 * Provides a hibernate implementation
 * of the <code>PermissionDAO</code> data access <code> interface.</code>
 * 
 * @author Angel
 *
 */
@Repository("roleDAO")
public class HibernateRoleDAO extends BaseDAOImpl<Role> implements RoleDAO {
	
	@Override
	public void deleteRole(Role role) {
		if(role.isDefaultAdminRole())
			return;
		else
			remove(role);
	}

	@Override
	public List<Role> getRoles() {
		return findAll();
	}

	@Override
	public List<Role> getRolesByName(String name) {
        return searchByPropertyEqual("name", name);
	}

	@Override
	public void saveRole(Role role) {
		save(role);
	}
}
