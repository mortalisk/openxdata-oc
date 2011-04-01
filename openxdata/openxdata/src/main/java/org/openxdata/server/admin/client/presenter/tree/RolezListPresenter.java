package org.openxdata.server.admin.client.presenter.tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import java.util.List;
import org.openxdata.server.admin.client.controller.callback.OpenXDataAsyncCallback;
import org.openxdata.server.admin.client.controller.callback.SaveAsyncCallback;
import org.openxdata.server.admin.client.listeners.RoleMappedListener;
import org.openxdata.server.admin.client.locale.OpenXdataText;
import org.openxdata.server.admin.client.locale.TextConstants;
import org.openxdata.server.admin.client.permissions.util.RolesMappingUtil;
import org.openxdata.server.admin.client.service.RoleServiceAsync;
import org.openxdata.server.admin.client.util.MainViewControllerUtil;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.event.EditableEvent;
import org.openxdata.server.admin.client.view.event.LoadRequetEvent;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.Role;
import org.purc.purcforms.client.util.FormUtil;

/**
 *
 * @author kay
 */
public class RolezListPresenter extends BaseTreePresenter<Role, RolezListPresenter.Display> {

    public interface Display extends IBaseTreeDisplay<Role> {

        public static String diplayName = "Roles";
    }
    private RoleServiceAsync roleService;

    @Inject
    public RolezListPresenter(EventBus eventBus, Display display) {
        super(display, eventBus, Role.class);
        roleService = RoleServiceAsync.Util.getInstance();
        bindHandler();
    }

    private void bindHandler() {
        LoadRequetEvent.addHandler(eventBus, new LoadRequetEvent.Handler<Permission>() {

            @Override
            public void onLoadRequest() {
                loadPermisions();
            }
        }).forClass(Permission.class);
    }

    @Override
    protected void loadItems() {
        Utilities.showProgress(OpenXdataText.get(TextConstants.LOADING_ROLES));
        OpenXDataAsyncCallback<List<Role>> callback = new OpenXDataAsyncCallback<List<Role>>() {

            @Override
            public void onSuccess(List<Role> result) {
                setItems(result);
                eventBus.fireEvent(new EditableEvent<Role>(result, Role.class));
                //Set the roles in Context for classes which depend on it
                //TODO: This should not be done.All classes should listen to role events through the
                //eventBus
                org.openxdata.server.admin.client.Context.setRoles(result);

                FormUtil.dlg.hide();
            }
        };
        roleService.getRoles(callback);
        loadPermisions();
    }

    private void loadPermisions() {
        Utilities.showProgress(OpenXdataText.get(TextConstants.LOADING_PERMISSIONS));
        OpenXDataAsyncCallback<List<Permission>> callback = new OpenXDataAsyncCallback<List<Permission>>() {

            @Override
            public void onSuccess(List<Permission> result) {
                eventBus.fireEvent(new EditableEvent<Permission>(result, Permission.class));
                FormUtil.dlg.hide();
            }
        };
        roleService.getPermissions(callback);
    }

    @Override
    protected boolean saveDirtyItems(List<Role> dirtyItems, SaveAsyncCallback callback) {
        for (Role role : dirtyItems) {
            callback.setCurrentItem(role);
            MainViewControllerUtil.setEditableProperties(role);
            roleService.saveRole(role, callback);
        }
        return true;
    }

    @Override
    protected void persistDelete(List<Role> deletedItems, SaveAsyncCallback callback) {
        for (Role role : deletedItems) {
            callback.setCurrentItem(role);
            roleService.deleteRole(role, callback);
        }
        deletedItems.clear();
    }

    @Override
    protected boolean beforeDeleteItem(final Role selected) {
        new RolesMappingUtil(new RoleMappedListener() {

            @Override
            public void isMappedToUser(boolean isMappedToUser) {
                if (isMappedToUser) {
                    Utilities.displayMessage("Cannot Delete A Role Mapped To User");
                } else {
                    deletedItems.add(selected);
                    items.remove(selected);
                    display.delete(selected);
                }
            }
        }).checkRoleMapping(selected);
        GWT.log("Returning true on beforeDeleteItem");
        return true;
    }

    @Override
    protected Role getNewItem() {
        return new Role(CONSTANTS.label_newrole());
    }

    @Override
    protected String getSuccessMessage() {
        return OpenXdataText.get(TextConstants.ROLES_SAVED_SUCCESSFULLY);
    }

    @Override
    protected String getFailedMessage() {
        return OpenXdataText.get(TextConstants.PROBLEM_SAVING_ROLES);
    }

    @Override
    protected boolean canDelete() {
        if (display.getSelected().isDefaultAdminRole()) {
            Utilities.displayMessage("Cannot delete default admin role");
            return false;
        }
        boolean canDelete = permissionResolver.isDeleteRoles();
        if (!canDelete)
            Utilities.displayMessage("You do not have sufficient priviledges to "
                    + "delete Roles! Contact your system administrator");
        return canDelete;
    }

    @Override
    protected boolean canView() {
        return permissionResolver.isViewPermission(Permission.PERM_VIEW_ROLES);
    }

    @Override
    protected boolean canAdd() {
        boolean canAdd = permissionResolver.isAddRoles();
        if (!canAdd)
            Utilities.displayMessage("You do not have sufficient priviledges Add Roles");
        return canAdd;
    }
}
