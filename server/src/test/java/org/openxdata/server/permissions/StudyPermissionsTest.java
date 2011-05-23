package org.openxdata.server.permissions;


import org.junit.Test;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.exception.OpenXDataException;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;


/**
 * Tests permissions for accessing studies.
 * 
 * @author daniel
 *
 */
public class StudyPermissionsTest extends PermissionsTest {

	@Test(expected=OpenXDataSecurityException.class)
	public void getStudies_shouldThrowOpenXDataSecurityException() throws OpenXDataException {
		studyManagerService.getStudies();
	}
	
	@Test(expected=OpenXDataSecurityException.class)
	public void saveStudy_shouldThrowOpenXDataSecurityException() throws OpenXDataException {
		studyManagerService.saveStudy(new StudyDef());
	}
	
	@Test(expected=OpenXDataSecurityException.class)
	public void deleteStudy_shouldThrowOpenXDataSecurityException() throws OpenXDataException {
		studyManagerService.deleteStudy(new StudyDef());
	}

}
