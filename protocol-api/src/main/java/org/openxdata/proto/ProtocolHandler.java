package org.openxdata.proto;

import org.openxdata.proto.exception.ProtocolException;

/**
 * An interface defining the necessary contract for implementing an upload
 * protocol handler.
 * 
 * @author batkinson
 */
public interface ProtocolHandler {

	/**
	 * Handles an upload request.
	 * 
	 * @param in
	 *            a connected input stream to read the upload from
	 * @param out
	 *            a connected output stream to write the response to
	 * @throws ProtocolException
	 */
	void handleRequest(SubmissionContext ctx) throws ProtocolException;

}