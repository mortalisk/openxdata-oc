package org.openxdata.server.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDataHeader;
import org.openxdata.server.admin.model.FormDataVersion;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.admin.model.mapping.UserStudyMap;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;
import org.openxdata.server.dao.EditableDAO;
import org.openxdata.server.dao.FormDAO;
import org.openxdata.server.dao.FormDataDAO;
import org.openxdata.server.dao.StudyDAO;
import org.openxdata.server.dao.UserFormMapDAO;
import org.openxdata.server.dao.UserStudyMapDAO;
import org.openxdata.server.security.util.OpenXDataSecurityUtil;
import org.openxdata.server.service.StudyManagerService;
import org.openxdata.server.util.XformUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation for study manager service.
 * 
 * @author daniel
 *
 */
@Service("studyManagerService")
@Transactional
public class StudyManagerServiceImpl implements StudyManagerService {

	@Autowired
	private FormDAO formDAO;
	
	@Autowired
	private StudyDAO studyDao;	
	
	@Autowired
	private FormDataDAO formDataDAO;
	
	@Autowired
	private UserFormMapDAO userFormMapDAO;
	
	@Autowired
	private UserStudyMapDAO userStudyMapDAO;

	@Autowired
	private EditableDAO editableDAO;
	
	@Override
	@Secured("Perm_Delete_Forms")
	@Deprecated
	public void deleteForm(FormDef formDef) {
		userFormMapDAO.deleteUserMappedForms(formDef.getId());
		formDAO.deleteForm(formDef);
	}

	@Override
	@Secured("Perm_Delete_Form_Data")
	@Deprecated
	public void deleteFormData(Integer formDataId){
		formDataDAO.deleteFormData(formDataId);
		// FIXME: needs to trigger exporter to delete too (and somehow update the history/version table)
	}

	@Override
	@Secured("Perm_Delete_Studies")
	public void deleteStudy(StudyDef studyDef) {
		userStudyMapDAO.deleteUserMappedStudies(studyDef.getId());
		List<FormDef> forms = studyDef.getForms();
		if (forms != null) {
			for (FormDef formDef : forms) {
				userFormMapDAO.deleteUserMappedForms(formDef.getId());
			}
		}
		studyDao.deleteStudy(studyDef);
	}

	@Override
	@Transactional(readOnly=true)
	@Secured("Perm_View_Studies")
	public List<StudyDef> getStudies() {		
		return studyDao.getStudies();
	}
	
	@Override
	@Transactional(readOnly=true)
	@Secured("Perm_View_Studies")
	public Map<Integer, String> getStudyNamesForCurrentUser() {		
		return userStudyMapDAO.getStudyNamesForUser(OpenXDataSecurityUtil.getLoggedInUser());
	}
	
	@Override
	@Transactional(readOnly=true)
	@Secured("Perm_View_Studies")
	public StudyDef getStudy(Integer id) {		
		return studyDao.getStudy(id);
	}

	@Override
	@Secured("Perm_Add_Forms")
	@Deprecated
	public void saveForm(FormDef formDef) {
		formDAO.saveForm(formDef);
	}

	   @Override
    @Secured(value = "Perm_Add_Studies")
    public void saveStudy(StudyDef studyDef) {
        List<FormDefVersion> newVersions = new ArrayList<FormDefVersion>();

        //Get a list of new form versions
        if (studyDef.getForms() != null) {
            for (FormDef formDef : studyDef.getForms()) {
                if (formDef.getVersions() == null) {
                    continue;
                }

                for (FormDefVersion formDefVersion : formDef.getVersions()) {
                    if (formDefVersion.isNew()) {
                        newVersions.add(formDefVersion);
                    }
                }
            }
        }

        studyDao.saveStudy(studyDef);

        //Now set the xforms id attribute to the value of the saved form version id.
        for (FormDefVersion formDefVersion : newVersions) {
            if (formDefVersion.getXform() != null) {
                formDefVersion.setXform(XformUtil.addFormId2Xform(formDefVersion.getId(),
                        formDefVersion.getXform()));
            }
        }

        //Save the modified new form versions, if any
        //TODO This should only save the formDefVersion instead of the whole study.
        if (newVersions.size() > 0) {
            studyDao.saveStudy(studyDef);
        }
        //map this new study to the current user
        //am assuming a study that has no user has just been created
        //this avoid having to map a study every time it is saved
        if (!OpenXDataSecurityUtil.getLoggedInUser().hasAdministrativePrivileges()&& (studyDef.getUsers()== null)) {
            UserStudyMap map = new UserStudyMap();
            map.setStudy(studyDef);
            map.setUser(OpenXDataSecurityUtil.getLoggedInUser());
            userStudyMapDAO.saveUserMappedStudy(map);
        }
    }

	@Override
	@Transactional(readOnly=true)
	@Secured("Perm_View_Form_Data")
	@Deprecated
	public List<FormDataHeader> getFormData(Integer formDefId, Integer userId, Date fromDate, Date toDate) {
		return editableDAO.getFormData(formDefId, userId, fromDate, toDate);
	}

	@Deprecated
	public List<FormDataVersion> getFormDataVersion(Integer formDataId) {
		return formDataDAO.getFormDataVersion(formDataId);
	}

	@Override
	@Transactional(readOnly=true)
	@Secured("Perm_View_Form_Data")
	@Deprecated
	public FormData getFormData(Integer formDataId) {
		return formDataDAO.getFormData(formDataId);
	}

