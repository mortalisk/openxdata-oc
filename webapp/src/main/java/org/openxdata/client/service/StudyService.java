/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.client.service;

import java.util.List;

import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.mapping.UserStudyMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 *
 * @author victor
 */
@RemoteServiceRelativePath("study")
public interface StudyService extends RemoteService {

    List<StudyDef> getStudies() throws OpenXDataSecurityException;

    void saveStudy(StudyDef studyDef) throws OpenXDataSecurityException;

    void deleteStudy(StudyDef studyDef) throws OpenXDataSecurityException;

    List<UserStudyMap> getUserMappedStudies() throws OpenXDataSecurityException;

    void saveUserMappedStudy(UserStudyMap userMappedStudy) throws OpenXDataSecurityException;

    void deleteUserMappedStudy(UserStudyMap userMappedStudy) throws OpenXDataSecurityException;
}
