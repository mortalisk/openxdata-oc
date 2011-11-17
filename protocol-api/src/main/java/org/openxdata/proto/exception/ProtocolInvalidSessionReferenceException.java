package org.openxdata.proto.exception;

/**
 * Exception for the situation where the session reference provided
 * by the user (to download data) is not valid
 */
public class ProtocolInvalidSessionReferenceException extends ProtocolException {

	private static final long serialVersionUID = 5773980668835433727L;

	public ProtocolInvalidSessionReferenceException() {
		super();
	}

	public ProtocolInvalidSessionReferenceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProtocolInvalidSessionReferenceException(String message) {
		super(message);
	}

	public ProtocolInvalidSessionReferenceException(Throwable cause) {
		super(cause);
	}
}
