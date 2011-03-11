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
        txtName = new TextBox();
        txtDescription = new TextBox();

        table.setWidget(0, 0, new Label(constants.label_name()));

        table.setWidget(1, 0, new Label(constants.label_description()));

        table.setWidget(0, 1, txtName);
        table.setWidget(1, 1, txtDescription);

        txtName.setWidth("100%");
        txtDescription.setWidth("100%");

        FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
        cellFormatter.setWidth(0, 0, "20%");

        table.getRowFormatter().removeStyleName(0, "FlexTable-Header");
        Utilities.maximizeWidget(table);
        tabs.add(table, "Role Properties");
        Utilities.maximizeWidget(tabs);

        tabs.selectTab(0);
        super.setUpKeyHandlers();
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
