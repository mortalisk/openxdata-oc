package org.openxdata.server.dao.hibernate;

import org.junit.Test;
import org.openxdata.server.dao.FormDownloadDAO;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class HibernateFormDownloadDAOTest extends BaseContextSensitiveTest {

	@Autowired
	private FormDownloadDAO dao;

	@Test
	public void testOXD397() {
		// throws an exception due to duplicate version text records
		dao.getXformLocaleText(2, "en");
	}

}
