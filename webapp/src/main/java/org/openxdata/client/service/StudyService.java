package org.openxdata.client.service;

import java.util.List;

import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.mapping.UserStudyMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("study")
public interface StudyService extends RemoteService {

    List<StudyDef> getStudies() throws OpenXDataSecurityException;

    void saveStudy(StudyDef studyDef) throws OpenXDataSecurityException;

    void deleteStudy(StudyDef studyDef) throws OpenXDataSecurityException;

    List<UserStudyMap> getUserMappedStudies() throws OpenXDataSecurityException;

    void saveUserMappedStudy(UserStudyMap userMappedStudy) throws OpenXDataSecurityException;

    void deleteUserMappedStudy(UserStudyMap userMappedStudy) throws OpenXDataSecurityException;
    
    /**
     * Sets the Users who have permissions to access a given form. Note that existing permissions will be overridden.
     * 
     * @param form Form to restrict access for.
     * @param users Definite list of users who will have access to the form.
     * @throws OpenXDataSecurityException If User does not have permission to map objects.
     */
    void setUserMappingForForm(FormDef form, List<User> users) throws OpenXDataSecurityException;
    
    /**
     * Sets the Users who have permissions to access a given Study. Note that existing permissions will be overridden.
     * 
     * @param study Study to restrict access for.
     * @param users Definite list of users who will have access to the form.
     * @throws OpenXDataSecurityException If User does not have permission to map objects.
     */
    void setUserMappingForStudy(StudyDef study, List<User> users) throws OpenXDataSecurityException;
}
