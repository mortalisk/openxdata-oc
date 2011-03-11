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
package org.openxdata.server.service.impl;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import org.openxdata.server.admin.model.User;
import org.openxdata.server.dao.UserDAO;
import org.openxdata.server.security.OpenXDataUserDetails;
import org.openxdata.server.security.OpenXdataUserDetailsService;
import org.openxdata.server.security.util.OpenXDataSecurityUtil;
import org.openxdata.server.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of the {@link AuthenticationService}
 * 
 * @author Angel
 *
 */
@Transactional
@Service("authenticationService")
public class AuthenticationServiceImpl implements AuthenticationService {
	
	@Autowired
	private UserDAO userDAO;

	@Autowired
	private OpenXdataUserDetailsService userDetailsService;
	
	@Override
	// no security - used for login
	public User authenticate(String username, String password) {
	       User user = null;
			if (isNotBlank(username) && isNotBlank(password)) {
				// login and password are not empty strings
				OpenXDataUserDetails userDetails = (OpenXDataUserDetails) userDetailsService.loadUserByUsername(username);
				if (userDetails != null) {
					// found the user, now checking the password etc
					user = userDetails.getOXDUser();
					
					if (isValidUserPassword(username, password)) {
						OpenXDataSecurityUtil.setSecurityContext(userDetails);
					} else {
						// password was invalid
						user = null;
				    }
				}
			}
			
			return user;
	}

	@Override
	// no security (for now - can be reviewed later)
	public Boolean isValidUserPassword(String username, String password) {
		User user = userDAO.getUser(username);
		
		String userPassword = user.getPassword();
		String salt = user.getSalt();
		if (isBlank(userPassword) || isBlank(salt)) {
			// We don't accept empty passwords
			return false;
		}

		// We decipher the password to ascertain if it matches the one in the database
		String hashedPassword = OpenXDataSecurityUtil.encodeString(password + salt);
		if(hashedPassword.equals(userPassword)){
			return true;
		} else {
			// try legacy method (for backward compatability)
			hashedPassword = OpenXDataSecurityUtil.encodeString2(password + salt);
			if(hashedPassword.equals(userPassword)){
					return true;
			}
		}
		return false;
	}

}
