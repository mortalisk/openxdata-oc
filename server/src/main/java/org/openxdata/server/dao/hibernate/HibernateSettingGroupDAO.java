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

import org.openxdata.server.admin.model.SettingGroup;
import org.openxdata.server.dao.SettingGroupDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Angel
 *
 */
@Repository("settingGroupDAO")
public class HibernateSettingGroupDAO extends BaseDAOImpl<SettingGroup> implements SettingGroupDAO{

	@Override
	public void deleteSettingGroup(SettingGroup settingGroup) {
		remove(settingGroup);
	}

	@Override
	@Transactional(readOnly=true)
	public List<SettingGroup> getSettingGroups() {
		return findAll();
	}

	@Override
	public void saveSettingGroup(SettingGroup settingGroup) {
		save(settingGroup);		
	}

	@Override
	@Transactional(readOnly=true)
	public SettingGroup getSettingGroup(String name) {
		return searchUniqueByPropertyEqual("name", name);
	}
}
