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
package org.openxdata.server.admin.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openxdata.server.admin.client.controller.ReportsViewController;
import org.openxdata.server.admin.client.controller.SettingsViewController;
import org.openxdata.server.admin.client.controller.StudiesViewController;
import org.openxdata.server.admin.client.controller.observe.OpenXDataObservable;
import org.openxdata.server.admin.client.controller.observe.OpenXDataObserver;
import org.openxdata.server.admin.client.service.AuthenticationServiceAsync;
import org.openxdata.server.admin.client.service.FormServiceAsync;
import org.openxdata.server.admin.client.service.ReportServiceAsync;
import org.openxdata.server.admin.client.service.RoleServiceAsync;
import org.openxdata.server.admin.client.service.SettingServiceAsync;
import org.openxdata.server.admin.client.service.SmsServiceAsync;
import org.openxdata.server.admin.client.service.StudyManagerServiceAsync;
import org.openxdata.server.admin.client.service.TaskServiceAsync;
import org.openxdata.server.admin.client.service.UserServiceAsync;
import org.openxdata.server.admin.client.service.UtilityServiceAsync;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.Locale;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.Report;
import org.openxdata.server.admin.model.ReportGroup;
import org.openxdata.server.admin.model.Role;
import org.openxdata.server.admin.model.SettingGroup;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;


/**
 * 
 * This provides a global point of access of services or items.
 * If something needs to be accessed by more than one guy, it could be put here.
 * 
 * @author daniel
 * @author Angel
 *
 */
public class Context implements OpenXDataObserver {
	
	/** The default locale key. */
	private static String defaultLocale = "en";
	
	/** The default locale key. */
	private static String locale = defaultLocale;
	
	private static AuthenticationServiceAsync authenticationServiceAsync;
		
	private static SmsServiceAsync smsServiceAsync;
	
	private static FormServiceAsync formServiceAsync;
	
	private static TaskServiceAsync taskServiceAsync;
	
	private static UserServiceAsync userServiceAsync;
	
	private static ReportServiceAsync reportServiceAsync;
	
	private static UtilityServiceAsync utilityServiceAsync;
	
	private static SettingServiceAsync settingServiceAsync;
	
	
	/** The study manager service async interface. */
	private static StudyManagerServiceAsync studyMgrService;
	
	private static RoleServiceAsync roleServiceAsync;
	
	/** The currently logged on user. */
	private static User user;
	
	/** The list of roles. */
	private static List<Role> roles;
	
	/** The list of settings groups. */
	private static List<SettingGroup> settingGroups;
	
	/** The list of studies. */
	private static List<StudyDef> studies;
	
	/** The list of users. */
	private static List<User> users;
	
	/** The list of locales. */
	private static List<Locale> locales;

	private static List<ReportGroup> reportGroups;

	private static List<Permission> permissions;
	
	//Private Constructor
	private Context(){}
	
	//Making the access of the singleton thread safe.
	private static class ContextHolder { 
		private static final Context INSTANCE = new Context();
	}
	
	public static Context getContextInstance(){
		return ContextHolder.INSTANCE;
	}
	
	/**
	 * Starts up the context.
	 */
    public static void startup() {
        studyMgrService = StudyManagerServiceAsync.Util.getInstance();
        authenticationServiceAsync = AuthenticationServiceAsync.Util.getInstance();
        smsServiceAsync = SmsServiceAsync.Util.getInstance();
        roleServiceAsync = RoleServiceAsync.Util.getInstance();
        userServiceAsync = UserServiceAsync.Util.getInstance();
        taskServiceAsync = TaskServiceAsync.Util.getInstance();
        settingServiceAsync = SettingServiceAsync.Util.getInstance();
        reportServiceAsync = ReportServiceAsync.Util.getInstance();
        utilityServiceAsync = UtilityServiceAsync.Util.getInstance();
        formServiceAsync = FormServiceAsync.Util.getInstance();
    }
	
	/**
	 * Shuts down the context.
	 */
	public static void shutdown(){
		user = null;
	}
		
	public static SmsServiceAsync getSmsService(){
		return smsServiceAsync;
	}
	
	public static AuthenticationServiceAsync getAuthenticationService(){
		return authenticationServiceAsync;
	}
	
	/**
	 * Gets the logged in user.
	 * 
	 * @return the user object.
	 */
	public static User getAuthenticatedUser(){
		return user;
	}
	
	/**
	 * Sets the logged in user.
	 * 
	 * @param user the user object.
	 */
	public static void setAuthenticatedUser(User user){
		Context.user = user;
	}
	
	/**
	 * Gets the list of roles.
	 * 
	 * @return the roles list.
	 */
	public static List<Role> getRoles(){
		if(Context.roles == null)
			return new ArrayList<Role>();
		else
			return roles;
	}
	
	/**
	 * Sets the list of roles.
	 * 
	 * @param roles the roles list.
	 */
	public static void setRoles(List<Role> roles){
		Context.roles = roles;
	}
	
	/**
	 * Get the list of locales.
	 * 
	 * @return the locale list.
	 */
	public static List<Locale> getLocales(){
		return locales;
	}
	
	/**
	 * Sets the list of locales.
	 * 
	 * @param locales the locale list.
	 */
	public static void setLocales(List<Locale> locales){
		Context.locales = locales;
	}	
	
	/**
	 * Sets the default locale.
	 * 
	 * @param locale the locale key.
	 */
	public static void setDefaultLocale(String locale){
		Context.defaultLocale = locale;
	}
	
	/**
	 * Gets the default locale.
	 * 
	 * @return the locale key.
	 */
	public static String getDefaultLocale(){
		return defaultLocale;
	}
	
