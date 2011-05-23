package org.openxdata.server.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataException;
import org.openxdata.server.service.UserService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests the {@link OpenXDataUtil} <tt>class.</tt>
 * 
 *
 */
public class OpenXDataUtilTest extends BaseContextSensitiveTest {
	
	@Autowired
	protected UserService userService;
	
	@Test
	public void testCheckIfUserChangedPassword_AssertFalse() throws OpenXDataException{
		User user = userService.findUserByUsername("admin");
		assertFalse(OpenXDataUtil.checkIfUserChangedPassword(user));
	}
	
	@Test
	public void testCheckIfUserChangedPassword_AssertTrue() throws OpenXDataException{
		User user = userService.findUserByUsername("admin");
		user.setPassword("silence");
		assertTrue(OpenXDataUtil.checkIfUserChangedPassword(user));
	}
	
	@Test
	public void testGetApplicationDirectory(){
		String appDirectory = OpenXDataUtil.getApplicationDataDirectory();
		
		assertNotNull(appDirectory);
	}
}