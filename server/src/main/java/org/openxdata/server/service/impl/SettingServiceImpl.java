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

import org.openxdata.server.admin.model.Setting;
import org.openxdata.server.admin.model.SettingGroup;
import org.openxdata.server.dao.SettingDAO;
import org.openxdata.server.dao.SettingGroupDAO;
import org.openxdata.server.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation for <code>Setting Service</code>.
 * 
 * @author Angel
 *
 */
@Service("settingService")
@Transactional
public class SettingServiceImpl implements SettingService {

	@Autowired
    private SettingDAO settingDAO;
	
	@Autowired
	private SettingGroupDAO settingGroupDAO;

    public void setSettingDAO(SettingDAO settingDAO) {
        this.settingDAO = settingDAO;
    }

    /* (non-Javadoc)
     * @see org.openxdata.server.service.SettingService#deleteSetting(org.openxdata.server.admin.model.Setting)
     */
    @Override
    @Secured("Perm_Delete_Settings")
	public void deleteSetting(Setting setting) {
        settingDAO.deleteSetting(setting);
    }

    /* (non-Javadoc)
     * @see org.openxdata.server.service.SettingService#deleteSettingGroup(org.openxdata.server.admin.model.SettingGroup)
     */
    @Override
    @Secured("Perm_Delete_SettingsGroup")
	public void deleteSettingGroup(SettingGroup settingGroup) {
    	settingGroupDAO.deleteSettingGroup(settingGroup);
    }

    /* (non-Javadoc)
     * @see org.openxdata.server.service.SettingService#getSetting(java.lang.String)
     */
    @Override
	@Transactional(readOnly=true)
	// note: no security required to read settings
    public String getSetting(String name) {
        return settingDAO.getSetting(name);
    }

    /* (non-Javadoc)
     * @see org.openxdata.server.service.SettingService#getSettings()
     */
    @Override
	@Transactional(readOnly=true)
	// note: no security required to read settings
    public List<SettingGroup> getSettings() {
        return settingGroupDAO.getSettingGroups();
    }

    /* (non-Javadoc)
     * @see org.openxdata.server.service.SettingService#saveSetting(org.openxdata.server.admin.model.Setting)
     */
    @Override
    @Secured("Perm_Add_Settings")
	public void saveSetting(Setting setting) {
        settingDAO.saveSetting(setting);
    }

    /* (non-Javadoc)
     * @see org.openxdata.server.service.SettingService#saveSettingGroup(org.openxdata.server.admin.model.SettingGroup)
     */
    @Override
    @Secured("Perm_Add_SettingsGroup")
	public void saveSettingGroup(SettingGroup settingGroup) {
    	settingGroupDAO.saveSettingGroup(settingGroup);
    }
    
    @Override
	@Transactional(readOnly=true)
	// note: no security required to read settings
	public String getSetting(String name, String defaultValue){
		return settingDAO.getSetting(name, defaultValue);
	}

	@Override
	@Transactional(readOnly=true)
	// note: no security required to read settings
	public SettingGroup getSettingGroup(String name) {
		return settingGroupDAO.getSettingGroup(name);
	}
}
