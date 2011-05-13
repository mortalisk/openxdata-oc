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
 * @author batkinson
 * 
 */
public interface SubmissionContext {

	DataInputStream getInputStream();

	DataOutputStream getOutputStream();

	String getLocale();

	byte getAction();

	List<Object[]> getUsers();

	List<Object[]> getStudies();

	String getStudyName(int id);
	
	String getStudyKey(int studyId);

	List<String> getStudyForms(int studyId);

	Map<Integer, String> getXForms();

	void setUploadResult(List<String> formInstances);
	
	String setUploadResult(String formInstance);
}