	/** Sets the current locale. 
	 * 
	 * @param locale the locale key.
	 */
	public static void setLocale(String locale){
		Context.locale = locale;
	}
	
	/**
	 * Gets the current locale.
	 * 
	 * @return the locale key.
	 */
	public static String getLocale(){
		return locale;
	}
	
	/**
	 * Checks if we are in localisation mode. 
	 * Being in localisation mode means we are translating 
	 * study contents into a language other than the default.
	 * 
	 * @return true if we are, else false.
	 */
	public static boolean inLocalizationMode(){
		return !defaultLocale.equalsIgnoreCase(locale);
	}
	
	/**
	 * Sets the list of studies.
	 * 
	 * @param studies the studies list.
	 */
	public static void setStudies(List<StudyDef> studies){
		Context.studies = studies;
	}
		
	/** 
	 * Gets the list of users.
	 * @return
	 */
	public static List<User> getUsers(){
		return users;
	}
	
	/**
	 * Gets the list of studies.
	 * @return
	 */
	public static List<StudyDef> getStudies(){
		return studies;
	}
	
	/**
	 * @return the permissionServiceAsync
	 */
	public static RoleServiceAsync getPermissionService() {
		return roleServiceAsync;
	}

	/**
	 * @return the userServiceAsync
	 */
	public static UserServiceAsync getUserService() {
		return userServiceAsync;
	}

	/**
	 * @return the taskServiceAsync
	 */
	public static TaskServiceAsync getTaskService() {
		return taskServiceAsync;
	}
	
	/**
	 * @return the settingServiceAsync
	 */
	public static SettingServiceAsync getSettingService() {
		return settingServiceAsync;
	}
	
	/**
	 * Sets the list of setting groups.
	 * 
	 * @param settingGroups the setting groups list.
	 */
	public static void setSettings(List<SettingGroup> settingGroups){
		Context.settingGroups = settingGroups;
	}
	
	/**
	 * Gets the value of a setting with a given name.
	 * 
	 * @param name the setting name.
	 * @param defaultValue the default value to return if the setting does not exist.
	 * @return the setting value if found, else defaultValue.
	 */
	public static String getSetting(String name, String defaultValue){
		return SettingGroup.getSetting(name,settingGroups,defaultValue);
	}
	
	/**
	 * Gets the study manager service async interface.
	 * 
	 * @return the interface.
	 */
	public static StudyManagerServiceAsync getStudyManagerService(){ 
		return studyMgrService;
	}

	/**
	 * @return the reportServiceAsync
	 */
	public static ReportServiceAsync getReportService() {
		return reportServiceAsync;
	}

	/**
	 * @return the utilityServiceAsync
	 */
	public static UtilityServiceAsync getUtilityService() {
		return utilityServiceAsync;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void update(OpenXDataObservable observable, Object observedModelObjects) {
		List<?> modelObjects = (List<?>) observedModelObjects;
		if(modelObjects != null){
			if(modelObjects.size() > 0){
			        if(observable.getClass().equals(StudiesViewController.class)){
					if(modelObjects.get(0) instanceof StudyDef){
						Context.studies = (List<StudyDef>) modelObjects;
					}
				}
				
				else if(observable.getClass().equals(SettingsViewController.class)){
					if(modelObjects.get(0) instanceof SettingGroup){
						Context.settingGroups = (List<SettingGroup>) modelObjects;
					}
				}

				else if(observable.getClass().equals(ReportsViewController.class)){
					if(modelObjects.get(0) instanceof ReportGroup){
						Context.reportGroups = (List<ReportGroup>) modelObjects;
					}
				}
			}
		}
	}
	
	/**
	 * Retrieves a list of <code>FormDefs</code>.
	 * 
	 * @return <cod>FormDefs</code>.
	 */
	public static List<FormDef> getForms(){
		List<FormDef> forms = new Vector<FormDef>();
		if(Context.getStudies() != null){
			for(StudyDef def : Context.getStudies()){
				forms.addAll(def.getForms());
			}
		}
		
		return forms;
	}

	/**
	 * Retrieves a list of <code>Reports</code>.
	 * 
	 * @return <cod>Reports</code>.
	 */
	public static List<Report> getReports() {
		List<Report> reports = new Vector<Report>();
		if(Context.getReportGroups() != null){
			for(ReportGroup xGroup : Context.reportGroups){
				reports.addAll(xGroup.getReports());
			}
		}
		
		return reports;
	}

	/**
	 * Sets the system <code>Report Groups.</code>
	 * @param reportGroups List of <code>Report Groups.</code>
	 */
	public static void setReportGroups(List<ReportGroup> reportGroups) {
		Context.reportGroups = reportGroups;
		
	}
	
	/**
	 * Returns the list of <code>Report Groups.</code>
	 * @return <code>List</code> of <code>Report Groups.</code>
	 */
	public static List<ReportGroup> getReportGroups(){
		return reportGroups;
		
	}
	
	/**
	 * Sets the List of system permissions.
	 * 
	 * @param permissions List of system permissions.
	 */
	public static void setPermissions(List<Permission> permissions) {
		Context.permissions = permissions;
		
	}
	
	/**
	 * Returns a List of <code>Permissions.</code>
	 * @return <code>List</code> of <code>Permissions.</code>
	 */
	public static List<Permission> getPermissions(){
		return Context.permissions;
	}

	/**
	 * @return the formServiceAsync
	 */
	public static FormServiceAsync getFormServiceAsync() {
		return formServiceAsync;
	}
}
