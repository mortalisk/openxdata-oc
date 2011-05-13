package org.openxdata.server.serializer;

import java.io.OutputStream;

public interface UserSerializer {

	/**
	 * Writes a list of users to a stream.
	 * 
	 * @param os the stream to write to.
	 * @param data the user list.
	 */
	public abstract void serializeUsers(OutputStream os, Object data);

}