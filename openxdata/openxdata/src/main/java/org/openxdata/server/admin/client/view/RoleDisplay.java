package org.openxdata.server.admin.client.view;

import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import org.openxdata.server.admin.client.presenter.RolePresenter;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.model.Role;

/**
 *
 * @author kay
 */
public class RoleDisplay extends BasePropertyDisplay implements RolePresenter.Display {

    private TextBox txtName;
    private TextBox txtDescription;

    public RoleDisplay() {
        init();
    }

    private void init() {
        txtName = addTextProperty(constants.label_name());
        txtDescription = addTextProperty(constants.label_description());
        super.init("Role Properties");
    }

    @Override
    public HasText getName() {
        return txtName;
    }

    @Override
    public HasText getDescription() {
        return txtDescription;
    }

    @Override
    public void setRole(Role role) {
        txtName.setText(role.getName());
        txtDescription.setText(role.getDescription());
    }
}
