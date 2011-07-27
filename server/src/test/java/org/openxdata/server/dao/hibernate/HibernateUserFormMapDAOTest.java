package org.openxdata.server.dao.hibernate;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.dao.UserFormMapDAO;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class HibernateUserFormMapDAOTest extends BaseContextSensitiveTest {

    @Autowired
    private UserFormMapDAO dao;

    @Test
    public void getFormDefFromStudy() {
    	List<UserFormMap> maps = dao.getUserMappedForms(1); // Sample Form (?)
    	Assert.assertNotNull(maps);
    	for (UserFormMap map : maps) {
    		Assert.assertEquals(1, map.getFormId());
    	}
    }
}
