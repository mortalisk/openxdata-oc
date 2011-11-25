package org.openxdata.server.dao.hibernate;

import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;
import org.openxdata.server.dao.FormVersionDAO;
import org.springframework.stereotype.Repository;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

/**
 *
 */
@Repository("formVersionDAO")
public class HibernateFormVersionDAO extends BaseDAOImpl<FormDefVersion> implements FormVersionDAO {

	@Override
	public PagingLoadResult<FormDefVersion> getForms(PagingLoadConfig loadConfig) {
		Search formSearch = getSearchFromLoadConfig(loadConfig, "formDef.name");
		SearchResult<FormDefVersion> result = searchAndCount(formSearch);
		return getPagingLoadResult(loadConfig, result);
	}

	@Override
	public PagingLoadResult<FormDefVersion> getForms(User user, PagingLoadConfig loadConfig) {
		if (user.hasAdministrativePrivileges()) {
			return getForms(loadConfig);
		} else {
			Search formSearch = getSearchFromLoadConfig(loadConfig, "formDef.name");
			formSearch.addFilterOr(Filter.some("formDef.users", Filter.equal("id", user.getId())), Filter.some("formDef.study.users", Filter.equal("id", user.getId())));
			SearchResult<FormDefVersion> result = searchAndCount(formSearch);
			return getPagingLoadResult(loadConfig, result);
		}
	}

	@Override
	public FormDefVersion getFormDefVersion(Integer formDefVersion) {
		return find(formDefVersion);
	}

}
