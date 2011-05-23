package org.openxdata.server.admin.client.permissions.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.permissions.PermissionResolver;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.Role;
import org.openxdata.server.admin.model.User;

/**
 * Utility class that holds the <code>Roles</code> for the currently logged on <code>User.</code> 
 * Organizes the <code>Roles</code> into a central place where they can be globally accessed for other operations to execute.
 * 
 * <p>
 * This <tt>class</tt> can not be instantiated.
 * </p>
 * 
 * 
 */
public class RolesListUtil {

	private static boolean isAdmin = false;
	
	private static Set<Permission> userPermissions;

	/**
	 * List of roles for currently logged in user
	 */
	private static Set<Role> userRoles = new HashSet<Role>();

	private static PermissionResolver permissionResolver;
	
	/**
	 * This is to avoid initialization of this class. Should be purely a utility
	 * class for role and some permission based implementations and operations.
	 * 
	 */
	private RolesListUtil() {}
	
	/**
	 * <tt>Inner class</tt> to holder a <tt>singleton instance</tt> of this <tt>class.</tt>
	 * Meant to make the testing of this class easier.
	 * 
	 *
	 */
	private static class RolesListUtilHolder{
		private static final RolesListUtil INSTANCE = new RolesListUtil();
	}
	
	/**
	 * Access the <tt>singleton</tt> in a <tt>threadsafe</tt> way.
	 * @return
	 */
	public static RolesListUtil getInstance(){
		return RolesListUtilHolder.INSTANCE;
	}

	/**
	 * Predicate method to ascertain if the user is administrator
	 * 
	 * @return true if role is administrator else false
	 */
	public boolean hasUserGotAdminPrivileges() {
		boolean ret = false;
		for (Role x : userRoles) {
			if (x.isDefaultAdminRole()) {
				ret = true;
				setAdmin(true);
			} else {
				ret = false;
			}
		}
		return ret;

	}

	/**
	 * Sets the flag to indicate if current <code>User</code> is administrator or not.
	 * 
	 * @param isAdmin Flag to set.
	 */
	private static void setAdmin(boolean isAdmin) {
		RolesListUtil.isAdmin = isAdmin;

	}

	/**
	 * Returns true if the current user has an administrative role.
	 * 
	 * @return true if Administrator otherwise false.
	 */
	public boolean isAdmin() {
		return isAdmin;
	}

	/**
	 * Setting the roles of the currently logged on user
	 * 
	 * @param roles
	 *            user roles
	 */
	public void setUserRoles(Set<Role> roles) {
		
		if(roles == null){
			userRoles = new HashSet<Role>();
			userPermissions = new HashSet<Permission>();
		}			
		else{
			RolesListUtil.userRoles = roles;
			userPermissions = setUserPermissions(roles);
			
			if (hasUserGotRoles())
				hasUserGotAdminPrivileges();
		}

	}

	/**
	 * Extracts the <code>User</code> Permissions given the <code>User Roles.</code>
	 * 
	 * @param roles <code>User Roles.</code>
	 * 
	 * @return <code>Collection of User Permissions.</code>
	 */
	private static Set<Permission> setUserPermissions(Set<Role> roles) {
		
		if(userPermissions == null)
			userPermissions = new HashSet<Permission>();
		
		for(Role xRole : roles){
			userPermissions.addAll(xRole.getPermissions());
		}
		
		return userPermissions;
	}
	
	/**
	 * Returns the currently logged in <code>User's permissions</code> extracted from <code>Roles.</code>
	 * <p>
	 * This method should only be used when the <code>MainView</code> is initializing. Henceforth use the PermissionResolver.getAllPermissions()
	 * if access to logged on <code>User's permissions</code> is required.
	 * 
	 * @return <code>Collection</code> of <code>User Roles.</code>
	 */
	public Set<Permission> getUserPermissions(){
		return RolesListUtil.userPermissions;
	}
	
	/**
	 * Getting the roles of the currently logged on <code>User.</code>
	 * 
	 * @return user roles
	 */
	public Set<Role> getUserRoles() {
		return userRoles;
	}

	/**
	 * Utility method to check if the user has any roles
	 * 
	 * @return
	 */
	public boolean hasUserGotRoles() {
		return userRoles.size() > 0;
	}

	/**
	 * Clears the current <tt>List</tt> of <tt>User Roles.</tt>
	 */
	public void clearRoles() {
		userRoles.clear();
		setAdmin(false);
	}
	
