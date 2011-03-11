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

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.UserNotFoundException;
import org.openxdata.server.service.UserService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests methods in the UserService which deal with users.
 * 
 * @author daniel
 * 
 */
public class UserServiceTest extends BaseContextSensitiveTest {

	@Autowired
	protected UserService userService;

	@Test
	public void getUsers_shouldReturnAllUsers() throws Exception {

		List<User> users = userService.getUsers();

		Assert.assertNotNull(users);
		Assert.assertEquals("There are 5 users", 5, users.size());
	}

	@Test
	public void saveUser_shouldSaveUser() throws Exception {
		final String userName = "User Name";

		List<User> users = userService.getUsers();
		Assert.assertEquals("There are 5 users", 5, users.size());
		Assert.assertNull(getUser(userName, users));

		User user = new User(userName);
		user.setCreator(users.get(0));
		user.setDateCreated(new Date());

		userService.saveUser(user);

		users = userService.getUsers();
		Assert.assertEquals("Added 1 user so now there are 6", 6, userService.getUsers().size());
		Assert.assertNotNull(getUser(userName, users));
	}

	@Test
	public void deleteUser_shouldDeleteGivenUser() throws Exception {

		final String userName = "User Name";

		List<User> users = userService.getUsers();
		Assert.assertEquals("There are 5 users", 5, users.size());
		Assert.assertNull(getUser(userName, users));

		User user = new User(userName);
		user.setCreator(users.get(0));
		user.setDateCreated(new Date());

		userService.saveUser(user);
		users = userService.getUsers();
		Assert.assertEquals("Added 1 user so now there are 6", 6, users.size());

		user = getUser(userName, users);
		Assert.assertNotNull(user);

		userService.deleteUser(user);

		users = userService.getUsers();
		Assert.assertEquals("Deleted the user so now there are 5", 5, users.size());
		Assert.assertNull(getUser(userName, users));
	}

	@Test
	public void testFindUserByEmail() throws Exception {
		User user = userService.findUserByEmail("cattabanks@gmail.com");
		Assert.assertNotNull("mark's users is there", user);
		Assert.assertEquals("his username is admin", "admin", user.getName());

	}

    @Test(expected=UserNotFoundException.class)
    public void findUserByEmailShouldThrowExceptionWhenNoUsersFound() throws UserNotFoundException {
		userService.findUserByEmail("nothere@gmail.com");		
    }

	@Test
	public void testFindUserByPhoneNo() throws Exception {
		User user = userService.findUserByPhoneNo("0768198075");
		Assert.assertNotNull("dagmar's users is there", user);
		Assert.assertEquals("her username is user", "user", user.getName());

	}

    @Test(expected=UserNotFoundException.class)
    public void findUserByPhoneNoShouldThrowExceptionWhenNoUsersFound() throws UserNotFoundException {
		userService.findUserByPhoneNo("555111222");
    }

	@Test
	public void testUserImport() throws Exception {
		URL resource = this.getClass().getClassLoader().getResource("org/openxdata/server/service/impl/import.csv");
		String importString = FileUtils.readFileToString(new File(resource.toURI()), "UTF-8");

		resource = this.getClass().getClassLoader().getResource("org/openxdata/server/service/impl/importErrors.csv");
		String expectedErrorString = FileUtils.readFileToString(new File(resource.toURI()), "UTF-8");
		expectedErrorString = expectedErrorString.trim().replaceAll("\\n*\\r*", "");

		String errorString = null;
		try {
			errorString = userService.importUsers(importString);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.out.println(errorString);
		errorString = errorString.trim().replaceAll("\\n*\\r*", "");

		Assert.assertEquals(expectedErrorString, errorString);
		User user = userService.findUserByUsername("name1");
		Assert.assertNotNull(user);
		Assert.assertEquals("name1", user.getName());
		Assert.assertEquals("firstName1", user.getFirstName());
		Assert.assertEquals("middleName1", user.getMiddleName());
		Assert.assertEquals("lastName1", user.getLastName());
		Assert.assertEquals("phoneNo1", user.getPhoneNo());
		Assert.assertEquals("email1", user.getEmail());
		Assert.assertTrue(user.getRoles()!=null);
		Assert.assertTrue(user.getRoles().size() == 2);
	}
	
	/**
	 * Gets a user object for a given name from a list of user objects.
	 * 
	 * @param name
	 *            the name of the user to look for.
	 * @param users
	 *            the list of user objects.
	 * @return the user object that matches the given name.
	 */
	private User getUser(String name, List<User> users) {
		for (User user : users) {
			if (user.getName().equals(name))
				return user;
		}

		return null;
	}
}
