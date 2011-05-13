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

import java.util.List;

import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataSessionExpiredException;
import org.openxdata.server.admin.model.exception.UserNotFoundException;

/**
 * This service is used for 
 * managing <code>Users</code> and provide user related functionality.
 * 
 * @author dagmar@cell-life.org.za
 * @author Angel
 */
public interface UserService {

    /**
     * Gets a user based on their user name
     * @param username String login name
     * @return User, or null if no match found
     */
    User findUserByUsername(String username) throws UserNotFoundException;

    /**
     * Finds a user based on their email
     * @param email String 
     * @return User, or null if no match found
     */
    User findUserByEmail(String email) throws UserNotFoundException;
    
	/**
	 * Finds a user based on their phone number (msisdn)
	 * @param phoneNo String
	 * @return User, or null if no match found
	 */
	User findUserByPhoneNo(String phoneNo) throws UserNotFoundException;
    	
	/**
	 * Saves a new and modified users to the database.
	 * 
	 * @param user the user to save.
	 */
	void saveUser(User user);
	
    /**
     * Resets the user's password and saves the user
     *
     */
    void resetPassword(User user, int size);
    
	/**
     * Gets a list of users in the database.
     * 
     * @return the user list.
     */
	List<User> getUsers();
	
	/**
	 * Removes a user from the database.
	 * 
	 * @param user the user to remove.
	 */
	void deleteUser(User user);
	
	/**
	 * Gets the currently logged in <code>User</code>.
	 * 
	 * @return <code>User</code> only and only <code>if(Context.getAuthenticatedUser() != null)</code>
	 * @throws OpenXDataSessionExpiredException <code>If(Context.getAuthenticatedUser == null)</code>
	 */
	User getLoggedInUser() throws OpenXDataSessionExpiredException;
	
	/**
	 * Ascertains if the Administrator changed the default password on initial login.
	 * 
	 * @return <code>True only and only if(OpenXDataUtil.checkIfAdministratorChangedPasswordOnInitialLogin() == true)</code>
	 */
	Boolean checkIfUserChangedPassword(User user);
	
	/**
	 * Import users from a CSV string of user data.
	 * <p>Expected columns are:
	 * <ul>
	 *   <li>name (compulsory)</li>
	 *   <li>clearTextPassword (compulsory)</li>
	 *   <li>firstName</li>
	 *   <li>middleName</li>
	 *   <li>lastName</li>
	 *   <li>phoneNo</li>
	 *   <li>email</li>
	 *   <li>roles (compulsory, comma separated list of role names)</li>
	 * </ul>
	 * Column headings must match exactly.
	 * The order of columns is not important and non-compulsory columns
	 * may be excluded.
	 * 
	 * @param userdata
	 * @return String containing error rows
	 */
	String importUsers(String userdata);
	
	/**
	 * Log the current user out of the system 
	 */
	void logout();

}