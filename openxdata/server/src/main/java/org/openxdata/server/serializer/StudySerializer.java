package org.openxdata.server.serializer;

import java.io.OutputStream;

public interface StudySerializer {

	/**
	 * Writes a list of studies to a stream.
	 * 
	 * @param os the stream to write to.
	 * @param data the study list.
	 */
	public abstract void serializeStudies(OutputStream os, Object data);

}