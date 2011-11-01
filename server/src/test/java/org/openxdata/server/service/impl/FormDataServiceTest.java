package org.openxdata.server.service.impl;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.security.util.OpenXDataSecurityUtil;
import org.openxdata.server.service.FormService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests the FormService type contract.
 * 
 * @author daniel
 * 
 */
public class FormDataServiceTest extends BaseContextSensitiveTest {

	@Autowired
	protected FormService formService;
	
	@Test
	public void testGetFormDataShouldReturnNullIfNoFormDataFoundWithGivenId() throws Exception {
		assertNull("formData with id=-1 exists", formService.getFormData(new Integer(-1)));
	}
	
	@Test
	public void testGetFormDataShouldNotReturnNullIfFormDataFoundWithGivenId() throws Exception {
		assertNotNull("formData with id=1 doesn't exist", formService.getFormData(1));
	}

	@Test
	public void deleteFormDataSDeleteFormDataWithGivenId() throws Exception {
		FormData formData = formService.getFormData(new Integer(1));
		assertNotNull("form data does not exist", formData);
		formData.setChangedBy(OpenXDataSecurityUtil.getLoggedInUser());
		formData.setDateChanged(new Date());
		formService.deleteFormData(formData);
		assertNull("formData still exists", formService.getFormData(new Integer(1)));
	}
	
	@Test
	public void testSaveFormData() throws Exception {
		FormData fd = new FormData(1, "data", "description", new Date(), new User(1, "guyzb"));
		formService.saveFormData(fd);
		assertNotNull("Id has been set", fd.getId());
	}
}
