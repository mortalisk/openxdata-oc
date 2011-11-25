package org.openxdata.server.dao;

import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;

/**
 *
 */
public interface FormVersionDAO extends BaseDAO<FormDefVersion> {

	PagingLoadResult<FormDefVersion> getForms(PagingLoadConfig loadConfig);

	PagingLoadResult<FormDefVersion> getForms(User user, PagingLoadConfig loadConfig);
	
	FormDefVersion getFormDefVersion(Integer formDefVersion);
}
