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
package org.openxdata.server.service;

import org.openxdata.server.admin.model.User;

public interface AuthenticationService {
	
	/**
	 * Authenticates a given <code>User</code>.
	 * 
	 * @param username <code>User's</code> user name.
	 * @param password <code>User's </code> password.
	 * 
	 * @return <code>User</code> only and only if <code>User</code> is successfully authenticated.
	 */
	User authenticate(String username, String password);

	/**
	 * Validates a user's password without authenticating them in the security context.
	 * 
	 * @param username
	 * @param password
	 * @return true if password matches user's password
	 */
	Boolean isValidUserPassword(String username, String password);
}
