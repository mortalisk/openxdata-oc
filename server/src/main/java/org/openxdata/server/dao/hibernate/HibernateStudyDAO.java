package org.openxdata.server.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.StudyDefHeader;
import org.openxdata.server.admin.model.User;
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
	public StudyDef getStudy(String studyKey) {
		Search search = new Search(StudyDef.class);
		search.addFilterEqual("studyKey", studyKey);
		return searchUnique(search);
	}

	@Override
    public PagingLoadResult<StudyDef> getMappedStudies(Integer userId, PagingLoadConfig loadConfig) {
		Search studySearch = getSearchFromLoadConfig(loadConfig, "name");
		studySearch.addFilterSome("users", Filter.equal("id", userId));
	    SearchResult<StudyDef> result = searchAndCount(studySearch);
	    return getPagingLoadResult(loadConfig, result);
    }

	@Override
	public PagingLoadResult<StudyDefHeader> getMappedStudyNames(Integer userId, PagingLoadConfig loadConfig) {
		Search studySearch = getSearchFromLoadConfig(loadConfig, "name");
		studySearch.addFilterSome("users", Filter.equal("id", userId));
		SearchResult<StudyDef> result = searchAndCount(studySearch);
		return getStudyDefHeaderPagingLoadResult(loadConfig, result);
	}

	@Override
	public PagingLoadResult<StudyDefHeader> getUnmappedStudyNames(Integer userId, PagingLoadConfig loadConfig) {
		Search studySearch = getSearchFromLoadConfig(loadConfig, "name");
		studySearch.addFilterAll("users", Filter.notEqual("id", userId));
		SearchResult<StudyDef> result = searchAndCount(studySearch);
		return getStudyDefHeaderPagingLoadResult(loadConfig, result);
	}
	
	private PagingLoadResult<StudyDefHeader> getStudyDefHeaderPagingLoadResult(PagingLoadConfig loadConfig, SearchResult<StudyDef> searchResult) {
		List<StudyDef> list = searchResult.getResult();
		List<StudyDefHeader> headerList = new ArrayList<StudyDefHeader>();
		if (list != null) {
			for (StudyDef fd : list) {
				headerList.add(new StudyDefHeader(fd.getId(), fd.getName()));
			}
		}
		int totalNum = searchResult.getTotalCount();
		int offset = loadConfig == null ? 0 : loadConfig.getOffset();
		return new PagingLoadResult<StudyDefHeader>(headerList, offset, list.size(), totalNum);
	}
}
