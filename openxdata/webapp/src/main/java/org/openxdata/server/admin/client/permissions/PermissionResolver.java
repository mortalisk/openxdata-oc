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
package org.openxdata.server.admin.client.permissions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.admin.model.mapping.UserReportGroupMap;
import org.openxdata.server.admin.model.mapping.UserReportMap;
import org.openxdata.server.admin.model.mapping.UserStudyMap;

/**
 * Holds the <tt>Permissions</tt> for the currently logged on <tt>User.</tt>
 * 
 * <p>
 * Resolving utility <tt>class</tt> that organizes the <tt>Permissions</tt> 
 * of the <tt>User</tt> and checks when a restricted resource or operation is invoked 
 * and consequently determines the access of level of the <tt>User</tt> depending on their <tt>Permissions.</tt>
 * </p>
 * 
 * @author Angel
 *
 */
public class PermissionResolver {	
	
	/** List of all <code>User Permissions.</code>*/
	private Set<Permission> allPermissions;
	
	/** List of <code>User Permissions</code> not classified in Add, Edit, Delete or View*/
	private List<Permission> extraPermissions = new ArrayList<Permission>();
	
    /**
     * List to be initialized with add permissions
     */
	private List<Permission> addPermissions = new ArrayList<Permission>();
	
	/**
	 * List to be initialized with edit permissions
	 */
	private List<Permission> editPermissions = new ArrayList<Permission>();
	
	/**
	 * List to be initialized with view permissions
	 */
	private List<Permission> viewPermissions = new ArrayList<Permission>();
	
	/**
	 * List to be initialized with delete permissions
	 */
	private List<Permission> deletePermissions = new ArrayList<Permission>();
	
	/** indicates if the current <tt>User</tt> has administrative privileges. */
	private boolean isAdmin = false;

	/**
	 * Global boolean variable indicating that the currently logged user can add studies
	 * and child items (Forms and Form versions)
	 */
	private boolean addStudies = false;
	
	/**
	 * Global boolean variable indicating that the currently logged on user can add Users
	 */
	private boolean addUsers = false;
	
	/**
	 * Global boolean variable indicating that the currently logged on user can add Roles
	 */
	private boolean addRoles = false;
	
	/**
	 * Global boolean variable indicating that the currently logged on user can add Tasks
	 */
	private boolean addTasks = false;
	
	/**
	 * Global boolean variable indicating that the currently logged on user can add Settings
	 */
	private boolean addSettings = false;
	
	/**
	 * Global boolean variable indicating that the currently logged on user can add Reports Groups
	 * and child items (Reports)
	 * 
	 */
	private boolean addReportGroups = false;
	
	/**
	 * Global boolean variable indicating that the currently logged on user can Delete Studies
	 * and child items (Forms and Form versions)
	 */
	private boolean deleteStudies = false;
	
	/**
	 * Global boolean variable indicating that the currently logged on user can delete Users
	 */
	private boolean deleteUsers = false;
	
	/**
	 * Global boolean variable indicating that the currently logged on user can delete Roles
	 */
	private boolean deleteRoles = false;
	
	/**
	 * Global boolean variable indicating that the currently logged on user can delete Tasks
	 */
	private boolean deleteTasks = false;
	
	/**
	 * Global boolean variable indicating that the currently logged on user can delete Settings
	 */
	private boolean deleteSettings = false;
	
	/**
	 * Global boolean variable indicating that the currently logged on user can delete Reports Groups
	 * and child items (Reports)
	 * 
	 */
	private boolean deleteReportGroups = false;

