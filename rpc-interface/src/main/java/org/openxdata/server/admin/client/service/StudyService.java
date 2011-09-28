package org.openxdata.server.admin.client.service;

import java.util.List;
import java.util.Map;

import org.openxdata.server.admin.model.OpenclinicaStudy;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.mapping.UserStudyMap;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("study")
public interface StudyService extends RemoteService {

    List<StudyDef> getStudies() throws OpenXDataSecurityException;
    
    StudyDef getStudy(Integer studyId) throws OpenXDataSecurityException;
    
    public Map<Integer, String> getStudyNamesForCurrentUser() throws OpenXDataSecurityException;

    StudyDef saveStudy(StudyDef studyDef) throws OpenXDataSecurityException;

    void deleteStudy(StudyDef studyDef) throws OpenXDataSecurityException;
    
    void saveUserMappedStudy(UserStudyMap userMappedStudy) throws OpenXDataSecurityException;

    void deleteUserMappedStudy(UserStudyMap userMappedStudy) throws OpenXDataSecurityException;
    
	/**
	 * Get a page of Users mapped to a specific study 
	 * @param studyId Integer id of specified study
	 * @param loadConfig PagingLoadConfig specifying page size and number
	 * @return
	 */
	PagingLoadResult<User> getMappedUsers(Integer studyId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException;
	
	/**
	 * Get a page of Users NOT mapped to the specified study
	 * @param studyId Integer id of specified study
	 * @param loadConfig PagingLoadConfig specifying page size and number
	 * @return
	 */
	PagingLoadResult<User> getUnmappedUsers(Integer studyId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException;

	/**
	 * Updates the users currently mapped to the specified study.
	 * @param studyId Integer id of specified study
	 * @param usersToAdd List of users to add to the study mapping
	 * @param usersToDelete List of users to delete from the study mapping
	 * @throws OpenXDataSecurityException
	 */
	void saveMappedStudyUsers(Integer studyId, List<User> usersToAdd, List<User> usersToDelete) throws OpenXDataSecurityException;
	
	List<OpenclinicaStudy> getOpenClinicaStudies();
	
	String importOpenClinicaStudy(String identifier);
	
	Boolean hasStudyData(String studyKey);
	
	void exportOpenclinicaStudyData(String studyKey);
}
