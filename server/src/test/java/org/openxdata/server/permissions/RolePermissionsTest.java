package org.openxdata.server.permissions;

import org.junit.Test;
import org.openxdata.server.admin.model.Role;
import org.openxdata.server.admin.model.exception.OpenXDataException;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;

/**
 * Tests permissions for accessing roles.
 * 
 * @author daniel
 * 
 */
public class RolePermissionsTest extends PermissionsTest {

	@Test(expected = OpenXDataSecurityException.class)
	public void getRoles_shouldThrowOpenXDataSecurityException()
			throws OpenXDataException {
		roleService.getRoles();
	}

	@Test(expected = OpenXDataSecurityException.class)
	public void saveRole_shouldThrowOpenXDataSecurityException()
			throws OpenXDataException {
		roleService.saveRole(new Role());
	}

	@Test(expected = OpenXDataSecurityException.class)
	public void deleteRoles_shouldThrowOpenXDataSecurityException()
			throws OpenXDataException {
		roleService.deleteRole(new Role());
	}
}
