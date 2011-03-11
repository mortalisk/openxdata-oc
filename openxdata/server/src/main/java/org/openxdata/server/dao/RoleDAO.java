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
package org.openxdata.server.dao;

import java.util.List;

import org.openxdata.server.admin.model.Role;

/**
 * Provides data access 
 * services to the <code>Permission service</code>.
 * 
 * @author Angel
 *
 */
public interface RoleDAO extends BaseDAO<Role> {
	
	/**
	 * Gets a list of roles from the database.
	 * 
	 * @return the role list.
	 */
	List<Role> getRoles();
	
	/**
	 * Saves a role to the database.
	 * 
	 * @param role the role to save.
	 */
	void saveRole(Role role);
	
	/**
	 * Deletes a role from the database.
	 * 
	 * @param role the role to delete.
	 */
	void deleteRole(Role role);
	
	/**
	 * Get roles with the specified name
	 */
	List<Role> getRolesByName(String name);
}
