package org.openxdata.server.service.impl;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDataHeader;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;
import org.openxdata.server.security.util.OpenXDataSecurityUtil;
import org.openxdata.server.service.FormService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
	public void getSetting_shouldReturnNullIfNoFormDataFoundWithGivenId() throws Exception {
		Assert.assertNull("formData with id=-1 exists", formService.getFormData(new Integer(-1)));
	}
	
	@Test
	public void getSetting_shouldNotReturnNullIfFormDataFoundWithGivenId() throws Exception {
		Assert.assertNotNull("formData with id=1 doesn't exist", formService.getFormData(1));
	}

	@Test
	public void deleteFormData_shouldDeleteFormDataWithGivenId() throws Exception {
		FormData formData = formService.getFormData(new Integer(1));
		Assert.assertNotNull("form data does not exist", formData);
		formData.setChangedBy(OpenXDataSecurityUtil.getLoggedInUser());
		formData.setDateChanged(new Date());
		formService.deleteFormData(formData);
		Assert.assertNull("formData still exists", formService.getFormData(new Integer(1)));
	}
	
	@Test
	public void saveFormData() throws Exception {
		FormData fd = new FormData(1, "data", "description", new Date(), new User(1, "guyzb"));
		formService.saveFormData(fd);
		Assert.assertNotNull("Id has been set", fd.getId());
	}
	
    @Test
    @Transactional(readOnly = true)
    public void getFormDataCount() {
        Assert.assertSame(1,formService.getFormResponseCount(1));
    }
	
	@Test
	public void getUnexportedData() throws Exception {
		PagingLoadResult<FormDataHeader> result = formService.getUnexportedFormData(new PagingLoadConfig(0,10));
		List<FormDataHeader> headers = result.getData();
		Assert.assertNotNull(headers);
		Assert.assertEquals(1, headers.size());
	}
}
