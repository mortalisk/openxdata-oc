/*
 *  Licensed to the OpenXdata Foundation (OXDF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The OXDF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, 
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 *  Copyright 2010 http://www.openxdata.org.
 */
package org.openxdata.test;

import java.net.URL;


/**
 * Tests Utility class
 *
 * @author daniel
 * @author Angel
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
