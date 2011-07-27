package org.openxdata.server.dao.hibernate;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.dao.FormDAO;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class HibernateFormDAOTest extends BaseContextSensitiveTest {

    @Autowired
    private FormDAO dao;

    @Test
    public void getFormDefFromStudy() {
    	List<FormDef> forms = dao.getForms(1); // Sample Study
    	Assert.assertNotNull(forms);
    	Assert.assertEquals(2, forms.size());
    	Assert.assertEquals("Sample Form", forms.get(0).getName());
    }
    
    @Test
    public void getFormNameFromStudy() {
    	Map<Integer, String> formNames = dao.getFormNames(1); // Sample Study
    	Assert.assertNotNull(formNames);
    	Assert.assertEquals(2, formNames.size());
    	Assert.assertEquals("Sample Form", formNames.get(1));
    }
}
