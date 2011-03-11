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

import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserFormMap;

/**
 * @author Angel
 *
 */
public interface UserFormMapDAO extends BaseDAO<UserFormMap> {
	
	/**
	 * Deletes a given <code>UserFormMap</code> from the database.
	 * @param map map to delete.
	 */
	void deleteUserMappedForm(UserFormMap map);

	/**
	 * Fetches a list of <code>UserFormMap</code> definitions from the database.
	 * @return List of <code>UserFormMap</code> definitions.
	 */
	List<UserFormMap> getUserMappedForms();

	/**
	 * Persists a given <code>UserFormMap</code> to the database.
	 * @param map map to persist.
	 */
	void saveUserMappedForm(UserFormMap map);
	
	/**
	 * Gets all the forms that are mapped to the specified user
	 * @param user User
	 * @return List of FormDef
	 */
	List<FormDef> getFormsForUser(User user);
	
	/**
	 * Gets all the forms that are mapped to the specified user in a specified study
	 * 
	 * @param user User
	 * @param studyDefId Integer
	 * @return List of FormDef
	 */
	List<FormDef> getFormsForUser(User user, Integer studyDefId);
}
