package org.openxdata.proto.exception;

/**
 * A base exception denoting that there was a problem while invoking the
 * protocol handler.
 * 
 * @author batkinson
 * 
 */
public class ProtocolException extends Exception {

	private static final long serialVersionUID = 6722671093883817735L;

	public ProtocolException() {
		super();
	}

	public ProtocolException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProtocolException(String message) {
		super(message);
	}

	public ProtocolException(Throwable cause) {
		super(cause);
	}
}
