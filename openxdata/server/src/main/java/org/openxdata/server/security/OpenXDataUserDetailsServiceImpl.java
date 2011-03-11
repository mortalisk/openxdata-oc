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
package org.openxdata.server.security;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.Role;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.dao.UserDAO;
import org.openxdata.server.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

/**
 * <tt>OpenXData UserDetailsService</tt> - used by 
 * spring security to retrieve user details and authenticate.
 * <p>
 * Note: the <tt>StudyManagerService.authenticate method</tt>
 * is bypassed because Spring Security implements "remember me" 
 * functionality that requires the retrieval of <tt>User</tt> details without a password.
 * </p>
 * 
 * @author dagmar@cell-life.org.za
 * @author Mark
 */
@Transactional (readOnly=true)
public class OpenXDataUserDetailsServiceImpl implements OpenXdataUserDetailsService {
    
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private RoleService roleService;

	/** Logger for this class.*/
	private Logger log = Logger.getLogger(this.getClass());    
	
	/**
	 * Constructs a <tt>User</tt> with appropriate security context details given a name.
	 * 
	 * @param username User name for the <tt>User</tt> we attempting to get.
	 * 
	 * @throws UsernameNotFoundException If Name is not existent in the database.
	 * @throws DataAccessException For any <tt>Data Access Layer Exception.</tt>
	 * 
	 */
    @Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {

		User user = userDAO.getUser(username);
		OpenXDataUserDetails userDetails = getUserDetailsForUser(user);

		return userDetails;
    }

	@Override
	public OpenXDataUserDetails getUserDetailsForUser(User user) {
		OpenXDataUserDetails userDetails = null;
		if (user != null) {
			// Object to hold User permissions.
			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			if (user.hasAdministrativePrivileges()) {

				// Assign all permissions to Admin
				assignAllPermissionsToAdministrator(authorities);
			} else {

				// Extract User permission for spring to use.
				extractUserPermissions(user, authorities);
			}

			// Construct a User Details Object for Spring Security Context.
			userDetails  = new OpenXDataUserDetails(user, true, true, true, true, authorities
					.toArray(new GrantedAuthority[authorities.size()]));
		}
		return userDetails;
	}

	/**
	 * Extracts <tt>User Permissions</tt> and converts them to 
	 * authorities that can be leveraged by <tt>Spring Security.</tt>
	 * 
	 * @param user <tt>User</tt> attempting to get authenticated.
	 * @param authorities <tt>Spring Security authorities object.</tt>
	 */
	private void extractUserPermissions(User user, List<GrantedAuthority> authorities) {
		if(user.getRoles() == null)
			return; //Some how i got problems with a user who had no roles.
		
		for (Role r : user.getRoles()) {
			List<Permission> permissions = r.getPermissions();
		    for (Permission p : permissions) {
		    	
		    	// Add User permissions to the authorities for Spring.
		        GrantedAuthority ga = new GrantedAuthorityImpl(p.getName());
		        log.debug("User: "+user.getName()+", GrantedAuthority: "+p.getName());
		        authorities.add(ga);
		    }
		}
	}

	/**
	 * Extracts <tt>administrative User</tt> all <tt>Permissions.</tt>
	 * 
	 * @param authorities <tt>Spring Security authorities object.</tt>
	 */
	private void assignAllPermissionsToAdministrator(List<GrantedAuthority> authorities) {
		List<Permission> permissions = null;            		
		permissions = roleService.getPermissions();
		
		if(permissions != null && permissions.size() > 0){
			for(Permission p : permissions){
				
				// Add User permissions to the authorities for Spring.
				GrantedAuthority ga = new GrantedAuthorityImpl(p.getName());
		        authorities.add(ga);
			}
		}
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}
}