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

import org.openxdata.server.OpenXDataConstants;
import org.openxdata.server.dao.SettingDAO;
import org.openxdata.server.serializer.DefaultXformSerializer;
import org.openxdata.server.serializer.StudySerializer;
import org.openxdata.server.serializer.UserSerializer;
import org.openxdata.server.serializer.XformSerializer;
import org.openxdata.server.service.SerializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Angel
 *
 */
@Transactional
@Service("serializationService")
// note: this is an internal service, so needs no security
public class SerializationServiceImpl implements SerializationService {

	@Autowired
	private SettingDAO settingDAO;
	
	public SerializationServiceImpl() {}
	
	/**
	 * @param settingDAO
	 */
	public SerializationServiceImpl(SettingDAO settingDAO) {
		this.settingDAO = settingDAO;
	}

	/**
     * Gets the value of data stream serializer.
     * 
     * @param name the name of the setting that points to the class
     * @param settingName the default name of the setting to use in case null is passed for the first parameter.
     * @param defaultValue the default value to return incase no setting with the given name exists.
     * @return the class name including its full pack.
     * 
     * @throws InstantiationException If the specified Serializer could not be loaded.
     */
    private Object getSerializer(String name, String settingName, String defaultValue) throws InstantiationException{
		String key = name;
		if(key == null || key.trim().length() == 0)
			key = settingName;
		
		String serializer = settingDAO.getSetting(key, defaultValue);
		try {
			Object obj = Class.forName(serializer).newInstance();
			return obj;
		} catch (Exception e) { 
			throw new InstantiationException();
		}
	}
    
	@Override
	public XformSerializer getFormSerializer(String name){
		try {
			Object serializer = getSerializer(name, OpenXDataConstants.SETTING_NAME_FORM_SERIALIZER, OpenXDataConstants.DEFAULT_FORM_SERIALIZER);
			if (serializer instanceof XformSerializer)
				return (XformSerializer) serializer;
		} catch (InstantiationException e) {
			// do nothing
		}
		
		return new DefaultXformSerializer();
		
	}
	
	@Override
	public UserSerializer getUserSerializer(String name){
		try {
			Object serializer = getSerializer(name, OpenXDataConstants.SETTING_NAME_USER_SERIALIZER, OpenXDataConstants.DEFAULT_USER_SERIALIZER);
			if (serializer instanceof UserSerializer)
				return (UserSerializer) serializer;
		} catch (InstantiationException e) {
			// do nothing
		}
		return new DefaultXformSerializer();
	}
	
	@Override
	public StudySerializer getStudySerializer(String name){
		 try {
			Object serializer = getSerializer(name, OpenXDataConstants.SETTING_NAME_STUDY_SERIALIZER, OpenXDataConstants.DEFAULT_STUDY_SERIALIZER);
			if (serializer instanceof StudySerializer)
				return (StudySerializer) serializer;
		} catch (InstantiationException e) {
			// do nothing
		}
		return new DefaultXformSerializer();
	}

}
