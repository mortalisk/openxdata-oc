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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.openxdata.server.admin.model.Role;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.dao.RoleDAO;

/**
 * Wrapper class to allow bean binding of roles to User class
 * 
 * @author simon@cell-life.org
 */
public class UserWrapper {
	
	private User user;
	private List<String> errors;
	
	private static RoleDAO roleDAO;

	public UserWrapper() {
		this.user = new User();
		errors = new ArrayList<String>();
	}
	
	public static void setRoleDAO(RoleDAO roleDAO) {
		UserWrapper.roleDAO = roleDAO;
	}

	public void setName(String name) {
		user.setName(name);
	}

	public String getName() {
		return user.getName();
	}

	public String getFirstName() {
		return user.getFirstName();
	}

	public void setFirstName(String firstName) {
		user.setFirstName(firstName);
	}

	public String getLastName() {
		return user.getLastName();
	}

	public void setLastName(String lastName) {
		user.setLastName(lastName);
	}

	public String getMiddleName() {
		return user.getMiddleName();
	}

	public void setMiddleName(String middleName) {
		user.setMiddleName(middleName);
	}

	public String getPhoneNo() {
		return user.getPhoneNo();
	}

	public void setPhoneNo(String phoneNo) {
		user.setPhoneNo(phoneNo);
	}

	public String getClearTextPassword() {
		return user.getClearTextPassword();
	}

	public void setClearTextPassword(String clearTextPassword) {
		user.setClearTextPassword(clearTextPassword);
	}

	public String getEmail() {
		return user.getEmail();
	}

	public void setEmail(String email) {
		user.setEmail(email);
	}

	public void setCreator(User creator) {
		user.setCreator(creator);
	}

	public void setDateCreated(Date dateCreated) {
		user.setDateCreated(dateCreated);
	}

	public Date getDateCreated() {
		return user.getDateCreated();
	}

	public User getUser() {
		return user;
	}

    public void setRoles(String role) {
        if (role.trim().isEmpty()) {
            errors.add("No roles specified");
            return;
        }
        String[] roleArr = role.split(",");
        for (String roleName : roleArr) {
            List<Role> roles = roleDAO.getRolesByName(roleName.trim());
            if (roles.size() == 1) {
                user.addRole(roles.get(0));
            } else if (roles.size() > 1) {
                getErrors().add("More than one role matched role name: " + roleName);
            } else if (roles.isEmpty()) {
                getErrors().add("No role matched role name: " + roleName);
            }
        }
    }

	public String getRoles() {
		if (user.getRoles() != null) {
			List<Role> roles = new ArrayList<Role>();
			roles.addAll(user.getRoles());
			if (roles.size() > 1){
				// ensure order of roles is predictable to enable testing
				Collections.sort(roles, new Comparator<Role>() {
					@Override
					public int compare(Role o1, Role o2) {
						return o1.getName().compareTo(o2.getName());
					}
				});
			}
			StringBuffer out = new StringBuffer();
			for (Role role : roles) {
				out.append(role.getName());
				out.append(",");
			}
			String roleString = out.toString();
			return roleString.substring(0, roleString.length() - 1);
		}
		return "";
	}

	public List<String> getErrors() {
		return errors;
	}

	public boolean hasErrors() {
		if (user.getName().isEmpty()){
			errors.add("name is empty");
		}
		
		if (user.getClearTextPassword().isEmpty()){
			errors.add("clearTextPassword is empty");
		}
		return !errors.isEmpty();
	}
	
	public String getErrorString(){
		List<String> errors = getErrors();
		StringBuffer errorString = new StringBuffer();
		for (String error : errors) {
			errorString.append("(");
			errorString.append(error);
			errorString.append(") ");
		}
		return errorString.toString();
	}
}