package org.openxdata.server.admin.model.exception;

public class UnexpectedException extends OpenXDataRuntimeException {

	private static final long serialVersionUID = 3287233488187814349L;

	public UnexpectedException() {
	}

	public UnexpectedException(String message) {
		super(message);
	}

	public UnexpectedException(Throwable throwable) {
		super(throwable);
	}

	public UnexpectedException(String message, Throwable cause) {
		super(message, cause);
	}

}
