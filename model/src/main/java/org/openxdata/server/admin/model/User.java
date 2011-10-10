package org.openxdata.server.admin.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is used to represent a user who can access the system.
 */
public class User extends AbstractEditable {

	private static final long serialVersionUID = -410052012755451028L;
	
	private String name;
	
	private String firstName;
	
	private String middleName;
	
	private String lastName;
	
	/** The user hashed password. */
	private String password;
	
	/** The salt used to hash the user's password. */
	private String salt;
	
	private String secretQuestion;
	
	private String secretAnswer;
	
	/** The user's phone number. For now, this is used for optionally adding another
	 * security level for user's who try to submit data using sms.
	 */
	private String phoneNo;
	
	/** The List of roles that the user has. */
	private Set<Role> roles;
	
	/** A flag which is set to true when the user cannot be allowed any longer to access
	 * the system. Such users are not deleted because their id's may be referenced as 
	 * foreign keys in some other tables.
	 */
	private Boolean voided = false;
	
	/** The user who made this user voided. */
	private User voidedBy;
	
	/** The date when this user was voided. */
	private Date dateVoided;
	
	/** The reason why this user was voided. */
	private String voidReason;
	
	/**
	 * The user's clear text password.
	 * This is just a means of transfer to the service layer because it's never persisted.
	 */
	private String clearTextPassword;
	
	private String email;
	
	/** Flag to determine if <code>User</code> is disabled or not*/
	private int status = 0;
	
	/** Models an active <code>User</code> */
	public static final int ACTIVE = 0;
	
	/** Models a disabled <code>User</code>*/
	public static final int DISABLED = 1;
	
	/** Models a new <code>User</code> pending approval*/
	public static final int PENDING_APPROVAL = 2;
		
	public User(){
		
	}
	
	/**
	 * Create a user with a given login name and password.
	 * 
	 * @param name the login name.
	 * @param clearTextPassword the non hashed password.
	 */
	public User(String name, String clearTextPassword){
		this.name = name;
		this.clearTextPassword = clearTextPassword;
	}
	
	/**
	 * Create a user with a given database id,login name, hashed password and salt.
	 * 
	 * @param userId the database id.
	 * @param name the login name.
	 * @param password the hashed password
	 * @param salt the salt.
	 */
	public User(int userId,String name, String password, String salt){
		this.id = userId;
		this.name = name;
		this.password = password;
		this.salt = salt;
	}
	
	/**
	 * Create a user with a given login name.
	 * 
	 * @param name the login name.
	 */
	public User(String name){
		this.name = name;
	}
	
	/**
	 * Create a user with a given database id and login name.
	 * 
	 * @param userId the database id.
	 * @param name the login name.
	 */
	public User(int userId, String name){
		this.id = userId;
		this.name = name;
	}

	public Date getDateVoided() {
		return dateVoided;
	}

