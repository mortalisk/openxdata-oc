package org.openxdata.proto.exception;

/**
 * Represents an exception condition loading a protocol.
 * 
 * @author batkinson
 * 
 */
public class ProtocolNotFoundException extends ProtocolException {

	private static final long serialVersionUID = 6245232564216810907L;

	public ProtocolNotFoundException() {
		super();
	}

	public ProtocolNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProtocolNotFoundException(String message) {
		super(message);
	}

	public ProtocolNotFoundException(Throwable cause) {
		super(cause);
	}

}
