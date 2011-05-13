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
package org.openxdata.server.util;

import java.io.File;

import org.apache.log4j.Logger;
import org.openxdata.server.OpenXDataConstants;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.UnexpectedException;

/**
 * 
 * OpenXData utilities.
 * 
 * @author daniel
 * @author Angel
 *
 */
public class OpenXDataUtil {

	private static Logger log = Logger.getLogger(OpenXDataUtil.class);

    public static String getHomeFolder() {
        return System.getProperty("user.home") ;
    }

    public static boolean isUnix() {
        String seperator = System.getProperty("path.separator", "");
        return seperator.equals(":");
    }

    public static boolean isWindows() {
        String seperator = System.getProperty("path.separator", "");
        return seperator.equals(";");
    }

    private static String fileSeparator() {
        return File.separator;
    }

	/**
	 * Gets the application data directory of openxdata for the logged on user.
	 * 
     * @return The path to the directory on the file system that will hold miscellaneous
     * 			data about the application (runtime properties, modules, etc)
     */
    public static String getApplicationDataDirectory() {

        String filepath = null;

        if (isUnix()) {
            filepath = getHomeFolder() + fileSeparator() + ".openxdata";
        } else if(isWindows()) {
            filepath = getHomeFolder() + fileSeparator()
                    + "Application Data" + fileSeparator()
                    + "openxdata";
        } else {
            throw new UnexpectedException("Unknown operating system.");
        }

        filepath = filepath + fileSeparator();

        File folder = new File(filepath);
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                log.warn("'" + folder.getAbsolutePath() + "' has been successfully created.");
            } else {
                log.warn("'" + folder.getAbsolutePath() + "' has not been created.");
            }
        }

        return filepath;
    }

	/**
	 * Checks if the requirement of the administrator changing the password on initial login has been honored.
	 * 
	 * @param user - Administrator <code>User</code> to check
	 * @return <code>True Only and only if (!user.getPassword().equals(hashedPassword))</code>
	 */
	public static boolean checkIfUserChangedPassword(User user) {
		if(user.getPassword().equals(OpenXDataConstants.DEFAULT_ADMINISTRATOR_PASSWORD)){
			
			String message = 
				"For security reasons, the administrator should change their password on initial login. " +
				"Please change your password to avoid seeing this message and to protect your data.";
			
			log.info(message);
			
			return false;
		}
		else{
			return true;
		}
	}
}
