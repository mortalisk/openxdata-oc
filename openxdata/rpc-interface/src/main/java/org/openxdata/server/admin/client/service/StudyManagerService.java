package org.openxdata.server.admin.client.service;

import java.util.Date;
import java.util.List;

import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDataHeader;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.admin.model.mapping.UserStudyMap;

import com.google.gwt.user.client.rpc.RemoteService;


/**
 * Defines the client side contract for the Study Manager Service.
 */
public interface StudyManagerService extends RemoteService{
	
	/**
	 * Fetches all the <tt>Studies</tt> in the system.
	 * 
	 * @return <tt>List</tt> of <tt>Studies.</tt>
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	List<StudyDef> getStudies() throws OpenXDataSecurityException;
	
	/**
	 * Saves a dirty or new <tt>Study.</tt>
	 * 
	 * @param studyDef <tt>Study</tt> to save.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void saveStudy(StudyDef studyDef) throws OpenXDataSecurityException;
	
	/**
	 * Deletes a given <tt>Study.</tt>
	 * 
	 * @param studyDef <tt>Study</tt> to delete.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void deleteStudy(StudyDef studyDef) throws OpenXDataSecurityException;
	
	/**
	 * Saves a dirty or new <tt>Form.</tt>
	 * 
	 * @param formDef <tt>Form</tt> to save.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void saveForm(FormDef formDef) throws OpenXDataSecurityException;
	
	/**
	 * Deletes a given <tt>Form.</tt>
	 * 
	 * @param formDef <tt>Form</tt> to save.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void deleteForm(FormDef formDef) throws OpenXDataSecurityException;
	
	/**
	 * Deletes a given <tt>Form's data.</tt>
	 * 
	 * @param formDataId ID of data to delete.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void deleteFormData(Integer formDataId) throws OpenXDataSecurityException;
	
	/**
	 * Fetches a given <tt>Form's data.</tt>
	 * 
	 * @param formDataId ID of the <tt>Form Data</tt> to fetch.
	 * @return <tt>Form's Form Data.</tt>
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	FormData getFormData(Integer formDataId) throws OpenXDataSecurityException;
	
	/** 
	 * Checks if a given <tt>Editable</tt> has data. 
	 *  
	 * @param editable <tt>Editable</tt> to check data for. 
	 * @return <tt>True if has data else false.</tt> 
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt> 
	 */ 
	Boolean hasEditableData(Editable editable) throws OpenXDataSecurityException, OpenXDataSecurityException; 
	
	/**
	 * Retrieves all the FormData matching the parameters
	 * 
	 * @param formDefId Integer id of a specific form
	 * @param userId Integer id of a specific user
	 * @param fromDate Date start range for submission date
	 * @param toDate Date end range for submission date
	 * @return
	 * @throws OpenXDataSecurityException
	 */
	List<FormDataHeader> getFormData(Integer formDefId, Integer userId, Date fromDate,  Date toDate) throws OpenXDataSecurityException;
	
	/**
	 * Fetches all the <tt>User Mapped Studies</tt> in the system.
	 * 
	 * @return <tt>List</tt> of <tt>User Mapped Studies.</tt>
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	List<UserStudyMap> getUserMappedStudies() throws OpenXDataSecurityException;
	
	/**
	 * Saves a dirty or new <tt>User Mapped Study.</tt>
	 * 
	 * @param userMappedStudy <tt>User Mapped Study</tt> to save.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void saveUserMappedStudy(UserStudyMap userMappedStudy) throws OpenXDataSecurityException;
	
	/**
	 * Deletes a given <tt>User Mapped Study.</tt>
	 * 
	 * @param userMappedStudy <tt>User Mapped Study</tt> to delete.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void deleteUserMappedStudy(UserStudyMap userMappedStudy) throws OpenXDataSecurityException;
	
	/**
	 * Fetches all the <tt>User Mapped Forms</tt> in the system.
	 * 
	 * @return <tt>List</tt> of <tt>User Mapped Forms.</tt>
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	List<UserFormMap> getUserMappedForms() throws OpenXDataSecurityException;
	
	/**
	 * Saves a dirty or new <tt>User Mapped Form.</tt>
	 * 
	 * @param userMappedForm <tt>User Mapped Form</tt> to save.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void saveUserMappedForm(UserFormMap userMappedForm) throws OpenXDataSecurityException;
	
	/**
	 * Deletes a given <tt>User Mapped Form.</tt>
	 * 
	 * @param userMappedForm <tt>User Mapped Form</tt> to delete.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void deleteUserMappedForm(UserFormMap userMappedForm) throws OpenXDataSecurityException;

}
