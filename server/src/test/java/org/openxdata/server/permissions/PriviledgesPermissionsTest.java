package org.openxdata.server.permissions;

import org.junit.Test;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.exception.OpenXDataException;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;

/**
 * For testing the permissions that access permissions.
 * 
 *
 */
public class PriviledgesPermissionsTest extends PermissionsTest {
    
    @Test(expected=OpenXDataSecurityException.class)
    public void savePermission_shouldThrowOpenXDataSecurityException() throws OpenXDataException {
	roleService.savePermission(new Permission());
    }
    
    @Test(expected=OpenXDataSecurityException.class)
    public void deletePermission_shouldThrowOpenXDataSecurityException() throws OpenXDataException {
    	roleService.deletePermission(new Permission());
    }
}
