package org.openxdata.server.permissions;

import org.junit.Test;
import org.openxdata.server.admin.model.Setting;
import org.openxdata.server.admin.model.SettingGroup;
import org.openxdata.server.admin.model.exception.OpenXDataException;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;

/**
 * Tests permissions for accessing settings.
 * 
 * @author daniel
 * 
 */
public class SettingPermissionsTest extends PermissionsTest {

	@Test(expected = OpenXDataSecurityException.class)
	public void saveSettingGroup_shouldThrowOpenXDataSecurityException()
			throws OpenXDataException {
		settingService.saveSettingGroup(new SettingGroup());
	}

	@Test(expected = OpenXDataSecurityException.class)
	public void saveSettings_shouldThrowOpenXDataSecurityException()
			throws OpenXDataException {
		settingService.saveSetting(new Setting());
	}

	@Test(expected = OpenXDataSecurityException.class)
	public void deleteSettingGroup_shouldThrowOpenXDataSecurityException()
			throws OpenXDataException {
		settingService.deleteSettingGroup(new SettingGroup());
	}

	@Test(expected = OpenXDataSecurityException.class)
	public void deleteSetting_shouldThrowOpenXDataSecurityException()
			throws OpenXDataException {
		settingService.deleteSetting(new Setting());
	}
}
