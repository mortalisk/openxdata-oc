package org.openxdata.server.dao.hibernate;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserStudyMap;
import org.openxdata.server.dao.UserStudyMapDAO;
import org.openxdata.server.service.UserService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class HibernateUserStudyMapDAOTest extends BaseContextSensitiveTest {

    @Autowired
    private UserStudyMapDAO dao;
    
    @Autowired
	protected UserService userService;

    @Test
    public void getFormDefFromStudy() {
    	List<UserStudyMap> maps = dao.getUserMappedStudies(1); // Sample Study
    	Assert.assertNotNull(maps);
    	for (UserStudyMap map : maps) {
    		Assert.assertEquals(1, map.getStudyId());
    	}
    }
    
	@Test
	public void testGetStudynamesAdmin() throws Exception {
		User user = userService.findUserByUsername("admin");
		Map<Integer, String> names = dao.getStudyNamesForUser(user);
		Assert.assertEquals(4, names.size());
		Assert.assertNotNull(names.get(1));
		Assert.assertEquals("Sample Study", names.get(1));
	}
	
	@Test
	public void testGetStudynamesNotAdmin() throws Exception {
		User user = userService.findUserByUsername("user");
		Map<Integer, String> names = dao.getStudyNamesForUser(user);
		Assert.assertEquals(1, names.size());
		Assert.assertNotNull(names.get(1));
		Assert.assertEquals("Sample Study", names.get(1));
	}
}
