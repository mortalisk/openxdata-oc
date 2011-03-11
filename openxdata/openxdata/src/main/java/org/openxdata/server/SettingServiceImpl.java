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
package org.openxdata.server;

import java.util.List;

import javax.servlet.ServletException;

import org.openxdata.server.admin.client.service.SettingService;
import org.openxdata.server.admin.model.Setting;
import org.openxdata.server.admin.model.SettingGroup;
import org.openxdata.server.rpc.OxdPersistentRemoteService;
import org.springframework.web.context.WebApplicationContext;

/**
 * Default Implementation for the <code>SettingService Interface.</code>
 * 
 * @author Angel
 *
 */
public class SettingServiceImpl extends OxdPersistentRemoteService implements SettingService {

	private static final long serialVersionUID = -7213691819930913724L;
	private org.openxdata.server.service.SettingService settingService;
	
	public SettingServiceImpl() {}
	
	@Override
	public void init() throws ServletException {
		super.init();
		WebApplicationContext ctx = getApplicationContext();
		settingService = (org.openxdata.server.service.SettingService)ctx.getBean("settingService");
	}

	@Override
	public void deleteSetting(Setting setting) {
		settingService.deleteSetting(setting);
	}

	@Override
	public void deleteSettingGroup(SettingGroup settingGroup) {
		settingService.deleteSettingGroup(settingGroup);
	}

	@Override
	public String getSetting(String name) {
		return settingService.getSetting(name);
	}

	@Override
	public List<SettingGroup> getSettings() {
		return settingService.getSettings();
	}

	@Override
	public void saveSetting(Setting setting) {
		settingService.saveSetting(setting);
	}

	@Override
	public void saveSettingGroup(SettingGroup settingGroup) {
		settingService.saveSettingGroup(settingGroup);
	}

	@Override
	public SettingGroup getSettingGroup(String name) {
		return settingService.getSettingGroup(name);
	}
}
