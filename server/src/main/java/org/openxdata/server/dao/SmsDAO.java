package org.openxdata.server.dao;

import java.util.List;

import org.openxdata.server.admin.model.FormSmsArchive;

public interface SmsDAO extends BaseDAO<FormSmsArchive> {
	
	List<FormSmsArchive> getFormSmsArchives();

}