	@Override
	@Transactional(readOnly=true)
	@Secured("Perm_View_Form_Data")
	public Boolean hasEditableData(Editable item) {
		return editableDAO.hasEditableData(item);
	}
	
	@Override
	@Secured({"Perm_View_Studies", "Perm_View_Users"})
	public List<UserStudyMap> getUserMappedStudies() {
		return userStudyMapDAO.getUserMappedStudies();
	}
	
	@Override
	@Secured({"Perm_View_Studies", "Perm_View_Users"})
	public List<UserStudyMap> getUserMappedStudies(Integer studyId) {
		return userStudyMapDAO.getUserMappedStudies(studyId);
	}
	
	@Override
	@Secured({"Perm_Add_Studies", "Perm_Add_Users"})
	public void saveUserMappedStudy(UserStudyMap map) {
		userStudyMapDAO.saveUserMappedStudy(map);
	}
	
	@Override
	@Secured({"Perm_Delete_Studies", "Perm_Delete_Users"})
	public void deleteUserMappedStudy(UserStudyMap map) {
		userStudyMapDAO.deleteUserMappedStudy(map);
	}
	
	@Override
	@Secured({"Perm_Add_Forms", "Perm_Add_Users"})
	@Deprecated
	public void saveUserMappedForm(UserFormMap map) {
		userFormMapDAO.saveUserMappedForm(map);
	}

	@Override
	@Secured({"Perm_Delete_Forms", "Perm_Delete_Users"})
	public void deleteUserMappedForm(UserFormMap map) {
		userFormMapDAO.deleteUserMappedForm(map);
	}
	
	@Override
	@Transactional(readOnly=true)
	@Secured({"Perm_View_Forms", "Perm_View_Users"})
	public List<UserFormMap> getUserMappedForms() {
		return userFormMapDAO.getUserMappedForms();
	}
	
	@Override
	@Secured("Perm_View_Studies")
	public String getStudyKey(int studyId) {
		String key = studyDao.getStudyKey(studyId);
		if (key == null)
			key = "";
		return key;
	}

	@Override
	@Secured("Perm_View_Studies")
	public String getStudyName(int studyId) {
		String name = studyDao.getStudyName(studyId);
		if(name == null)
                name = "UNKNOWN STUDY";
        return name;
	}
	
	@Override
	@Secured("Perm_View_Studies")
	public List<StudyDef> getStudyByName(String studyName) {
		return studyDao.searchByPropertyEqual("name", studyName);
	}

	@Override
	@Secured("Perm_View_Forms")
	@Deprecated
	public List<FormDef> getFormByName(String formName) {
		return formDAO.searchByPropertyEqual("name", formName);
	}

	@Override
	@Secured({"Perm_View_Studies", "Perm_View_Users"})
    public PagingLoadResult<User> getMappedUsers(Integer studyId, PagingLoadConfig loadConfig) {
	    return studyDao.getMappedUsers(studyId, loadConfig);
    }

	@Override
	@Secured({"Perm_View_Studies", "Perm_View_Users"})
    public PagingLoadResult<User> getUnmappedUsers(Integer studyId, PagingLoadConfig loadConfig) {
	    return studyDao.getUnmappedUsers(studyId, loadConfig);
    }

	@Override
	@Secured({"Perm_Add_Users", "Perm_Add_Studies"})
    public void saveMappedStudyUsers(Integer studyId, List<User> usersToAdd, List<User> usersToDelete) {
		if (usersToAdd != null) {
			for (User u : usersToAdd) {
				UserStudyMap map = new UserStudyMap(u.getId(), studyId);
				userStudyMapDAO.saveUserMappedStudy(map);
			}
		}
		if (usersToDelete != null) {
			for (User u : usersToDelete) {
				UserStudyMap map = userStudyMapDAO.getUserStudyMap(u.getId(), studyId);
				userStudyMapDAO.deleteUserMappedStudy(map);
			}
		}
    }

	@Override
	@Secured({"Perm_View_Studies", "Perm_View_Users"})
    public PagingLoadResult<StudyDef> getMappedStudies(Integer userId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException {
		return studyDao.getMappedStudies(userId, loadConfig);
    }

	@Override
	@Secured({"Perm_View_Studies", "Perm_View_Users"})
    public PagingLoadResult<StudyDef> getUnmappedStudies(Integer userId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException {
	    return studyDao.getUnmappedStudies(userId, loadConfig);
    }

	@Override
	@Secured({"Perm_Add_Users", "Perm_Add_Studies"})
    public void saveMappedUserStudies(Integer userId, List<StudyDef> studiesToAdd, List<StudyDef> studiesToDelete)
            throws OpenXDataSecurityException {
	    if (studiesToAdd != null) {
		    for (StudyDef sd : studiesToAdd) {
		    	UserStudyMap map = new UserStudyMap(userId, sd.getId());
				userStudyMapDAO.saveUserMappedStudy(map);
		    }
	    }
	    if (studiesToDelete != null) {
		    for (StudyDef sd : studiesToDelete) {
		    	UserStudyMap map = userStudyMapDAO.getUserStudyMap(userId, sd.getId());
				userStudyMapDAO.deleteUserMappedStudy(map);
		    }
	    }
    }

	@Override
	public StudyDef getStudy(String studyKey) {
		return studyDao.getStudy(studyKey);
	}
}
