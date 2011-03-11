package org.openxdata.server.admin.client.presenter;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Set;
import org.openxdata.server.admin.model.Role;
import org.openxdata.server.admin.model.User;

/**
 *
 * @author kay
 */
public class UserRoleMapPresenter extends BaseMapPresenter<User, Role> {

    public interface Display extends BaseDisplay<Role> {
    }

    @Inject
    public UserRoleMapPresenter(EventBus eventBus, Display display) {
        super(display, eventBus, User.class, Role.class);
    }

    @Override
    protected void save() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected boolean mapToItem(Role systemItem) {
        selectedItem.addRole(systemItem);
        return true;
    }

    @Override
    protected boolean unMapItem(Role userItem) {
        selectedItem.removeRole(userItem);
        return true;
    }

    @Override
    protected ArrayList<Role> getSelectedItemzMap(User user) {
        final Set<Role> roles = user.getRoles();
        if (roles != null)
            return new ArrayList<Role>(roles);
        else return new ArrayList<Role>();
    }
}
