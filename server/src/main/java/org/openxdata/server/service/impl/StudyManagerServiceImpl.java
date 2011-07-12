package org.openxdata.server.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDataHeader;
import org.openxdata.server.admin.model.FormDataVersion;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.admin.model.mapping.UserStudyMap;
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
	public void deleteForm(FormDef formDef) {
		formDAO.deleteForm(formDef);
	}

	@Override
	@Secured("Perm_Delete_Form_Data")
	public void deleteFormData(Integer formDataId){
		formDataDAO.deleteFormData(formDataId);
		// FIXME: needs to trigger exporter to delete too (and somehow update the history/version table)
	}

	@Override
	@Secured("Perm_Delete_Studies")
	public void deleteStudy(StudyDef studyDef) {
		studyDao.deleteStudy(studyDef);
	}

	@Override
	@Transactional(readOnly=true)
	@Secured("Perm_View_Studies")
	public List<StudyDef> getStudies() {		
		return studyDao.getStudies();
	}

	@Override
	@Secured("Perm_Add_Forms")
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
                formDefVersion.setXform(XformUtil.addFormId2Xform(formDefVersion.getFormDefVersionId(),
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
            map.addStudy(studyDef);
            map.addUser(OpenXDataSecurityUtil.getLoggedInUser());
            saveUserMappedStudy(map);
        }
    }

	@Override
	@Transactional(readOnly=true)
	@Secured("Perm_View_Form_Data")
	public List<FormDataHeader> getFormData(Integer formDefId, Integer userId, Date fromDate, Date toDate) {
		return editableDAO.getFormData(formDefId, userId, fromDate, toDate);
	}

	public List<FormDataVersion> getFormDataVersion(Integer formDataId) {
		return formDataDAO.getFormDataVersion(formDataId);
	}

	@Override
	@Transactional(readOnly=true)
	@Secured("Perm_View_Form_Data")
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
	@Transactional(readOnly=true)
	@Secured("Perm_View_Forms")
	public List<FormDef> getFormsForUser(User user) {
		return userFormMapDAO.getFormsForUser(user);
	}
	
	@Override
	@Transactional(readOnly=true)
	@Secured("Perm_View_Forms")
	public List<FormDef> getFormsForUser(User user, Integer studyDefId) {
		return userFormMapDAO.getFormsForUser(user, studyDefId);
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
	@Secured({"Perm_Add_Forms", "Perm_Add_Users"})
	public void saveUserMappedForm(UserFormMap map) {
		userFormMapDAO.saveUserMappedForm(map);
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
}
