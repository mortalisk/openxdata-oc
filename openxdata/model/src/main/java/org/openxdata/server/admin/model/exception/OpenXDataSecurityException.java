package org.openxdata.server.admin.model.exception;

/**
 * Class to handle Spring Security exceptions
 * Basically to wrap and serialize them to the client.
 */
public class OpenXDataSecurityException extends OpenXDataRuntimeException {

	/**
	 * Generated <code>serialization</code> ID.
	 */
	private static final long serialVersionUID = 792037043275797398L;
	
	/**
	 * Default non arg constructor.
	 */
	public OpenXDataSecurityException() {}

	/**
	 * Constructor that takes an argument which is an
	 * instance any unregistered security
	 * exception that might be thrown on the server.
	 * 
	 * @param throwable the unregistered <code>exception</code> thrown on the server.
	 * Recommended exception is the spring security access denied exception.
	 */
	public OpenXDataSecurityException(Throwable throwable) {
		super(throwable);
	}
	
	/**
	 * Constructor that takes an argument which is a 
	 * <code>String </code> message for the 
	 * instance of any exception that might be thrown on the server.
	 * 
	 * @param message the message of the unregistered exception thrown on the server.
	 * Recommended exception is the spring security access denied exception.
	 */
	public OpenXDataSecurityException(String message){
		super(message);
	}
}
