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

import org.openxdata.server.admin.model.User;

/**
 * Provides data access 
 * services to the <code>User service</code>.
 * 
 * @author Angel
 *
 */
public interface UserDAO extends BaseDAO<User> {
	
	/**
	 * Gets a list of users in the database.
	 * 
	 * @return the user list.
	 */
	List<User> getUsers();
	
	/**
	 * Retrieves a user by their log in name
	 * 
	 * @param username String
	 * @return User, or null if no match found
	 */
	User getUser(String username);
	
	/**
	 * Retrieves a user by their email
	 * 
	 * @param email String
	 * @return User, or null if no match found
	 */	
	User findUserByEmail(String email);
	
	/**
	 * Retrieves a user by their phone number
	 * 
	 * @param phoneNo String
	 * @return User, or null if no match found
	 */	
	User findUserByPhoneNo(String phoneNo);
	
	/**
	 * Saves a user to the database.
	 * 
	 * @param user the user to save.
	 */
	void saveUser(User user);
	
	/**
	 * Removes a user from the database.
	 * 
	 * @param user the user to remove.
	 * 
	 */
	void deleteUser(User user);
	
	/**
	 * This method is supposed to be only called in the authentication 
	 * method and should not be annotated with spring security @secured annotation 
	 * because it is required to set the User's online status when the User logs in. The User 
	 * might not necessarily have permissions to call the method {@link #saveUser(User)}and yet we intend to have his online status saved.
	 * 
	 * @param user User to set online status for.
	 */
	void saveOnlineStatus(User user);
}
