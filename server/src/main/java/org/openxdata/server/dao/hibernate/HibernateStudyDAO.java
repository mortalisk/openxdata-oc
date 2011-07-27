package org.openxdata.server.dao.hibernate;

import java.util.List;

import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.dao.StudyDAO;
import org.springframework.stereotype.Repository;

import com.googlecode.genericdao.search.Search;

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
}
