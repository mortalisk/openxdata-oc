package org.openxdata.server.admin.model;

/**
 * This class encapsulates a permission 
 * which can be used to restrict the level of access in <code>openXData</code>. 
 * 
 * <P>
 * Examples of permissions could be<code> View Form Data, Edit Form Data,
 * Delete Form Data, Create New Studies, Export Data, Export Study </code>.
 * </P>
 */
public class Permission extends AbstractEditable {
	
	private static final long serialVersionUID = 4590744588070449021L;
	
	/** The name of the permission. */
	private String name;
	
	/** The description of the permission. */
	private String description;


	/* UI permissions */
	public static final String PERM_FORM_DESIGN = "Perm_Form_Design";
	public static final String PERM_EMIT= "Perm_Emit";
	public static final String PERM_DATA_ENTRY = "Perm_Data_Entry";
    public static final String PERM_DATA_EDIT = "Perm_Data_Edit";
    public static final String PERM_EXPORT_REPORTS ="Perm_Export_Reports";
	public static final String PERM_IMPORT_REPORTS = "Perm_Import_Reports";
	public static final String PERM_IMPORT_STUDIES = "Perm_Import_Studies";
	public static final String PERM_EXPORT_STUDIES = "Perm_Export_Studies";
	public static final String PERM_REPORT_QUERY_BUILDER ="Perm_Report_Query_Builder";
	public static final String PERM_DATABASE_BACKUP = "Perm_Database_Backup";
	public static final String PERM_DIAGNOSE_SYSTEM ="Perm_Diagnose_System";
	public static final String PERM_OPEN_ITEMS_VIA_FILE_MENU = "Perm_Open_Items_Via_File_Menu";
	public static final String PERM_MOBILE_INSTALLER = "Perm_Mobile_Installer";

	/* task permissions*/
	public static final String PERM_TASK_SCHEDULING = "Perm_Task_Scheduling";
	public static final String PERM_TASK_ADDING_PARAMETER = "Perm_Task_Adding_Parameter";

	/*settingsgroup permissions*/
	public static final String PERM_ADD_SETTINGSGROUP = "Perm_Add_SettingsGroup";
	public static final String PERM_VIEW_SETTINGSGROUP ="Perm_View_SettingsGroup";
	public static final String PERM_DELETE_SETTINGSGROUP = "Perm_Delete_SettingsGroup";
	public static final String PERM_EDIT_SETTINGSGROUP = "Perm_Edit_SettingsGroup";

	/* Report group Permissions */
	public static final String PERM_ADD_REPORTGROUPS = "Perm_Add_ReportGroups";
	public static final String PERM_VIEW_REPORTGROUPS = "Perm_View_ReportGroups";
	public static final String PERM_DELETE_REPORTGROUPS = "Perm_Delete_ReportGroups";
	public static final String PERM_EDIT_REPORTGROUPS = "Perm_Edit_ReportGroups";

	/* Role/Permission permissions*/
	public static final String PERM_ADD_ROLES = "Perm_Add_Roles";
	public static final String PERM_DELETE_ROLES = "Perm_Delete_Roles";
	public static final String PERM_VIEW_ROLES = "Perm_View_Roles";
	public static final String PERM_EDIT_ROLES = "Perm_Edit_Roles";
	public static final String PERM_VIEW_PERMISSIONS = "Perm_View_Permissions";
	public static final String PERM_DELETE_PERMISSIONS = "Perm_Delete_Permissions";
	public static final String PERM_EDIT_PERMISSIONS = "Perm_Edit_Permissions";
	public static final String PERM_EDIT_ROLE_PERMISSIONS ="Perm_Edit_Role_Permissions";
	public static final String PERM_EDIT_USER_ROLES = "Perm_Edit_User_Roles";

