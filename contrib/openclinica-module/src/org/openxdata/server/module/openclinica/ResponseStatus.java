package org.openxdata.server.module.openclinica;


/**
 * Contains the header of a mobile connection response.
 * 
 * @author Daniel
 *
 */
public class ResponseStatus {
	
	/** Problems occured during execution of the request. */
	public static final byte STATUS_ERROR = 0;
	
	/** Request completed successfully. */
	public static final byte STATUS_SUCCESS = 1;
	
	/** Not permitted to carry out the requested operation. */
	public static final byte STATUS_ACCESS_DENIED = 2;
}
