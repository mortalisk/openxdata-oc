package org.openxdata.server.admin.client.view.treeview;

import org.openxdata.server.admin.client.presenter.tree.UsersListPresenter;
import org.openxdata.server.admin.model.User;

/**
 *
 * @author kay
 */
//TODO this view will replace user tree view
public class UsersTreeDisplay extends BaseTreeDisplay<User> implements UsersListPresenter.Display {

    @Override
    public String getDisplayLabel(User user) {
        return user.getName();
    }

    @Override
    protected String getTooltip(User user) {
        return user.getFullName();
    }

    @Override
    protected String getTreeName() {
        return displayName;

    }
}
