package org.openxdata.server.service.impl;

import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.UserNotFoundException;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.admin.model.mapping.UserStudyMap;
import org.openxdata.server.service.StudyManagerService;
import org.openxdata.server.service.UserService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests methods in the UserService which deal with users.
 * 
 * @author daniel
 * 
 */
public class UserServiceTest extends BaseContextSensitiveTest {

	private static Logger log = LoggerFactory.getLogger(UserServiceTest.class);
	
	@Autowired
	protected UserService userService;
	
	@Autowired
	private StudyManagerService studyService;
	
	List<User> dummyUsers;
	final String userName = "User Name";
	
	@Before
	public void setUp(){
		
		List<User> users = userService.getUsers();
		
		dummyUsers = new ArrayList<User>();
		User user = new User(userName);
		user.setCreator(users.get(0));
		user.setDateCreated(new Date());
		
		User user2 = new User(userName);
		user2.setCreator(users.get(0));
		user2.setDateCreated(new Date());
		
		User user3 = new User(userName);
		user3.setCreator(users.get(0));
		user3.setDateCreated(new Date());
		
		dummyUsers.add(user);
		dummyUsers.add(user2);
		dummyUsers.add(user3);
	}

	@Test
	public void getUsers_shouldReturnAllUsers() throws Exception {

		List<User> users = userService.getUsers();

		Assert.assertNotNull(users);
		Assert.assertEquals("There are 6 users", 6, users.size());
	}

	@Test
	public void saveUser_shouldSaveUser() throws Exception {

		List<User> users = userService.getUsers();
		Assert.assertEquals("There are 6 users", 6, users.size());
		Assert.assertNull(getUser(userName, users));

		User user = new User(userName);
		user.setCreator(users.get(0));
		user.setDateCreated(new Date());

		userService.saveUser(user);

		users = userService.getUsers();
		Assert.assertEquals("Added 1 user so now there are 7", 7, userService.getUsers().size());
		Assert.assertNotNull(getUser(userName, users));
	}
	
	@Test
	public void testSaveUsers(){
		List<User> users = userService.getUsers();
		Assert.assertEquals("There are 6 users", 6, users.size());
		Assert.assertNull(getUser(userName, users));
		
		userService.saveUsers(dummyUsers);
		Assert.assertEquals("Added 3 users so now there are 9", 9, userService.getUsers().size());
	}
	
	@Test
	public void testDeleteUsers(){
		List<User> users = userService.getUsers();
		Assert.assertEquals("There are 6 users", 6, users.size());
		Assert.assertNull(getUser(userName, users));
		
		userService.saveUsers(dummyUsers);
		Assert.assertEquals("Added 3 users so now there are 9", 9, userService.getUsers().size());
		
		userService.deleteUsers(dummyUsers);
		Assert.assertEquals("Deleted 3 users so now there are 6", 6, userService.getUsers().size());
	}

	@Test
	public void deleteUser_shouldDeleteGivenUser() throws Exception {

		List<User> users = userService.getUsers();
		Assert.assertEquals("There are 6 users", 6, users.size());
		Assert.assertNull(getUser(userName, users));

		User user = new User(userName);
		user.setCreator(users.get(0));
		user.setDateCreated(new Date());

		userService.saveUser(user);
		users = userService.getUsers();
		Assert.assertEquals("Added 1 user so now there are 7", 7, users.size());

		user = getUser(userName, users);
		Assert.assertNotNull(user);

		userService.deleteUser(user);

		users = userService.getUsers();
		Assert.assertEquals("Deleted the user so now there are 6", 6, users.size());
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

		String errorfile = null;
		try {
			errorfile = userService.importUsers(importString);
		} catch (Throwable e) {
			e.printStackTrace();
 		}
		log.debug(errorfile);
		String errorString = IOUtils.toString(new StringReader(errorfile));
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
		
		User user2 = userService.findUserByUsername("name7");
		List<FormDef> forms = studyService.getFormByName("Sample Form");
		List<UserFormMap> formMaps = studyService.getUserMappedForms();
		int count = 0;
		for (UserFormMap map : formMaps) {
			if (map.getUserId() == user2.getId()) {
				count++;
				Assert.assertEquals(forms.get(0).getId(), map.getFormId());
			}
		}
		Assert.assertEquals(1, count);
		List<UserStudyMap> studyMaps = studyService.getUserMappedStudies();
		count = 0;
		for (UserStudyMap map : studyMaps) {
			if (map.getUserId() == user2.getId()) {
				count++;
			}
		}
		Assert.assertEquals(2, count);		
	}
	
	
	private User getUser(String name, List<User> users) {
		for (User user : users) {
			if (user.getName().equals(name))
				return user;
		}

		return null;
	}
}
