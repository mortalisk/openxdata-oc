package org.openxdata.server;


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
	
	/** User login failed */
	public static final byte STATUS_ACCESS_DENIED = 2;
	
	/** Not permitted to carry out the requested operation. NOTE: clashes with MForms ResponseHeader!!!! */
	@Deprecated
	public static final byte STATUS_PERMISSION_DENIED = 3;
}
