package org.openxdata.server.dao.hibernate;

import java.util.List;

import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;
import org.openxdata.server.dao.StudyDAO;
import org.springframework.stereotype.Repository;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

/**
 *
 * @author Jonny Heggheim
 */
@Repository("studyDAO")
public class HibernateStudyDAO extends BaseDAOImpl<StudyDef> implements StudyDAO {

    @Override
    public void deleteStudy(StudyDef studyDef) {
        remove(studyDef);
    }

    @Override
    public List<StudyDef> getStudies() {
        return findAll();
    }
    
    @Override
    public StudyDef getStudy(Integer id) {
    	return super.find(id);
    }

    @Override
    public boolean save(StudyDef entity) {
        return super.save(entity);
    }

    @Override
    public void saveStudy(StudyDef studyDef) {
        save(studyDef);
    }

	@Override
	public String getStudyKey(Integer studyId) {
		Search search = new Search(StudyDef.class);
		search.addFilterEqual("id", studyId);
		search.addField("studyKey");
		return searchUnique(search);
	}
	
	@Override
	public String getStudyName(int studyId) {
		Search search = new Search(StudyDef.class);
		search.addFilterEqual("id", studyId);
		search.addField("name");
		return searchUnique(search);
	}

	@Override
    public PagingLoadResult<User> getMappedUsers(Integer studyId, PagingLoadConfig loadConfig) {
		return findAllUsersByPage(loadConfig, true, StudyDef.class, studyId);
    }

	@Override
    public PagingLoadResult<User> getUnmappedUsers(Integer studyId, PagingLoadConfig loadConfig) {
		return findAllUsersByPage(loadConfig, false, StudyDef.class, studyId);
    }

	@Override
    public PagingLoadResult<StudyDef> getMappedStudies(Integer userId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException {
		Search studySearch = getSearchFromLoadConfig(loadConfig, "name");
		studySearch.addFilterSome("users", Filter.equal("id", userId));
	    SearchResult<StudyDef> result = searchAndCount(studySearch);
	    return getPagingLoadResult(loadConfig, result);
    }

	@Override
    public PagingLoadResult<StudyDef> getUnmappedStudies(Integer userId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException {
		Search studySearch = getSearchFromLoadConfig(loadConfig, "name");
		studySearch.addFilterAll("users", Filter.notEqual("id", userId));
	    SearchResult<StudyDef> result = searchAndCount(studySearch);
	    return getPagingLoadResult(loadConfig, result);
    }
}
