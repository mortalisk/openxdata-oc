package org.openxdata.server.dao.hibernate;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.openxdata.server.admin.model.FormDataHeader;
import org.openxdata.server.dao.EditableDAO;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Jonny Heggheim
 */
public class HibernateEditableDAOTest extends BaseContextSensitiveTest {

    @Autowired
    private EditableDAO dao;

    @Test
    @Transactional(readOnly = true)
    public void getFormDataWithEmptyInput() {
        dao.getFormData(null, null, null, null);
    }
    
    @Test
    @Transactional(readOnly = true)
    public void getFormDataForAFormDef() {
        dao.getFormData(12, null, null, null);
    }
    
    @Test
    @Transactional(readOnly = true)
    public void getFormDataForUser() {
        dao.getFormData(12, 12, null, null);
    }
    
    @Test
    @Transactional(readOnly = true)
    public void getFormDataBetweenDates() {
        List<FormDataHeader> results = dao.getFormData(12, null, new Date(), new Date());
        for (FormDataHeader result : results) {
        	System.out.println("result = "+result.getFormDataId());
        }
    }

    @Test
    @Transactional(readOnly = true)
    public void getFormDataWithNonEmptyInput() {
        dao.getFormData(12, 12, new Date(), new Date());
    }
}
