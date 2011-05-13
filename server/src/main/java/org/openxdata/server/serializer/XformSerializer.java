package org.openxdata.server.serializer;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface XformSerializer {

	/**
	 * Writes a list of xforms to a stream.
	 * 
	 * @param os the stream to write to.
	 * @param xforms the list of xforms.
	 */
	public void serializeForms(OutputStream os,List<String> xforms, Integer studyId, String studyName, String studyKey);

	/**
	 * Deserializes or reads form data from a stream into a list of xml xforms models. 
	 * 
	 * @param is the stream to read from.
	 * @param map of all form versions keyed by the form version id.
	 * @return a list of xform xml models as a List<String>.
	 */
	public abstract List<String> deSerialize(InputStream is, Map<Integer, String> map);
	
	/**
	 * Writes a message to the stream telling the opposite end that we processed
	 * the request successfully.
	 * 
	 * @param os the stream to write to
	 */
	public abstract void serializeSuccess(OutputStream os);

	/**
	 * Writes an access denied message to the stream. Access denied happens
	 * when the other end tries to connect with an invalid user name or password.
	 * 
	 * @param dos the stream to write to.
	 */
	public abstract void serializeAccessDenied(OutputStream dos);

	/**
	 * Writes a message to the stream which tells the other end that we failed
	 * to processes the request.
	 * 
	 * @param dos the stream to write to.
	 * @param ex the exception, if any, that was thrown during the processing.
	 */
	public abstract void serializeFailure(OutputStream dos, Exception ex);
}