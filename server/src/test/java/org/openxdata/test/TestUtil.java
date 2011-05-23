package org.openxdata.test;

import java.net.URL;


/**
 * Tests Utility class
 *
 * @author daniel
 *
 */
public class TestUtil {
	
	private static String TEST_FILE = "org/openxdata/server/util/TestFile.txt";
	private static String TEST_LOG_OUTPUT_FILE_PATH = "org/openxdata/server/util/openxdata-server-error.htm";

	/**
	 * Returns the path to the <tt>Test File.</tt>
	 * @return absolute path to the resource.
	 */
	public static String getTestFilePath(){
		URL url = TestUtil.class.getClassLoader().getResource(TEST_FILE);
		
		return url.getPath().replace("%20", " ");
	}
	
	/**
	 * Returns the path to the <tt>HTML Log File.</tt>
	 * @return absolute path to the resource.
	 */
	public static String getTestHTMLLogFilePath(){
		URL url = TestUtil.class.getClassLoader().getResource(TEST_LOG_OUTPUT_FILE_PATH);
		
		return url.getPath().replace("%20", " ");
	}

}
