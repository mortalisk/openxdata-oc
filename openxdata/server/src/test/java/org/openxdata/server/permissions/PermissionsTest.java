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
package org.openxdata.server.permissions;

import org.junit.Before;
import org.junit.Ignore;
import org.openxdata.server.service.AuthenticationService;
import org.openxdata.server.service.LocaleService;
import org.openxdata.server.service.ReportService;
import org.openxdata.server.service.RoleService;
import org.openxdata.server.service.SettingService;
import org.openxdata.server.service.StudyManagerService;
import org.openxdata.server.service.TaskService;
import org.openxdata.server.service.UserService;
import org.openxdata.server.service.UtilityService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Base class for unit tests for permissions.
 * 
 * @author daniel
 *
 */
@Ignore
public abstract class PermissionsTest extends BaseContextSensitiveTest {
	
	public static String USER_NAME = "user";
	public static String PASSWORD = "love";
	
	@Autowired
	protected UserService userService;
	
	@Autowired
	protected StudyManagerService studyManagerService;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@Autowired
	protected RoleService roleService;
	
	@Autowired
	protected UtilityService utilityService;
	
	@Autowired
	protected LocaleService localeService;

	@Autowired
	protected TaskService taskService;
	
	@Autowired
	protected SettingService settingService;
	
	@Autowired
	protected ReportService reportService;
	
	/**
	 * Run before each unit test in this class.
	 * 
	 * @throws Exception
	 */
	@Before
	@Override
	public void runBeforeEachTest() throws Exception {	
		authenticationService.authenticate(USER_NAME, PASSWORD);
	}
}
