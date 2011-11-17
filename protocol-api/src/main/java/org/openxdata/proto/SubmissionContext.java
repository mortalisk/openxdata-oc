package org.openxdata.proto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;
import java.util.Map;

import org.openxdata.proto.exception.ProtocolAccessDeniedException;
import org.openxdata.proto.exception.ProtocolInvalidSessionReferenceException;

/**
 * An abstraction of the contextual information necessary to satisfy a
 * submission request. Essentially, this is needed to insulate the protocol
 * handlers from the specifics of the server and vice versa.
 * 
 * NB: To avoid breaking backwards compatibility, please do not change
 * or remove methods from this interface.
 */
public interface SubmissionContext {

	/**
	 * Gets the current data input stream, used to read request data
	 * 
	 * @return DataInputStream
	 */
	DataInputStream getInputStream();

	/**
	 * Gets the current data output stream, used to write response data
	 * 
	 * @return DataOutputStream
	 */
	DataOutputStream getOutputStream();

	/**
	 * Gets the user's locale (used for internationalization)
	 * 
	 * @return String locale identifier (e.g. "en")
	 */
	String getLocale();

	/**
	 * Gets the current action
	 * 
	 * @return byte indicating the action
	 */
	byte getAction();

	/**
	 * Gets a list of all the Users in the system
	 *
	 * @return List of Object[] { Integer userId, String name, String encodedPassword, String salt }
	 */
	List<Object[]> getUsers();

	/**
	 * Gets a list of all the studies the user has access to
	 *
	 * @return List of Object[] { Integer study identifier, String study name}
	 */
	List<Object[]> getStudies();

	/**
	 * Retrieves the name of the study
	 * 
	 * @param id int study definition identifier
	 * @return String study name
	 */
	String getStudyName(int id);
	
	/**
	 * Gets the key for the study (FIXME: for what this is used?)
	 * 
	 * @param studyId int study definition identifier
	 * @return String study key
	 */
	String getStudyKey(int studyId);

	/**
	 * Gets a list of the xforms, that the user has access to, for the specified study
	 * @param studyId int study definition identifier
	 * @return List of Strings containing xforms
	 */
	List<String> getStudyForms(int studyId);

	/**
	 * Get a list of the xforms that the user has access to
	 * 
	 * @return Map of xforms using form definition version id as the key
	 */
	Map<Integer, String> getXForms();

	/**
	 * Bulk save of form data
	 * 
	 * @param formInstances List of String XML containing captured form data
	 */
	void setUploadResult(List<String> formInstances);
	
	/**
	 * Saves the form data and returns the record identifier
	 *
	 * @param formInstance String XML containing the captured form data
	 * @return String containing a session reference
	 */
	String setUploadResult(String formInstance);

	/**
	 * Saves the form data and returns the record identifier
	 *
	 * @param formInstanceId Integer identifier of the form data (can be null if new data)
	 * @param formInstance String XML containing the captured form data
	 * @return String containing a session reference
	 */
	String setUploadResult(Integer formInstanceId, String formInstance);
	
	/**
	 * Retrieves the specified Form Definition Version
	 * @param formDefId
	 * @return String xform
	 */
	String getXForm(int formDefId);
	
	/**
	 * Retrieves a specific Form Data, usually for editing purposes
	 * Extra checking:
	 *  1. user must have extra edit permission if the data was not submitted by them.
	 *  2. form data must be for the specified form definition
	 *
	 * @param formDefId int identifier of the form definition
	 * @param formDataId int identifier of the form data to retrieve
	 * @return String containing XML form data
	 */
	String getFormInstance(int formDefId, int formDataId) throws ProtocolInvalidSessionReferenceException, ProtocolAccessDeniedException;
}
