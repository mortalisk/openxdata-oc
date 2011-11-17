package org.openxdata.server.dao.hibernate;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.dao.FormDAO;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class HibernateFormDAOTest extends BaseContextSensitiveTest {

    @Autowired
    private FormDAO dao;
    
    @Test public void testGetFormNameFromStudy() {
    	Map<Integer, String> formNames = dao.getFormNames(1);
    	Assert.assertNotNull(formNames);
    	Assert.assertEquals(2, formNames.size());
    	Assert.assertEquals("Sample Form", formNames.get(1));
    }
	
	@Test
	public void getFormDef() {
		FormDefVersion formDefVer = dao.getFormVersion(3);
		Assert.assertNotNull(formDefVer);
		Assert.assertEquals(3, formDefVer.getId());
		Assert.assertEquals(3, formDefVer.getFormDef().getId());
	}
}
