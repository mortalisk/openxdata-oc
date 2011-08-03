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

import java.util.List;

import org.openxdata.server.admin.model.User;

/**
 * Wrapper class to allow bean binding of roles to User class
 * 
 * @author simon@cell-life.org
 */
public class UserImportBean {
	
	private String name;
	private String firstName;
	private String middleName;
	private String lastName;
	private String phoneNo;
	private String email;
	private String roles;
	private String clearTextPassword;
	private String formPermissions;
	private String studyPermissions;
	private List<String> errors;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public String getClearTextPassword() {
		return clearTextPassword;
	}
	public void setClearTextPassword(String clearTextPassword) {
		this.clearTextPassword = clearTextPassword;
	}
	public String getFormPermissions() {
		return formPermissions;
	}
	public void setFormPermissions(String formPermissions) {
		this.formPermissions = formPermissions;
	}
	public String getStudyPermissions() {
		return studyPermissions;
	}
	public void setStudyPermissions(String studyPermissions) {
		this.studyPermissions = studyPermissions;
	}
	
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public User getUser(){
		User u = new User();
		u.setName(name);
		u.setFirstName(firstName);
		u.setMiddleName(middleName);
		u.setLastName(lastName);
		u.setPhoneNo(phoneNo);
		u.setEmail(email);
		u.setClearTextPassword(clearTextPassword);
		return u;
	}
	
	public static String[] getColumnHeaders() {
		return new String[] { "name", "firstName", "middleName", "lastName",
				"phoneNo", "email", "clearTextPassword", "roles", "formPermissions",
				"studyPermissions",	"error messages" };
	}

	public String[] toStringArray() {
		return new String[] { getName(), getFirstName(),
				getMiddleName(), getLastName(), getPhoneNo(),
				getEmail(), getClearTextPassword(), getRoles(),
				getFormPermissions(), getStudyPermissions(),
				getErrorString() };
	}
	
	private String getErrorString(){
		if (errors == null){
			return "";
		}
		
		StringBuffer errorString = new StringBuffer();
		for (String error : errors) {
			errorString.append("(");
			errorString.append(error);
			errorString.append(") ");
		}
		return errorString.toString();
	}
	@Override
	public String toString() {
		return "UserImportBean [name=" + name + ", firstName=" + firstName
				+ ", middleName=" + middleName + ", lastName=" + lastName
				+ ", phoneNo=" + phoneNo + ", email=" + email + ", roles="
				+ roles + ", clearTextPassword=" + clearTextPassword
				+ ", formPermissions=" + formPermissions
				+ ", studyPermissions=" + studyPermissions + ", errors="
				+ errors + "]";
	}
	
}