	/**
	 * Clears the current <tt>List</tt> of <tt>User Permissions.</tt>
	 */
	public void clearPermissions(){
		userPermissions.clear();
	}

	/**
	 * Checks If a <code> Permission</code> is ancillary.
	 * By ancillary we mean that it requires other permissions in order to perform appropriately.
	 * <p>
	 * For example, a <code>User</code> with <code>Perm_Add_Form_Versions</code> 
	 * needs the <code>Permission Perm_Form_Design</code> to correctly accomplish their duties in the <code>Form Designer</code>.
	 * </p>
	 * 
	 * @param perm - <code>Permission</code> to check.
	 * @param systemPermissions - List of System permissions 
	 * @param permissionsAttachedToRole - Current <tt>Role</tt> Permissions.
	 * 
	 * @return List of Permissions to bind to the <code>User</code>.
	 */
	public static List<Permission> checkAndBindAncillaryPermissions(Permission perm, List<Permission> systemPermissions, List<Permission> permissionsAttachedToRole) {
		
		List<Permission> allPermissions = systemPermissions;
		List<Permission> rolePermissions = new Vector<Permission>();
		
		if(perm != null){
			String permName = perm.getName();
			
			//Checking for if the permission needs the Perm_Form_Design permission
			if(checkIfPermissionIsAncillary(permName, "Perm_Add_Forms") 
					|| checkIfPermissionIsAncillary(permName, "Perm_Add_Form_Versions")
					|| checkIfPermissionIsAncillary(permName, "Perm_Delete_Forms")
					|| checkIfPermissionIsAncillary(permName, "Perm_Delete_Form_Versions")
					|| checkIfPermissionIsAncillary(permName, "Perm_Edit_Forms")
					|| checkIfPermissionIsAncillary(permName, "Perm_Edit_Form_Versions")
					|| checkIfPermissionIsAncillary(permName, "Perm_View_Forms")
					|| checkIfPermissionIsAncillary(permName, "Perm_View_Form_Versions")){
				
				if(allPermissions != null && allPermissions.size() > 0){
					
					Permission viewFormsPermission = getAncillaryPermission(allPermissions, "Perm_View_Forms");
					Permission formDesignPermission = getAncillaryPermission(allPermissions, "Perm_Form_Design");
					Permission viewStudiesPermission = getAncillaryPermission(allPermissions, "Perm_View_Studies");
					Permission viewFormDataPermission = getAncillaryPermission(allPermissions, "Perm_View_Form_Data");
					
					if(!containsPermission(formDesignPermission.getName(), permissionsAttachedToRole)){
						rolePermissions.add(formDesignPermission);
					}
						
					if(!containsPermission(perm.getName(), permissionsAttachedToRole)){
						rolePermissions.add(perm);
					}						
					
					if(!containsPermission(viewFormsPermission.getName(), permissionsAttachedToRole)){
						rolePermissions.add(viewFormsPermission);
					}
					
					if(!containsPermission(viewStudiesPermission.getName(), permissionsAttachedToRole)){
						rolePermissions.add(viewStudiesPermission);
					}
					
					if(!containsPermission(viewFormDataPermission.getName(), permissionsAttachedToRole)) {
						rolePermissions.add(viewFormDataPermission);
					}
				}
			}
			else{
				//Add original Permission
				rolePermissions.add(perm);
			}
		}
		
		return rolePermissions;
	}

