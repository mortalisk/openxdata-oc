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

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openxdata.server.admin.model.Setting;
import org.openxdata.server.admin.model.SettingGroup;
import org.openxdata.server.admin.model.exception.OpenXDataException;
import org.openxdata.server.service.SettingService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests methods in the SettingService which deal with settings.
 * I think we should re factor out these methods into a SettingsService
 * 
 * @author daniel
 *
 */
public class SettingServiceTest extends BaseContextSensitiveTest {

    @Autowired
    protected SettingService settingService;

    @Test
    public void getSetting_shouldReturnNullIfNoSettingFoundWithGivenName() throws Exception {
        Assert.assertNull(settingService.getSetting("some non existing setting name"));
    }

    @Test
    public void getSetting_shouldNotReturnNullIfSettingFoundWithGivenName() throws Exception {
        Assert.assertNotNull(settingService.getSetting("epihandyser"));
    }

    @Test
    public void testGetSettings() {
    	assertNotNull(settingService.getSettings());
    }
    
    @Test
    public void saveSetting_shouldSaveSetting() throws Exception {
        Assert.assertNull(settingService.getSetting("Name"));

        settingService.saveSetting(new Setting("Name", "Description", "Value"));
        
        String setting = settingService.getSetting("Name");
        Assert.assertNotNull(setting);
    }

    @Test
    public void saveSettingGroup_shouldSaveSettingGroup() throws Exception {
        Assert.assertNull(getSettingGroup("Name"));

        settingService.saveSettingGroup(new SettingGroup("Name"));

        Assert.assertNotNull(getSettingGroup("Name"));
    }

    @Test
    public void deleteSetting_shouldDeleteSettingWithGivenName() throws Exception {
        Setting setting = getSetting("epihandyser");
        Assert.assertNotNull(setting);

        setting.getSettingGroup().removeSetting(setting);
        settingService.deleteSetting(setting);
        Assert.assertNull(getSetting("epihandyser"));
    }

    @Test
    public void deleteSettingGroup_shouldDeleteSettingGroupWithGivenName() throws Exception {
        SettingGroup settingGroup = getSettingGroup("General");
        Assert.assertNotNull(settingGroup);

        settingService.deleteSettingGroup(settingGroup);

        Assert.assertNull(getSettingGroup("General"));
    }

    @Test
    public void getSettings_shouldReturnAllSettings() throws Exception {

        List<SettingGroup> settingGroups = settingService.getSettings();

        Assert.assertNotNull(settingGroups);
        Assert.assertEquals(4, settingGroups.size());

        for (SettingGroup settingGroup : settingGroups) {

            String name = settingGroup.getName();
            List<Setting> settings = settingGroup.getSettings();
            Assert.assertNotNull(settings);

            if (name.equals("General")) {
                Assert.assertEquals(1, settings.size());
            } else if (name.equals("Date")) {
                Assert.assertEquals(6, settings.size());
            } else if (name.equals("Serialization")) {
                Assert.assertEquals(3, settings.size());
            } else if (name.equals("SMS")) {
                Assert.assertEquals(5, settings.size());
            } else {
                Assert.fail("Expected Setting group name: Date, General, Serialization or SMS");
            }
        }
    }

    /**
     * Gets a setting object with a given name.
     *
     * @param name the name of the setting.
     * @return the setting object.
     * @throws OpenXDataException
     */
    private Setting getSetting(String name) throws OpenXDataException {
        List<SettingGroup> settingGroups = settingService.getSettings();
        return SettingGroup.getSettingFromGroups(settingGroups, name);
    }

    /**
     * Gets a setting group object with a given name.
     *
     * @param name the name of the setting group.
     * @return the setting group object.
     * @throws OpenXDataException
     */
    private SettingGroup getSettingGroup(String name) throws OpenXDataException {
        List<SettingGroup> settingGroups = settingService.getSettings();

        for (SettingGroup settingGroup : settingGroups) {
            if (settingGroup.getName().equals(name)) {
                return settingGroup;
            }
        }

        return null;
    }
}
