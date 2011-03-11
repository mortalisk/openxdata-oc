package org.openxdata.server.admin.client.view.mapping;

import org.openxdata.server.admin.client.permissions.UIViewLabels;
import org.openxdata.server.admin.client.presenter.UserRoleMapPresenter;
import org.openxdata.server.admin.model.Role;

/**
 *
 * @author kay
 */
public class UserRoleMapDisplay extends BaseMapDiplay<Role> implements UserRoleMapPresenter.Display {

    public UserRoleMapDisplay() {
        super();
        table.remove(btnSave);
    }

    @Override
    protected String getName(Role role) {
        return role.getName();
    }

    @Override
    protected UIViewLabels getMapViewLabels() {
        UIViewLabels labels = new UIViewLabels();
        labels.setLabel("User Roles");
        labels.setRightListBoxLabel("System Roles");
        labels.setLeftListBoxLabel("User Roles");
        labels.setMapButtonText("Add Role");
        labels.setUnMapButtonText("Remove Role");
        labels.setAddButtonTitle("Adds the selected Role to the User.");
        labels.setRemoveButtonTitle("Removes the selected Role from the User.");
        return labels;
    }
}
