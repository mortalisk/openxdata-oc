package org.openxdata.server.admin.client.presenter;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.inject.Inject;
import org.openxdata.server.admin.client.view.BasePropertyDisplay;
import org.openxdata.server.admin.client.view.event.EditableEvent;
import org.openxdata.server.admin.client.view.event.ItemSelectedEvent;
import org.openxdata.server.admin.client.view.event.PresenterChangeEvent;
import org.openxdata.server.admin.client.view.event.ViewEvent;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.Role;

/**
 *
 * @author kay
 */
public class RolePresenter implements IPresenter<RolePresenter.Display> {

    private final RolePermissionMapPresenter permissionMapPresenter;

    public interface Display extends BasePropertyDisplay.Interface {

        public HasText getName();

        public HasText getDescription();

        public void setRole(Role role);
    }
    private Display display;
    private Role role;
    private EventBus eventBus;
    private RolePresenter thisPresenter = this;

    @Inject
    public RolePresenter(Display display, EventBus eventBus, RolePermissionMapPresenter permissionMapPresenter) {
        this.display = display;
        this.eventBus = eventBus;
        this.permissionMapPresenter = permissionMapPresenter;
        if (permissionResolver.isViewPermission(Permission.PERM_VIEW_ROLES)) {
            if (permissionResolver.isEditPermission(Permission.PERM_EDIT_ROLES))
                bindUI();
            else
                display.disableAll();

        } else {
            display.showNoPermissionView();
        }
        bindHandlers();
    }

    private void bindUI() {
        ValueChangeHandler<String> valueChangeHandler = new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                if (!event.getValue().isEmpty())
                    syncChange();
            }
        };
        display.addGeneralChangeHadler(valueChangeHandler);
        if(permissionResolver.isPermission(Permission.PERM_EDIT_ROLE_PERMISSIONS))
            display.addTab(permissionMapPresenter.getDisplay(), CONSTANTS.label_rolesmanagement());
    }

    private void bindHandlers() {
        ItemSelectedEvent.addHandler(eventBus, new ItemSelectedEvent.Handler<Role>() {

            @Override
            public void onSelected(Composite sender, Role item) {
                setRole(item);
            }
        }).forClass(Role.class);
        ViewEvent.addHandler(eventBus, new ViewEvent.Handler<Role>() {

            @Override
            public void onView() {
                eventBus.fireEvent(new PresenterChangeEvent(thisPresenter));
            }
        }).forClass(Role.class);
    }

    private void syncChange() {
        role.setName(display.getName().getText());
        role.setDescription(display.getDescription().getText());
        eventBus.fireEvent(new EditableEvent<Role>(role));
    }

    public void setRole(Role role) {
        display.setRole(role);
        this.role = role;
    }

    @Override
    public Display getDisplay() {
        return display;
    }
}
