package org.openxdata.server.admin.client.service;

import java.util.List;
import java.util.Map;

import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.StudyDefHeader;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
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
	
	/**
	 * Get a page of Studies mapped to a specific User 
	 * @param userId Integer id of specified user
	 * @param loadConfig PagingLoadConfig specifying page size and number
	 * @return
	 */
	PagingLoadResult<StudyDef> getMappedStudies(Integer userId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException;

	/**
	 * Get a page of Studies mapped to a specific User 
	 * @param userId Integer id of specified user
	 * @param loadConfig PagingLoadConfig specifying page size and number
	 * @return
	 */
	PagingLoadResult<StudyDefHeader> getMappedStudyNames(Integer userId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException;

	/**
	 * Get a page of Studies NOT mapped to the specified User
	 * @param userId Integer id of specified user
	 * @param loadConfig PagingLoadConfig specifying page size and number
	 * @return
	 */
	PagingLoadResult<StudyDefHeader> getUnmappedStudyNames(Integer userId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException;
	
	/**
	 * Updates the studies currently mapped to the specified user.
	 * @param userId Integer id of specified user
	 * @param studiesToAdd List of studies to add to the user's access
	 * @param studiesToDelete List of studies to delete from the user's access
	 * @throws OpenXDataSecurityException
	 */
	void saveMappedUserStudyNames(Integer userId, List<StudyDefHeader> studiesToAdd, List<StudyDefHeader> studiesToDelete) throws OpenXDataSecurityException;
}
