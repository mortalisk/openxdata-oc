package org.openxdata.proto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;
import java.util.Map;

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

}
