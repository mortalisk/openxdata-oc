package org.openxdata.server.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserStudyMap;
import org.openxdata.server.dao.UserStudyMapDAO;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository("userStudyMapDAO")
public class HibernateUserStudyMapDAO extends BaseDAOImpl<UserStudyMap> implements UserStudyMapDAO {

    @Override
    public List<UserStudyMap> getUserMappedStudies() {
        return findAll();
    }
    
    @Override
    public List<UserStudyMap> getUserMappedStudies(Integer studyId) {
        return searchByPropertyEqual("studyId", studyId);
    }

    @Override
    public void saveUserMappedStudy(UserStudyMap map) {
        save(map);
    }

    @Override
    public void deleteUserMappedStudy(UserStudyMap map) {
        remove(map);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Map<Integer, String> getStudyNamesForUser(User user) {		
		Map<Integer, String> studyNames = new HashMap<Integer, String>();
    	Session session = getSession();
    	Query query = null;
    	if (user.hasAdministrativePrivileges()) {
    		query = session.createQuery(
					"select distinct sd.id, sd.name from StudyDef as sd");
    	} else {
			query = session.createQuery(
					"select distinct sd.id, sd.name from StudyDef as sd, User as u" +
					" where u.name = :name" +
					" and u in elements(sd.users)");
			query.setString("name", user.getName());
    	}
    	List<Object[]> result = query.list();
		for (Object[] obj : result) {
			studyNames.put((Integer)obj[0], (String)obj[1]);
		}
		return studyNames;
    }
}
