package org.openxdata.server.admin.client.presenter;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import java.util.List;
import org.openxdata.server.admin.client.permissions.util.RolesListUtil;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.Role;

/**
 *
 * @author kay
 */
public class RolePermissionMapPresenter extends BaseMapPresenter<Role, Permission> {

    public interface Display extends BaseDisplay<Permission> {
    }

    @Inject
    public RolePermissionMapPresenter(EventBus eventBus, Display display) {
        super(display, eventBus, Role.class, Permission.class);
    }

    @Override
    protected boolean unMapItem(Permission itemMapped) {
        super.selectedItem.removePermission(itemMapped);
        return true;
    }

    @Override
    protected boolean mapToItem(Permission systemItem) {
        super.selectedItem.addPermission(systemItem);
        List<Permission> ancillaryPerms = RolesListUtil.checkAndBindAncillaryPermissions(systemItem, getSystemItems(), selectedItem.getPermissions());
        super.selectedItem.addPermissions(ancillaryPerms);
        return true;
    }

    @Override
    protected void save() {
    }

    @Override
    protected List<Permission> getSelectedItemzMap(Role selectedItem) {
        return selectedItem.getPermissions();
    }
}
