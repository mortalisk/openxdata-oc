package org.openxdata.server.dao.hibernate;

import java.util.List;

import org.openxdata.server.admin.model.mapping.UserStudyMap;
import org.openxdata.server.dao.UserStudyMapDAO;
import org.springframework.stereotype.Repository;

/**
 * @author Angel
 *
 */
@Repository("userStudyMapDAO")
public class HibernateUserStudyMapDAO extends BaseDAOImpl<UserStudyMap> implements UserStudyMapDAO {

    @Override
    public List<UserStudyMap> getUserMappedStudies() {
        return findAll();
    }

    @Override
    public void saveUserMappedStudy(UserStudyMap map) {
        save(map);
    }

    @Override
    public void deleteUserMappedStudy(UserStudyMap map) {
        remove(map);
    }
}
