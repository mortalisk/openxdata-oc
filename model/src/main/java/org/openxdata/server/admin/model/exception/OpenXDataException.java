package org.openxdata.server.admin.model.exception;

import java.io.Serializable;

/**
 * General class to handle OpenXData exceptions.
 * The idea is to enable serialization of custom <code>exceptions</code> to the client.
 * <P>
 * Most exceptions are not registered in the <code>GWT RPC policy</code> 
 * so will in most cases not be serialized to the client leading to the proverbial <code>"Call failed on server" </code>message.
 * </P>
 * This class wraps those exceptions and 
 * re throws them to the client as known exceptions with meaningful messages to the user.
 */
public class OpenXDataException  extends Exception implements Serializable   {

	/**
	 * Generated <code>serialization</code> ID.
	 */
	private static final long serialVersionUID = 1348971901401835418L;
	
	/**
	 * Default non arg constructor.
	 */
	public OpenXDataException(){}
	
	/**
	 * Constructor that takes an argument 
	 * which is the <code>String</code> 
	 * message to display when the exception is thrown.
	 * 
	 * @param message message to display when <code>exception</code> is thrown.
	 */
	public OpenXDataException(String message){
		super(message);
	}

	/**
	 * Constructor that takes an argument 
	 * which is an instance of <code>throwable</code>. 
	 * It is the exception that is thrown used to initialise this exception too.
	 * 
	 * @param throwable exception that is thrown.
	 */
	public OpenXDataException(Throwable throwable) {
	    super(throwable);
	}

	/**
	 * Constructs an instance of the <tt>Class</tt> with a custom message and the <tt>Exception.</tt>
	 * 
	 * @param message Custom message to accompany the exception.
	 * @param cause <tt>Exception</tt> that was thrown.
	 */
	public OpenXDataException(String message, Throwable cause) {
		super(message, cause);
	}
}
