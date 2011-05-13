package org.openxdata.server.admin.model.exception;

/**
 * Exception that is thrown when the user's session has expired.
 * Indicates that the user should be redirected to a login page
 */
public class OpenXDataSessionExpiredException extends OpenXDataSecurityException  {
    
	/**
	 * Generated <code>serialization</code> ID.
	 */
    private static final long serialVersionUID = -4151708298744270279L;

    /**
     * Default non arg constructor.
     */
    public OpenXDataSessionExpiredException() {}
    
    /**
     * Constructor that takes an argument which is
	 * a <code>String </code> message for the instance of any 
	 * unregistered security exception that might be thrown on the server.
	 * 
	 * @param message the message of the unregistered exception thrown on the server.
     */
    public OpenXDataSessionExpiredException(String message) {
        super(message);
    }

	/**
	 * Constructor that takes an argument 
	 * which is an instance any unregistered 
	 * security exception that might be thrown on the server.
	 * 
	 * @param throwable the unregistered <code>exception</code> thrown on the server.
	 */
    public OpenXDataSessionExpiredException(Throwable throwable) {
        super(throwable);
    }
}
