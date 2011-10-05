package org.openxdata.server.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.StudyDef;
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
		return super.find(id);
	}
	
	/* (non-Javadoc)
	 * @see org.openxdata.server.dao.FormDAO#getForms()
	 */
	@Override
	public List<FormDef> getForms() {
		return findAll();
	}
	
	@Override
	public List<FormDef> getForms(Integer studyId) {
		StudyDef value = new StudyDef();
		value.setId(studyId);
		return this.searchByPropertyEqual("study", value);
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
}
