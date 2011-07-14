package org.openxdata.server.permissions;

import org.junit.Test;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataException;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;

/**
 * Tests permissions for accessing users.
 * 
 * @author daniel
 * 
 */
public class UserPermissionsTest extends PermissionsTest {

	@Test(expected = OpenXDataSecurityException.class)
	public void getUsers_shouldThrowOpenXDataSecurityException()
			throws OpenXDataException {
		userService.getUsers();
	}

	@Test(expected = OpenXDataSecurityException.class)
	public void saveUser_shouldThrowOpenXDataSecurityException()
			throws OpenXDataException {
		userService.saveUser(new User());
	}

	@Test(expected = OpenXDataSecurityException.class)
	public void deleteUser_shouldThrowOpenXDataSecurityException()
			throws OpenXDataException {
		userService.deleteUser(new User());
	}
}
