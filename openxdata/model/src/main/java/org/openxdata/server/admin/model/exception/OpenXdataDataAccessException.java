package org.openxdata.server.admin.model.exception;

public class OpenXdataDataAccessException extends OpenXDataRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -862944117632477907L;
	
    /**
     * Default non arg constructor.
     */
    public OpenXdataDataAccessException() {
    	super("A Data Base Access Exception occurred.");
    }
    
    /**
     * Constructor that takes an argument which is
	 * a <code>String </code> message for the instance of any 
	 * unregistered security exception that might be thrown on the server.
	 * 
	 * @param message the message of the unregistered exception thrown on the server.
     */
    public OpenXdataDataAccessException(String message) {
        super(message);
    }
    
    public OpenXdataDataAccessException(Throwable throwable) {
    	super(throwable);
    }
}
