package org.openxdata.server.admin.client.view.treeview;

import com.google.gwt.resources.client.ImageResource;
import org.openxdata.server.admin.client.presenter.tree.RolezListPresenter;
import org.openxdata.server.admin.model.Role;

/**
 *
 * @author kay
 */
public class RoleTreeDisplay extends BaseTreeDisplay<Role> implements RolezListPresenter.Display {

    @Override
    protected String getTooltip(Role item) {
        return item.getDescription();
    }

    @Override
    protected String getTreeName() {
        return diplayName;
    }

    @Override
    public String getDisplayLabel(Role item) {
        return item.getName();
    }

    @Override
    protected ImageResource getImage(Role item) {
        return images.role();
    }
}