   /**
	 * Creates the PermissionResolver. Permissions must be set later
	 * @param isAdmin loadAdminView indicates is user is admin
	 */
	public PermissionResolver(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	
	/**
	 * Creates the PermissionResolver
	 * @param isAdmin indicates is user is admin
	 * @param currentUserRolePermissions list of user permissions extracted from roles
	 */
	public PermissionResolver(boolean isAdmin, Set<Permission> permissions) {
		this.isAdmin = isAdmin;
		setPermissions(permissions);
	}
	
	/**
	 * Ascertain current user permissions and bind them to relevant lists.
	 * 
	 * Should always be preceded with a call to PermissionResolver.clearPermissions()
	 * 
	 * @param permissions list of permissions
	 */
	private void setPermissions(Set<Permission> permissions) {		
		
		allPermissions = permissions;		
		if(allPermissions != null && allPermissions.size() > 0){
			for(Permission x : allPermissions){
				if(x.getName().contains("Add")){							
					addPermissions.add(x);
				}
				else if(x.getName().contains("Edit")){
					editPermissions.add(x);
				}
				else if(x.getName().contains("View")){
					viewPermissions.add(x);
				}
				else if(x.getName().contains("Delete")){				
					deletePermissions.add(x);
				}
				else {
					extraPermissions.add(x);
				}
			}			
			simulateToolBarPermissions();
		}
	}

	/**
	 * Sets the <code>Permissions</code> for authorizing actions via the <code>Tool Bar.</code>
	 */
	private void simulateToolBarPermissions() {
		if(isAddPermission("Add_Studies"))
			addStudies = true;
		if(isDeletePermission("Delete_Studies"))
			deleteStudies = true;
		if(isAddPermission("Add_Users"))
			addUsers = true;
		if(isDeletePermission("Delete_Users"))
			deleteUsers = true;
		if(isAddPermission("Add_Roles"))
			addRoles = true;
		if(isDeletePermission("Delete_Roles"))
			deleteRoles = true;
		if(isAddPermission("Add_Tasks"))
			addTasks = true;
		if(isDeletePermission("Delete_Tasks"))
			deleteTasks = true;
		if (isAddPermission("Add_Settings"))
			addSettings = true;
		if(isDeletePermission("Delete_Settings"))
			deleteSettings = true;
		if(isAddPermission("Add_Reports"))
			addReportGroups = true;
		if(isDeletePermission("ReportGroups"))
			deleteReportGroups = true;
	}
		
	/**
	 * Retrieve add permissions
	 * 
	 * @return list of add permissions
	 */
	public List<Permission> getAddPermissions(){
		return addPermissions;
	}
	
	/**
	 * Retrieve edit permissions
	 * 
	 * @return list of edit permissions
	 */
	public List<Permission> getEditPermissions(){
		return editPermissions;
	}
	
	/**
	 * Retrieve view permissions
	 * 
	 * @return list of view permissions
	 */
	public List<Permission> getViewPermissions(){
		return viewPermissions;
		
	}
	
	/**
	 * Retrieve delete permissions
	 * 
	 * @return list of delete permissions
	 */
	public List<Permission> getDeletePermissions(){
		return deletePermissions;
		
	}
	
	/**
	 * Add a permission to the Add permission list
	 * 
	 * @param permission permission to add
	 */
	public void addPermissionToAddPermissions(Permission permission){
		addPermissions.add(permission);
	}
	
	/**
	 * Add a permission to the edit permission list
	 * 
	 * @param permission permission to add
	 */
	public void addPermissionToEditPermissions(Permission permission){
		editPermissions.add(permission);
	}
	
	/**
	 * Add a permission to the view permission list
	 * 
	 * @param permission permission to add
	 */
	public void addPermissionToViewPermissions(Permission permission){
		viewPermissions.add(permission);
	}
	
	/**
	 * Add a permission to the delete permission list
	 * 
	 * @param permission permission to add
	 */
	public void addPermissionToDeletePermissions(Permission permission){
		deletePermissions.add(permission);
	}
	
	/**
	 * Determine if the add permission list is not empty
	 * @return true otherwise false
	 */
	public boolean isAddPermission() {
		if (isAdmin) return true;
		
		if(addPermissions == null)
			addPermissions = new Vector<Permission>();
		
		return addPermissions.size() > 0;
	}

	/**
	 * Determine if the delete permission list is not empty
	 * 
	 * @return true otherwise false
	 */
	public boolean isDeletePermission() {
		if (isAdmin) return true;
		
		if(deletePermissions == null)
			deletePermissions = new Vector<Permission>();
		
		return deletePermissions.size() > 0;
	}
	
	/**
	 * Determine if the view permission list is not empty
	 * 
	 * @return true otherwise false
	 */
	public boolean isViewPermission() {
		if (isAdmin) return true;
		
		if(viewPermissions == null)
			viewPermissions = new Vector<Permission>();
		
		return viewPermissions.size() > 0;
	}
	
	/**
	 * Determine if the edit permission list is not empty
	 * 
	 * @return true otherwise false
	 */
	public boolean isEditPermission() {
		if (isAdmin) return true;
		
		if(editPermissions == null)
			editPermissions = new Vector<Permission>();
		
		return editPermissions.size() > 0;
	}
	
	/**
	 * Ascertains if the extra <tt>permissions list</tt> has items in it.
	 * 
	 * @return <tt>True</tt> only and only <code>If(extraPermissions.size() > 0)</code>
	 */
	public boolean isExtraPermission(){
		if(isAdmin)return true;
		
		if(extraPermissions == null)
			extraPermissions = new Vector<Permission>();
		
		return extraPermissions.size() > 0;
		
	}
	
	/**
	 * Determine if the given permission exists in the add permission list
	 * 
	 * @return true otherwise false
	 */
	public boolean isAddPermission(String permissionType) {
		return isPermissionCheck(permissionType, addPermissions);
	}
	
	/**
	 * Determine if the given permission exists in the delete permission list
	 * 
	 * @return true if not else false
	 */
	public boolean isDeletePermission(String permissionType) {
		return isPermissionCheck(permissionType, deletePermissions);
	}
	
	/**
	 * Determine if the given permission exists in the view permission list
	 * 
	 * @return true if not else false
	 */
	public boolean isViewPermission(String permissionType) {
		return isPermissionCheck(permissionType, viewPermissions);
	}
	
	/**
	 * Determine if the given permission exists in the edit permission list
	 * 
	 * @return true if not else false
	 */
	public boolean isEditPermission(String permissionType) {
		
		return isPermissionCheck(permissionType, editPermissions);
	}
	
	/**
	 * Determine if the given permission exists in the extra permission list
	 * 
	 * @return <tt>true if not else false.</tt>
	 */
	public boolean isExtraPermission(String permissionType) {
		return isPermissionCheck(permissionType, extraPermissions);
	}
	
	private boolean isPermissionCheck(String permissionType, List<Permission> permissions) {
		if(isAdmin()){ return true;}
		
		if(permissions != null && permissions.size() > 0){
			for(Permission x : permissions){
				if(x.getName().toUpperCase().contains(permissionType.toUpperCase())){

					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Determine if the given permission exists in any of the permission lists
	 * 
	 * @return true if not else false
	 */
	public boolean hasUserGotPermissions(String permissionType){
		if (isAdmin) return true;
		if(isAddPermission(permissionType) || isDeletePermission(permissionType) || 
				isViewPermission(permissionType) || isEditPermission(permissionType)){
			
			return true;
		}
		else{
			return false;
		}
	}
	
    /**
     * Checks if the current user has the specified permission for any type (add/edit/delete/view)
     * @param permissionType String type of permission (e.g. 'Users')
     * @return boolean true if the user has the permission
     */
    public boolean isPermission(String permissionType) {
    	if (isAdmin) return true;
    	
        if (isAddPermission(permissionType)) {
            return true;
        }
        if (isViewPermission(permissionType)) {
            return true;
        }
        if (isEditPermission(permissionType)) {
            return true;
        }
        if (isDeletePermission(permissionType)) {
            return true;
        }
        if(isExtraPermission(permissionType)){
        	return true;
        }
        return false;
    }
	
	/**
	 * Retrieve add studies flag
	 * 
	 * @return the addStudies flag
	 */
	public boolean isAddStudies() {
		return isAdmin || addStudies;
	}

	/**
	 * Retrieve the flag
	 * 
	 * @return the addUsers flag
	 */
	public boolean isAddUsers() {
		return isAdmin || addUsers;
	}

	/**
	 * Retrieve the add roles flag
	 * 
	 * @return the addRoles flag
	 */
	public boolean isAddRoles() {
		return isAdmin || addRoles;
	}

	/**
	 * Retrieve the add tasks flag
	 * 
	 * @return the addTasks flag
	 */
	public boolean isAddTasks() {
		return isAdmin || addTasks;
	}

	/**
	 * Retrieve the add settings flag
	 * 
	 * @return the addSettings flag
	 */
	public boolean isAddSettings() {
		return isAdmin || addSettings;
	}

	/**
	 * Retrieve the add report groups flag
	 * 
	 * @return the addReportGroups flag
	 */
	public boolean isAddReportGroups() {
		return isAdmin || addReportGroups;
	}

	/**
	 * Retrieve the delete studies flag
	 * 
	 * @return the deleteStudies flag
	 */
	public boolean isDeleteStudies() {
		return isAdmin || deleteStudies;
	}

	/**
	 * Retrieve the delete users flag
	 * 
	 * @return the deleteUsers flag
	 */
	public boolean isDeleteUsers() {
		return isAdmin || deleteUsers;
	}

	/**
	 * Retrieve the delete tasks flag
	 * 
	 * @return the deleteTasks flag
	 */
	public boolean isDeleteTasks() {
		return isAdmin || deleteTasks;
	}

	/**
	 * Retrieve the delete settings flag
	 * 
	 * @return the deleteSettings flag
	 */
	public boolean isDeleteSettings() {
		return isAdmin || deleteSettings;
	}

	/**
	 * Retrieve the delete roles flag
	 * 
	 * @return the deleteRoles flag
	 */
	public boolean isDeleteRoles() {
		return isAdmin || deleteRoles;
	}

	/**
	 * Retrieve delete report groups flag
	 * 
	 * @return the deleteReportGroups flag
	 */
	public boolean isDeleteReportGroups() {
		return isAdmin || deleteReportGroups;
	}

	/**
	 * Returns all the User Permissions.
	 * 
	 * @return the allPermissions
	 */
	public Set<Permission> getLoggedOnUserPermissions() {
		
		if(allPermissions == null)
			allPermissions = new HashSet<Permission>();
		
		return allPermissions;
	}
	
	/**
	 * Determines if the user is administrator
	 * @return true if administrator else false
	 */
	public boolean isAdmin() {
		return isAdmin;
	}

	/**
	 * @return the extraPermissions
	 */
	public List<Permission> getExtraPermissions() {
		return extraPermissions;
	}
	
	/**
	 * Gets the current <code>User's Mapped Forms</code> 
	 * from <code> a list</code> of all  <code>Mapped Forms</code> in the system.
	 * 
	 * @param user <tt>User</tt> to retrieve <tt>UserFormMaps</tt> for.
	 * @param mappedForms List of <tt>Mapped Forms</tt> to retrieve from.
	 * 
	 * @return <code>List</code> of <code>User Mapped Forms.</code>
	 */
	public List<UserFormMap> getUserMappedForms(User user, List<UserFormMap> mappedForms) {
		
		// Hold User's mapped Forms.
		List<UserFormMap> userMappedForms = new Vector<UserFormMap>();
		
		if(user != null &&
				mappedForms != null){
			
			// Administrators have no mapped Forms.
			if(user.hasAdministrativePrivileges()){
				return userMappedForms;
			}
			else{
				for(UserFormMap map : mappedForms){
					if(map.getUserId() == user.getUserId())
						userMappedForms.add(map);
				}	
			}
		}
		
		return userMappedForms;
	}
	
	/**
	 * Gets the current <code>User's Mapped Report Groups</code> 
	 * from <code> a list</code> of all  <code>Mapped Report Groups</code> in the system.
	 * 
	 * @param user <tt>User</tt> to retrieve <tt>UserReportGroupMaps</tt> for.
	 * @param mappedReportGroups List of <tt>Mapped Report Groups</tt> to retrieve from.
	 * 
	 * @return <code>List</code> of <code>User Mapped Report Groups.</code>
	 */
	public List<UserReportGroupMap> getUserMappedReportGroups(User user, List<UserReportGroupMap> mappedReportGroups) {
		
		// Hold User's mapped Report Groups.
		List<UserReportGroupMap> userMappedReportGroups = new Vector<UserReportGroupMap>();		
		
		if(user != null &&
				mappedReportGroups != null){
			
			// Administrators have no mapped Report Groups.
			if(user.hasAdministrativePrivileges()){
				return userMappedReportGroups;
			}
			else{
				for(UserReportGroupMap map : mappedReportGroups){
					if(map.getUserId() == user.getUserId())
						userMappedReportGroups.add(map);					
				}				
			}
		}
		
		return userMappedReportGroups;
	}
	
	/**
	 * Gets the current <code>User's Mapped Reports</code> 
	 * from <code> a list</code> of all  <code>Mapped Reports</code> in the system.
	 * 
	 * @param user <tt>User</tt> to retrieve <tt>UserReportMaps</tt> for.
	 * @param mappedReports List of <tt>Mapped Reports</tt> to retrieve from.
	 * 
	 * @return <tt>List</tt> of <tt>User Mapped Reports.</tt>
	 */
	public List<UserReportMap> getUserMappedReports(User user, List<UserReportMap> mappedReports) {
		
		// Hold User's mapped Reports.
		List<UserReportMap> userMappedReports = new Vector<UserReportMap>();
		
		if(user != null &&
				mappedReports != null){
			
			// Administrators have no mapped Reports.
			if(user.hasAdministrativePrivileges()){
				return userMappedReports;
			}
			else{
				for(UserReportMap map : mappedReports){
					if(map.getUserId() == user.getUserId())
						userMappedReports.add(map);
				}
			}
		}
		
		return userMappedReports;
	}
	
	/**
	 * Gets the current <code>User's Mapped Studies</code> 
	 * from a<code> list</code> of all <code>Mapped Studies</code> in the system.
	 * 
	 * @param user <tt>User</tt> to retrieve <tt>UserStudyMaps</tt> for.
	 * @param mappedStudies List of <tt>Mapped Studies</tt> to retrieve from.
	 * 
	 * @return <code>List</code> of <code>User Mapped Studies.</code>
	 */
	public List<UserStudyMap> getUserMappedStudies(User user, List<UserStudyMap> mappedStudies) {
		
		// Hold User's mapped studies.
		List<UserStudyMap> userMappedStudies = new Vector<UserStudyMap>();
		
		if(user != null &&
				mappedStudies != null){
				
				// Administrators have no mapped studies.
				if(user.hasAdministrativePrivileges()){
					return userMappedStudies;
				}
				else{
					for(UserStudyMap map : mappedStudies){
						if(map.getUserId() == user.getUserId())
							userMappedStudies.add(map);
					}
				}
			}
		
		return userMappedStudies;
	}
}
