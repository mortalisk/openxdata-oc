package org.openxdata.server.admin.client.view.mapping;

import org.openxdata.server.admin.client.permissions.UIViewLabels;
import org.openxdata.server.admin.client.presenter.RolePermissionMapPresenter;
import org.openxdata.server.admin.model.Permission;

/**
 *
 * @author kay
 */
public class RolePermissionMapDisplay extends BaseMapDiplay<Permission> implements
RolePermissionMapPresenter.Display{

    public RolePermissionMapDisplay() {
        super();
        table.remove(btnSave);
    }



    @Override
    protected UIViewLabels getMapViewLabels() {
        UIViewLabels labels = new UIViewLabels();
        labels.setLabel("Role Permissions");
        labels.setRightListBoxLabel("System Permissions");
        labels.setLeftListBoxLabel("Role  Permissions");
        labels.setMapButtonText("Add Permission");
        labels.setUnMapButtonText("Remove Permission");
        labels.setAddButtonTitle("Adds the selected Permission to the Role.");
        labels.setRemoveButtonTitle("Removes the selected Permission from the Role.");
        return labels;
    }

    @Override
    protected String getName(Permission role) {
        return role.getName().replaceFirst("Perm_", "");
    }
}
