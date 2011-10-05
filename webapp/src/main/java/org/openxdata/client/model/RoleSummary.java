package org.openxdata.client.model;

import org.openxdata.server.admin.model.Role;

import com.extjs.gxt.ui.client.data.BaseModel;

/**
 * ModelData for the Role data object
 */
public class RoleSummary extends BaseModel {

	private static final long serialVersionUID = -483245623678099579L;
	
	private Role role;

    public RoleSummary() {
    }

    public RoleSummary(Role role) {
        setRole(role);
    }

    public void setId(String id) {
        set("id", id);
    }

    public String getId() {
        return get("id");
    }

    public void setName(String userName) {
        set("name", userName);
    }

    public String getName() {
        return get("name");
    }

    public void setRole(Role role) {
        this.role = role;
        updateRole(role);
    }

    public Role getRole() {
        return role;
    }

    public void updateRole(Role role) {
        setId(String.valueOf(role.getId()));
        setName(role.getName());
    }
}