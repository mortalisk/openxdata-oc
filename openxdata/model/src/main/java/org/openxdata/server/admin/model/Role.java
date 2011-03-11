package org.openxdata.server.admin.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class model a set of related permission that could be assigned to user
 * to carry out a particular task. An example could be a Data Entry role which
 * could have permissions like View Form Data, Edit Form Data, Delete Form Data,
 * and possibly more.
 */
public class Role extends AbstractEditable {

	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = 5331693197915876841L;

	/** The database identifier of the role. */
	private int roleId = 0;

	/** The name of the role. */
	private String name;

	/**
	 * The description of the role. This just throws some more light about what
	 * the role does.
	 */
	private String description;

	/** The set of permissions that the role has. */
	private List<Permission> permissions;

	private List<User> users;

	/**
	 * Constructs a new role object.
	 */
	public Role() {
		permissions = new ArrayList<Permission>();
	}

	/**
	 * Constructs a new role object with a given name.
	 * 
	 * @param name
	 *            the name of the role.
	 */
	public Role(String name) {
		this();
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRoleId() {
		return roleId;
	}

	@Override
	public int getId() {
		return roleId;
	}
	
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	@Override
	public boolean isNew() {
		return roleId == 0;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	public void addPermission(Permission permission) {
		this.permissions.add(permission);
	}

	/**
	 * Returns the users associated to this Role
	 * 
	 * @return the users
	 */
	public List<User> getUsers() {
		return users;
	}

	/**
	 * @param users
	 *            the users to set
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}

	public void addPermissions(List<Permission> permissions) {
		for (Permission x : permissions) {
			if (!this.permissions.contains(x))
				this.permissions.add(x);
		}
	}

	public void removePermission(Permission permission) {
		for (Permission x : permissions) {
			if (x.getName().equals(permission.getName())) {
				permissions.remove(x);
				break;
			}
		}
	}

	
	/**
	 * Checks if the given <code>Role</code> is the default administrator <code>Role</code> that ships with the system.
	 * @param role <code>Role</code> to check.
	 * @return <code>True only and only if role.getName().equals("Role_Administrator")
	 */
	public boolean isDefaultAdminRole() {
		return this.getName().equals("Role_Administrator");
	}
}
