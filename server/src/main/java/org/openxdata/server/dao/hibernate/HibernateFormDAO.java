package org.openxdata.server.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;
import org.openxdata.server.dao.FormDAO;
import org.springframework.stereotype.Repository;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

/**
 *
 */
@Repository("formDAO")
public class HibernateFormDAO extends BaseDAOImpl<FormDef> implements FormDAO {

	@Override
	public void deleteForm(FormDef formDef) {
		remove(formDef);
	}
	
	@Override
	public void saveForm(FormDef formDef) {
		save(formDef);
	}
	
	@Override
	public FormDef getForm(Integer id) {
		return find(id);
	}
	
	@Override
    public FormDef getForm(String name) {
	    return searchUniqueByPropertyEqual("name", name);
    }
	
	@Override
	public PagingLoadResult<FormDef> getForms(PagingLoadConfig loadConfig) {
		Search formSearch = getSearchFromLoadConfig(loadConfig, "name");
	    SearchResult<FormDef> result = searchAndCount(formSearch);
	    return getPagingLoadResult(loadConfig, result);
	}
	
	@Override
	public PagingLoadResult<FormDef> getForms(User user, PagingLoadConfig loadConfig) {
		if (user.hasAdministrativePrivileges()) {
			return getForms(loadConfig);
		} else {
			Search formSearch = getSearchFromLoadConfig(loadConfig, "name");
			formSearch.addFilterOr(
					Filter.some("users", Filter.equal("id", user.getId())), 
					Filter.some("study.users", Filter.equal("id", user.getId())));
		    SearchResult<FormDef> result = searchAndCount(formSearch);
		    return getPagingLoadResult(loadConfig, result);
		}
	}
	
	@Override 
	public List<FormDef> getStudyForms(User user, Integer studyDefId) {
		if (user.hasAdministrativePrivileges()) {
			return searchByPropertyEqual("study.id", studyDefId);
		} else {
			Search formSearch = new Search();
			formSearch.addFilterOr(
					Filter.all("users", Filter.equal("id", user.getId())), 
					Filter.all("study.users", Filter.equal("id", user.getId())));
			formSearch.addFilter(Filter.equal("study.id", studyDefId));
		    return search(formSearch);
		}
	}
	
	@SuppressWarnings("unchecked")
    @Override
	public Map<Integer, String> getFormNames(Integer studyId) {
		Query query = getSession().createQuery("select id, name from FormDef where study.id =:studyId");
		query.setInteger("studyId", studyId);
		Map<Integer, String> formNames = new HashMap<Integer, String>();
        List<Object[]> result = query.list();
		for (Object[] obj : result) {
			formNames.put((Integer)obj[0], (String)obj[1]);
		}
		return formNames;
	}

	@Override
    public PagingLoadResult<User> getMappedUsers(Integer formId, PagingLoadConfig loadConfig) {
		return findAllUsersByPage(loadConfig, true, FormDef.class, formId);
    }

	@Override
    public PagingLoadResult<User> getUnmappedUsers(Integer formId, PagingLoadConfig loadConfig) {
		return findAllUsersByPage(loadConfig, false, FormDef.class, formId);
    }

	@Override
    public PagingLoadResult<FormDef> getMappedForms(Integer userId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException {
		Search formSearch = getSearchFromLoadConfig(loadConfig, "name");
		formSearch.addFilterSome("users", Filter.equal("id", userId));
	    SearchResult<FormDef> result = searchAndCount(formSearch);
	    return getPagingLoadResult(loadConfig, result);
    }

	@Override
    public PagingLoadResult<FormDef> getUnmappedForms(Integer userId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException {
		Search formSearch = getSearchFromLoadConfig(loadConfig, "name");
		formSearch.addFilterAll("users", Filter.notEqual("id", userId));
	    SearchResult<FormDef> result = searchAndCount(formSearch);
	    return getPagingLoadResult(loadConfig, result);
    }

	@Override
	public FormDefVersion getFormVersion(Integer formVersionId) {
		return (FormDefVersion)getSession().createCriteria(FormDefVersion.class)
			.add(Restrictions.eq("id", formVersionId)).uniqueResult();
	}
}
