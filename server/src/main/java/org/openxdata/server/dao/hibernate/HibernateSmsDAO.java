package org.openxdata.server.dao.hibernate;

import java.util.List;

import org.openxdata.server.admin.model.FormSmsArchive;
import org.openxdata.server.dao.SmsDAO;
import org.springframework.stereotype.Repository;

@Repository("smsDAO")
public class HibernateSmsDAO extends BaseDAOImpl<FormSmsArchive> implements SmsDAO {

	@Override
	public List<FormSmsArchive> getFormSmsArchives() {
        return findAll();
	}
}
