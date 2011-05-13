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

import org.hibernate.SessionFactory;
import org.openxdata.server.admin.model.Setting;
import org.openxdata.server.dao.SettingDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * Provides a hibernate implementation
 * of the <code>SettingDAO</code> data access <code> interface.</code>
 * 
 * @author Angel
 *
 */
@Repository("settingDAO")
public class HibernateSettingDAO extends BaseDAOImpl<Setting> implements SettingDAO {
	
	@Autowired
	public SessionFactory sessionFactory;
	
	@Override
	public void deleteSetting(Setting setting) {
		remove(setting);
	}

	@Override
	public List<Setting> getSettings() {		
		return findAll();
	}

	@Override
	public void saveSetting(Setting setting){
		save(setting);
	}
	
	@Override
	public String getSetting(String name){
		Setting setting = searchUnique(new Search().addFilterEqual("name", name));
		if(setting != null)
			return setting.getValue();
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see org.openxdata.server.dao.SettingDAO#getSetting(java.lang.String, java.lang.String)
	 */
	@Override
	public String getSetting(String name, String defaultValue) {
		try{
			String value = getSetting(name);
			if(value == null)
				return defaultValue;
			
			return value;
		}
		catch(Exception ex){
			return defaultValue;
		}
	}
}
