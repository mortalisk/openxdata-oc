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

import org.openxdata.server.admin.model.Setting;

/**
 * Provides data access 
 * services to the <code>Setting service</code>.
 * 
 * @author Angel
 *
 */
public interface SettingDAO extends BaseDAO<Setting> {

	/**
	 * Gets the value of a setting with a given name.
	 * 
	 * @param name the name of the setting whose value to get.
	 * @return the value of the setting.
	 */
	List<Setting> getSettings();
	
	/**
	 * Saves a setting to the database.
	 * 
	 * @param setting the setting to save.
	 */
	void saveSetting(Setting setting);
	
	/**
	 * Deletes a setting from the database.
	 * 
	 * @param setting the setting to delete.
	 */
	void deleteSetting(Setting setting);
	
	/**
	 * Gets the value of a setting with a given name.
	 * 
	 * @param name the setting name.
	 * @return the value of the setting.
	 */
	public String getSetting(String name);
	
	/**
	 * Gets the value of a setting with a given name.
	 * 
	 * @param name the setting name.
	 * @param the default Value to return if the setting is not found.
	 * 
	 * @return the value of the setting.
	 */
	public String getSetting(String name, String defaultValue);
	
}
