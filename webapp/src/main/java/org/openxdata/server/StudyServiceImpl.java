package org.openxdata.server;

import java.util.List;
import java.util.Map;

import org.openxdata.client.service.StudyService;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.mapping.UserStudyMap;
import org.openxdata.server.rpc.OxdPersistentRemoteService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class StudyServiceImpl extends OxdPersistentRemoteService implements StudyService {
	
	private static final long serialVersionUID = 4681223938185113228L;
	
	private org.openxdata.server.service.StudyManagerService studyService;

    @Override
    public List<StudyDef> getStudies() {
        return getStudyManagerService().getStudies();
    }
    
    @Override
    public Map<Integer, String> getStudyNamesForCurrentUser() {
    	return getStudyManagerService().getStudyNamesForCurrentUser();
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
    public List<UserStudyMap> getUserMappedStudies(Integer studyId) {
    	return getStudyManagerService().getUserMappedStudies(studyId);
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

	
    @Override
	public void setUserMappingForForm(FormDef form, List<User> users)
			throws OpenXDataSecurityException {
    	getStudyManagerService().setUserMappingForForm(form, users);
	}
    

	@Override
	public void setUserMappingForStudy(StudyDef study, List<User> users)
			throws OpenXDataSecurityException {
		
		getStudyManagerService().setUserMappingForStudy(study, users);
	}

	@Override
    public StudyDef getStudy(Integer studyId) throws OpenXDataSecurityException {
	    return getStudyManagerService().getStudy(studyId);
    }
}
