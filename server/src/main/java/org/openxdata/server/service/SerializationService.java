package org.openxdata.server.service;

import org.openxdata.server.serializer.StudySerializer;
import org.openxdata.server.serializer.UserSerializer;
import org.openxdata.server.serializer.XformSerializer;

/**
 * Handles the invoking of different serializers.
 * 
 *
 */
public interface SerializationService {

    /**
     * Gets the name of the class responsible for 
     * writing and reading forms to and from a stream respectively.
     * 
     * @param name the name of the setting that points to the class.
     * @return the class name including its full package name.
     */
	public XformSerializer getFormSerializer(String serializerName);

	/**
	 * Gets the name of the class responsible for 
	 * writing and reading users to and from a stream respectively.
     * 
	 * @param name the name of the setting that points to the class.
	 * @return the class name including its full package name.
	 */
	public UserSerializer getUserSerializer(String serializerName);

	/**
	 * Gets the name of the class responsible for 
	 * writing and reading studies to and from a stream respectively.
     *  
	 * @param name the name of the setting that points to the class.
	 * @return the class name including its full package name.
	 */
	public StudySerializer getStudySerializer(String serializerName);

}
