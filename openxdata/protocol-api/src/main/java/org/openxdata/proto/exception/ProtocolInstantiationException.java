package org.openxdata.proto.exception;

/**
 * Represents an exceptional condition creating a protocol handler instance.
 * 
 * @author batkinson
 * 
 */
public class ProtocolInstantiationException extends ProtocolException {

	private static final long serialVersionUID = 5773980668835433727L;

	public ProtocolInstantiationException() {
		super();
	}

	public ProtocolInstantiationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProtocolInstantiationException(String message) {
		super(message);
	}

	public ProtocolInstantiationException(Throwable cause) {
		super(cause);
	}

}
