package org.openxdata.server;

import java.util.List;

import org.openxdata.client.service.StudyService;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.mapping.UserStudyMap;
import org.openxdata.server.rpc.OxdPersistentRemoteService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author victor
 */
public class StudyServiceImpl extends OxdPersistentRemoteService implements StudyService {
	
	private static final long serialVersionUID = 4681223938185113228L;
	
	private org.openxdata.server.service.StudyManagerService studyService;

    @Override
    public List<StudyDef> getStudies() {
        return getStudyManagerService().getStudies();
    }

    @Override
    public void saveStudy(StudyDef studyDef) {
        getStudyManagerService().saveStudy(studyDef);
    }

    @Override
    public void deleteStudy(StudyDef studyDef) {
        getStudyManagerService().deleteStudy(studyDef);
    }

    @Override
    public List<UserStudyMap> getUserMappedStudies() {
        return getStudyManagerService().getUserMappedStudies();
    }

    @Override
    public void saveUserMappedStudy(UserStudyMap userMappedStudy) {
        getStudyManagerService().saveUserMappedStudy(userMappedStudy);
    }

    @Override
    public void deleteUserMappedStudy(UserStudyMap userMappedStudy) {
        getStudyManagerService().deleteUserMappedStudy(userMappedStudy);
    }

    private org.openxdata.server.service.StudyManagerService getStudyManagerService() {
        if (studyService == null) {
            WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(this.getServletContext());
            studyService = (org.openxdata.server.service.StudyManagerService) ctx.getBean("studyManagerService");
        }
        return studyService;
    }
}
