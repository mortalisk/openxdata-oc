package org.openxdata.proto.exception;

/**
 * Exception for the situation where the user does not have the necessary permissions
 * to carry out the requested operation.
 */
public class ProtocolAccessDeniedException extends ProtocolException {

	private static final long serialVersionUID = 5773980668835433727L;

	public ProtocolAccessDeniedException() {
		super();
	}

	public ProtocolAccessDeniedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProtocolAccessDeniedException(String message) {
		super(message);
	}

	public ProtocolAccessDeniedException(Throwable cause) {
		super(cause);
	}
}