	public void setDateVoided(Date dateVoided) {
		this.dateVoided = dateVoided;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getName() {
		return name;
	}
	
	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getClearTextPassword() {
		return clearTextPassword;
	}

	public void setClearTextPassword(String clearTextPassword) {
		this.clearTextPassword = clearTextPassword;
	}

	public String getFullName(){
		String fullName = "";
		
		if(firstName != null)
			fullName += firstName  + " ";
		if(middleName != null)
			fullName += middleName  + " ";
		if(lastName != null)
			fullName += lastName;
		
		return fullName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Retrieves all the <code>User Roles.</code>
	 * @return <code>Set</code> of <code>User Roles.</code>
	 */
	public Set<Role> getRoles() {
		return roles;
	}

	/**
	 * Sets the <code>User Roles.</code>
	 * @param roles <code>User Roles.</code>
	 */
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	/**
	 * Adds a <code>Role</code> to the set of roles mapped to this <code>User.</code>
	 * @param role <code>Role</code> to map to the <code>User.</code>
	 */
	public void addRole(Role role){
		if (roles == null) {
			roles = new HashSet<Role>();
		}
		roles.add(role);
	}

	/**
	 * Removes a <code>Role</code> from the set of roles mapped to this <code>User.</code>
	 * @param role <code>Role</code> to remove from the <code>User.</code>
	 */
	public void removeRole(Role role){
		Role roleToRemove = null;
		if (roles != null) {
			for(Role x : roles) {
				if (x.getName().equals(role.getName())) {
					roleToRemove = x;
					break;
				}
			}
		}
		if (roleToRemove != null) {
			// can't remove from a collection while you are iterating over it
			roles.remove(roleToRemove);
		}
	}
	
	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getSecretAnswer() {
		return secretAnswer;
	}

	public void setSecretAnswer(String secretAnswer) {
		this.secretAnswer = secretAnswer;
	}

	public String getSecretQuestion() {
		return secretQuestion;
	}

	public void setSecretQuestion(String secretQuestion) {
		this.secretQuestion = secretQuestion;
	}

	public Boolean getVoided() {
		return voided;
	}

	public void setVoided(Boolean voided) {
		this.voided = voided;
	}

	public User getVoidedBy() {
		return voidedBy;
	}

	public void setVoidedBy(User voidedBy) {
		this.voidedBy = voidedBy;
	}

	public String getVoidReason() {
		return voidReason;
	}

	public void setVoidReason(String voidReason) {
		this.voidReason = voidReason;
	}
		
	public boolean hasNewPassword(){
		return (clearTextPassword != null && clearTextPassword.trim().length() > 0);
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
 
    /**
     * Determines if this user has the administrator role.
     * 
     * @return True if has ROLE_ADMIN attached to them. Else, false.
     */
    public boolean hasAdministrativePrivileges() {
        if (roles != null) {
            for (Role r : roles) {
                if (r.isDefaultAdminRole()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void setStatus(int status) {
    	this.status = status;
    }
    
    /**
     * Determines if this user has the specified permission
     * @param permission String permission (e.g. "Perm_Data_Edit")
     * @return
     */
    public boolean hasPermission(String... permission) {
    	boolean ret = false;
    	if (this.hasAdministrativePrivileges()) {
    		ret = true;
    	} else {
	    	if (roles != null) {
	            for (Role r : roles) {
	            	for (Permission p : r.getPermissions()) {
	            		for (String perm : permission) {
	            			if (perm.equalsIgnoreCase(p.getName())) {
	            				ret = true;
	            				break;
	            			}
	            		}
	            	}
	            }
	    	}
    	}
    	return ret;
    }
    
	public void setStatusAfterGiveType(String status) {
		if(status.equalsIgnoreCase("Active"))
			this.status = 0;
		else
			if(status.equalsIgnoreCase("Disabled"))
				this.status = 1;
			else
				if(status.equalsIgnoreCase("Pending Approval"))
					this.status = 2;
				else
					this.status = -1;
	}
	
	public int getStatus() {
		return status;
	}
	
	/**
	 * Returns a string indicating the <code>User Status.</code>
	 * 
	 * @return string corresponding to the <code>User status.</code>
	 */
	public String getStatusType() {
		if(isDisabled())
			return "Disabled";
		if(isActive())
			return "Active";
		if (isPendingApproval())
			return "Pending Approval";
		
		return "";
	}
	
	public boolean isActive() {
		return status == 0;
	}
	
	public boolean isDisabled() {
		return status == 1;
	}
	
	public boolean isPendingApproval() {
		return status == 2;
	}

	public void setUserStatus(int status) {
		this.status = status;
		
	}

	/**
	 * Ascertains if the current <code>User</code> 
	 * is the default administrator that ships with the system.
	 * 
	 * @see #hasAdministrativePrivileges() to check if a user
	 * is an administrator (sees if a user has the admin role).
	 * 
	 * @return <code>true if(user is default administrator)</code>
	 */
	public boolean isDefaultAdministrator() {
		boolean defaultAdmin = false;		
		if(this.firstName != null && this.middleName != null){
			if(this.name.equalsIgnoreCase("admin") || this.firstName.equalsIgnoreCase("administrator") ||
					this.middleName.equalsIgnoreCase("administrator")) 
					defaultAdmin = true;
		}
		return defaultAdmin;
		
		
	}

    /**
     * Checks if this <code>User</code> has a given <code>Role</code>.
     * 
     * @param role <code>Role</code> to check.
     * @return <code>True only and only if Role exists in the list of roles assigned to User.</code>
     */
    public synchronized boolean hasRole(Role role){
    	synchronized(this){
    		if (roles != null) {
	        	for(Role xRole : roles){
	        		if(xRole.getName().equalsIgnoreCase(role.getName()))
	        			return true;
	        	}
    		}
    		return false;
    	}
    }
}