	/* Locale Permissions*/
	public static final String PERM_ADD_LOCALES = "Perm_Add_Locales";
	public static final String PERM_ADD_PERMISSIONS = "Perm_Add_Permissions";
	public static final String PERM_VIEW_LOCALES = "Perm_View_Locales";
	public static final String PERM_DELETE_LOCALES ="Perm_Delete_Locales";
	public static final String PERM_EDIT_LOCALES = "Perm_Edit_Locales";

	/* Form permissions */
	public static final String PERM_ADD_FORMS = "Perm_Add_Forms";
	public static final String PERM_VIEW_FORM_VERSIONS = "Perm_View_Form_Versions";
	public static final String PERM_DELETE_FORM_VERSIONS = "Perm_Delete_Form_Versions";
	public static final String PERM_EDIT_FORM_VERSIONS = "Perm_Edit_Form_Versions";
	public static final String PERM_ADD_FORM_VERSIONS = "Perm_Add_Form_Versions";
	public static final String PERM_VIEW_FORMS = "Perm_View_Forms";
	public static final String PERM_DELETE_FORMS = "Perm_Delete_Forms";
    public static final String PERM_EDIT_FORMS = "Perm_Edit_Forms";    

	/* Data permissions */
	public static final String PERM_ADD_FORM_DATA = "Perm_Add_Form_Data";
	public static final String PERM_VIEW_FORM_DATA = "Perm_View_Form_Data";
	public static final String PERM_DELETE_FORM_DATA = "Perm_Delete_Form_Data";
	public static final String PERM_EDIT_FORM_DATA = "Perm_Edit_Form_Data";
	public static final String PERM_EDIT_MY_FORM_DATA = "Perm_Edit_My_Form_Data";
	public static final String PERM_EXPORT_FORM_DATA = "Perm_Export_Form_Data";
	public static final String PERM_VIEW_UNEXPORTED_FORM_DATA = "Perm_View_Unexported_Form_Data";

	/* Reports Permissions*/
	public static final String PERM_ADD_REPORTS = "Perm_Add_Reports";
	public static final String PERM_DELETE_REPORTS = "Perm_Delete_Reports";
	public static final String PERM_VIEW_REPORTS = "Perm_View_Reports";
	public static final String PERM_EDIT_REPORTS = "Perm_Edit_Reports";

	/* Settings Permissions */
	public static final String PERM_ADD_SETTINGS = "Perm_Add_Settings";
	public static final String PERM_DELETE_SETTINGS = "Perm_Delete_Settings";
	public static final String PERM_VIEW_SETTINGS = "Perm_View_Settings";
    public static final String PERM_EDIT_SETTINGS = "Perm_Edit_Settings";

	/*Task Permissions*/
	public static final String PERM_ADD_TASKS = "Perm_Add_Tasks";
	public static final String PERM_DELETE_TASKS = "Perm_Delete_Tasks";
	public static final String PERM_VIEW_TASKS = "Perm_View_Tasks";
	public static final String PERM_EDIT_TASKS = "Perm_Edit_Tasks";

	/* User permissions*/
	public static final String PERM_ADD_USERS = "Perm_Add_Users";
	public static final String PERM_DELETE_USERS = "Perm_Delete_Users";
	public static final String PERM_VIEW_USERS = "Perm_View_Users";
	public static final String PERM_EDIT_USERS = "Perm_Edit_Users";
	public static final String PERM_IMPORT_USERS = "Perm_Import_Users";

	/*study permissions*/
	public static final String PERM_ADD_STUDIES = "Perm_Add_Studies";
	public final static String PERM_VIEW_STUDIES = "Perm_View_Studies";
	public static final String PERM_DELETE_STUDIES = "Perm_Delete_Studies";
	public static final String PERM_EDIT_STUDIES = "Perm_Edit_Studies";
	
	
	/**
	 * Constructs a new permission object.
	 */
	public Permission(){
		
	}
	
	/**
	 * Constructs a new permission object with a given name.
	 * 
	 * @param name the name of the permission.
	 */
	public Permission(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public String getDescription(){
		return description;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
}