	/**
	 * Predicate method to check if a permission is already in a given list.
	 * 
	 * @param permissionToCheck - <code>Permission</code> to check for.
	 * @param permissionListToCheckAgainst - <code>List of Permissions </code> to check from.
	 * 
	 * @return <code>True if(exists)<p>else False<p></code>
	 */
	private static boolean containsPermission(String permissionToCheck, List<Permission> permissionListToCheckAgainst) {
		boolean ret = false;		
		for(Permission perm : permissionListToCheckAgainst){
			if(perm != null){
				if(perm.getName().equalsIgnoreCase(permissionToCheck)){
					ret = true;
					break;
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * Adds a system generated <tt>Role</tt> to the <code>User</code> during the mapping of Study and Form Objects.
	 * 
	 * @param user <code>User</code> to add <tt>Role</tt> to. Note that <tt>Role</tt> added is post fixed with "_" e.g. Role_View_Foo_.
	 */
	public static Role  addViewStudiesRoleOnStudyMap(User user){
		Role addedRole = null;
		List<Permission> userPermissions = getUserPermissions(user);
		if(containsPermission("Perm_View_Studies", userPermissions))
			return null;
		else{
			Role xRole = getAncillaryRole("Role_View_Studies_", Context.getRoles());				
			if(xRole != null){
				if(!user.hasRole(xRole)){	
					user.addRole(xRole);
                                        addedRole = xRole;
				}
			}
			else{
				xRole = createAncillaryStudiesRole(xRole);
				user.addRole(xRole);
                                addedRole = xRole;
			}
		}
                return  addedRole;
	}
	
	/**
	 * Adds a system generated <tt>Role</tt> to the <code>User</code> during the mapping of <code>Report Group objects.</code>
	 * 
	 * @param user <code>User</code> to add <tt>Role</tt> to. Note that <tt>Role</tt> added is post fixed with "_" e.g. Role_View_Foo_.
	 */
	public static Role addViewReportGroupsRoleOnReportGroupMap(User user){
		List<Permission> lUserPermissions = getUserPermissions(user);
		if(containsPermission("Perm_View_ReportGroups", lUserPermissions))
			return null;
		else{
			Role xRole = getAncillaryRole("Role_View_ReportGroups_", Context.getRoles());
			if(xRole != null){
				if(!user.hasRole(xRole)){
					user.addRole(xRole);
				}
			}
			else{
				
				xRole = createAncillaryViewReportGroupsRole(xRole);
				addXRoleToLists(user, xRole);
			}
                        return xRole;
		}
	}

	/**
	 * Retrieves the <code>User permissions.</code>
	 * 
	 * @param user <code>User</code> to retrieve <code>permissions.</code>
	 * @return List of <code>User Permissions.</code>
	 */
	private static List<Permission> getUserPermissions(User user) {
		List<Permission> userPermissions = new Vector<Permission>();
		for(Role role : user.getRoles()){
			userPermissions.addAll(role.getPermissions());
		}
		return userPermissions;
	}
	
	/**
	 * Creates the new <code>Role</code> with specific <code>Permissions</code>.
	 * 
	 * @param role <code>Role to construct.</code>
	 * 
	 * @return Constructed <code>Role</code>
	 */
	private static Role createAncillaryViewReportGroupsRole(Role role) {
		
		role = new Role("Role_View_ReportGroups_");
		role.setDescription("Ancillary Role created by the system during the mapping of Report Groups. Assigned to the User to enable them view Mapped Reports Groups.");
		
		Permission viewReportsPermission = getAncillaryPermission(Context.getPermissions(), "Perm_View_ReportGroups");
		role.addPermission(viewReportsPermission);
		
		return role;
	}
	
	/**
	 * Creates the new <code>Role</code> with specific <code>Permissions</code>.
	 * 
	 * @param role <code>Role to construct.</code>
	 * 
	 * @return Constructed <code>Role</code>
	 */
	private static Role createAncillaryStudiesRole(Role role) {
		
		role = new Role("Role_View_Studies_");
		role.setDescription("Ancillary Role created by the system during the mapping of Studies. Assigned to the User to enable them view Mapped Studies.");
		
		Permission viewStudiesPermission = getAncillaryPermission(Context.getPermissions(), "Perm_View_Studies");
		role.addPermission(viewStudiesPermission);
		
		return role;
	}
		
	/**
	 * Gets a <code>Role</code> from a specified list of Roles.
	 * 
	 * @param roleName Name of Role to get
 	 * @param listOfRoles List of Role to search from. 
	 * @return <code>Role if exists in the List</code>
	 */
	private static Role getAncillaryRole(String roleName, List<Role> listOfRoles) {
		for(Role x : listOfRoles){
			if(x.getName().equals(roleName)){
				return x;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the <code>ancillary Permission 
	 * </code>[specified by the <code>ancillaryPermissionName parameter</code>] from a <code>List</code> of given <code>Permissions</code>.
	 * 
	 * @param permissions <code>List</code> of <code>Permissions</code> to retrieve from.
	 * @param ancillaryPermissionName The ancillary <tt>permission</tt> to check for.
	 * 
	 * @return <code>Permission (Perm_Form_Design)</code>.<p><code>If (exists) else null</code></p>.
	 * 
	 */
	private static Permission getAncillaryPermission(List<Permission> permissions, String ancillaryPermissionName) {
		
		Permission permission = null;		
		for(Permission perm : permissions){
			if(perm.getName().equals(ancillaryPermissionName) ||
					perm.getName().equalsIgnoreCase(ancillaryPermissionName)){
				
				permission = perm;
				break;
			}
		}
		
		return permission;
	}

	/**
	 * Checks if a given <code>Role</code> exists in a given collection.
	 * 
	 * @param role <code>Role</code> to check for.
	 * @param roles Collection to check from.
	 * 
	 * @return <code>True only and only if role exists in collection.</code>
	 */
	private static boolean checkIfRoleExists(Role role, List<Role> rolesList) {
		for(Role x : rolesList){
			if(x.getName().equals(role.getName())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if a given <code>Permission</code> is ancillary,
	 * depending on the <code>ancillaryPermissionNameToCheckAgainst</code> parameter.
	 * 
	 * @param permName <code>Permission</code> to check <code>If(ancillary)</code>.
	 * @param ancillaryPermissionNameToCheckAgainst - <code>Ancillary Permission</code> we checking for.
	 * 
	 * @return <code>True if(ancillary)<p>else False<p></code>
	 */
	private static boolean checkIfPermissionIsAncillary(String permName, String ancillaryPermissionNameToCheckAgainst) {
		boolean ret = false;
		if(permName.equalsIgnoreCase(ancillaryPermissionNameToCheckAgainst) ||
				permName.contains(ancillaryPermissionNameToCheckAgainst)){
			ret = true;
		}
				
		return ret;
	}

	/**
	 * Adds a system generated <tt>Role</tt> to the <code>User</code> during the mapping of <code>FormDefs</code>.
	 * 
	 * @param user <code>User</code> to add <tt>Role</tt> to. Note that <tt>Role</tt> added is post fixed with "_" e.g. Role_View_Studies_.
	 */
	public static Role addViewFormsRoleOnFormMap(User user) {
		List<Permission> userPermissions = getUserPermissions(user);
		if(containsPermission("Perm_View_Forms", userPermissions))
			return null;
		else{
			
			Role xRole = getAncillaryRole("Role_View_Forms_", Context.getRoles());
			if(xRole != null){
				if(!user.hasRole(xRole)){
					user.addRole(xRole);
				}
			}
			else{
				xRole = createAncillaryViewFormsRole(xRole);
				addXRoleToLists(user, xRole);
                                
			}return xRole;
		}	
	}

	/**
	 * Adds the ancillary <code>Role</code> to specified objects for saving.
	 * 
	 * @param user <code>User</code> to add <code>Role</code> to.
	 * @param xRole <code>Role</code> to add to Lists</code>.
	 */
	private static void addXRoleToLists(User user, Role xRole) {
		if(!checkIfRoleExists(xRole, Context.getRoles())){
			Context.getRoles().add(xRole);
		}
		
		user.addRole(xRole);
	}

	/**
	 * Creates the new <code>Role</code> with specific <code>Permissions</code>.
	 * 
	 * @param xRole <code>Role to construct.</code>
	 * 
	 * @return Constructed <code>Role</code>
	 */
	private static Role createAncillaryViewFormsRole(Role xRole) {
		xRole = new Role("Role_View_Forms_");
		xRole.setDescription("Ancillary Role created by system during the mapping of Forms. Assigned to User to enable view Mapped Forms.");
		
		Permission viewFormsPermission = getAncillaryPermission(Context.getPermissions(), "Perm_View_Forms");
		Permission viewStudiesPermission = getAncillaryPermission(Context.getPermissions(), "Perm_View_Studies");
		
		xRole.addPermission(viewFormsPermission);
		xRole.addPermission(viewStudiesPermission);
		
		return xRole;
	}

	/**
	 * Add a system generated <tt>Role</tt> to the <code>User</code> during the mapping of <code>Reports.</code>
	 * @param user <tt>User</tt> to add <tt>ancillary Roles</tt> to.
	 */
	public static Role addViewReportsRoleOnReportMap(User user) {
		List<Permission> userPermissions = getUserPermissions(user);
		if(containsPermission("Perm_View_Reports", userPermissions))
			return null;
		else{
			Role xRole = getAncillaryRole("Role_View_Reports_", Context.getRoles());
			if(xRole != null){
				if(!user.hasRole(xRole)){
					user.addRole(xRole);
				}
			}
			else{
				xRole = createAncillaryViewReportsRole(xRole);
				addXRoleToLists(user, xRole);
			}
                        return xRole;
		}
	}

	/**
	 * Creates the new <code>Role</code> with specific <code>Permissions</code>.
	 * 
	 * @param xRole <code>Role to construct.</code>
	 * 
	 * @return Constructed <code>Role</code>
	 */
	private static Role createAncillaryViewReportsRole(Role xRole) {
		xRole = new Role("Role_View_Reports_");
		xRole.setDescription("Ancillary Role created by system during the mapping of Reports. Assigned to User to enable view Mapped Reports.");
		
		Permission viewReportsPermission = getAncillaryPermission(Context.getPermissions(), "Perm_View_Reports");
		Permission viewReportGroupsPermission = getAncillaryPermission(Context.getPermissions(), "Perm_View_Report_Groups");
		
		xRole.addPermission(viewReportsPermission);
		xRole.addPermission(viewReportGroupsPermission);
		
		return xRole;
	}

	/**
	 * Removes the <code>ancillary permission</code> that was added to the <code>User</code> during mapping of <code>Reports</code>
	 * @param user <code>User</code> to remove <code>ancillary permission</code> from.
	 */
	public static void removeViewReportsRoleAddedOnMap(User user) {	
		Role xRole = null;
		Set<Role> roles = user.getRoles();
		for (Iterator<Role> it = roles.iterator(); it.hasNext(); ) {			
			Object item = it.next();
			if(((Role) item).getName().equals("Role_View_Reports_")){
				xRole = (Role) item;
			}
		}
		
		if(xRole != null)
			user.removeRole(xRole);
	}
	
	/**
	 * Removes the <code>ancillary Role</code> that was added to the <code>User</code> during mapping of <code>Forms</code>
	 * @param user <code>User</code> to remove <code>ancillary Role</code> from.
	 */
	public static void removeViewFormsRoleAddedOnMap(User user) {
		Role xRole = null;
		Set<Role> roles = user.getRoles();
		for (Iterator<Role> it = roles.iterator(); it.hasNext(); ) {				
			Object item = it.next();
			if(((Role) item).getName().equals("Role_View_Forms_")){
				xRole = (Role) item;
			}
		}
		
		if(xRole != null)
			user.removeRole(xRole);
	}
	
	/**
	 * Removes the <code>ancillary Role</code> that was added to the <code>User</code> during mapping of <code>Reports or Report Groups.</code>
	 * 
	 * @param user <code>User</code> to remove <code>ancillary Role</code> from.
	 */
	public static void removeViewReportGroupsRoleOnReportGroupMap(User user){
		
		Role xRole = null;
		Set<Role> roles = user.getRoles();
		for (Iterator<Role> it = roles.iterator(); it.hasNext(); ) {				
			Object item = it.next();
			if(((Role) item).getName().equals("Role_View_ReportGroups_")){
				xRole = (Role) item;
			}
		}
		
		if(xRole != null)
			user.removeRole(xRole);
	}
	
	/**
	 * Removes the <code>ancillary permission</code> that was added to the <code>User</code> during mapping of <code>Studies</code>
	 * @param user <code>User</code> to remove <code>ancillary permission</code> from.
	 */
	public static Role removeViewStudiesRoleAddedOnMap(User user){
		Role xRole = null;
		Set<Role> roles = user.getRoles();
		for (Iterator<Role> it = roles.iterator(); it.hasNext(); ) {				
			Object item = it.next();
			if(((Role) item).getName().equals("Role_View_Studies_")){
				xRole = (Role) item;
			}
		}
		
		if(xRole != null)
			user.removeRole(xRole);
                return xRole;
	}

	/**
	 * Sets the <tt>Permission Resolving Object</tt> for this session.
	 * 
	 * @param permissionResolver<tt>Permission Resolving Object</tt> to set.
	 */
	public static void setPermissionResolver(PermissionResolver permissionResolver) {
		RolesListUtil.permissionResolver = permissionResolver;		
	}
	
	/**
	 * Retrieves the <tt>Permission Resolving Object</tt> configured for this session.
	 * @return instance of {@link PermissionResolver}
	 */
	public static PermissionResolver getPermissionResolver(){
		return RolesListUtil.permissionResolver;
	}
}
