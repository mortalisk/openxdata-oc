package org.openxdata.server.admin.client.view;

import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import org.openxdata.server.admin.client.permissions.util.RolesListUtil;
import org.openxdata.server.admin.client.presenter.UserPresenter;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.User;

/**
 *
 * @author kay
 */
public class UserDisplay extends BasePropertyDisplay implements UserPresenter.Display{
    /** Widget for modeling the <tt>User Status.</tt> */
    private ListBox cboStatus = new ListBox();
    /** Widget for displaying and entering user email address. */
    private TextBox txtEmail = new TextBox();
    /** Widget for displaying and entering user name. */
    private TextBox txtName = new TextBox();
    /** Widget for displaying and entering user phone number. */
    private TextBox txtPhoneNo = new TextBox();
    /** Widget for displaying and entering user last name. */
    private TextBox txtLastName = new TextBox();
    /** Widget for displaying and entering user first name. */
    private TextBox txtFirstName = new TextBox();
    /** Widget for displaying and entering user middle name. */
    private TextBox txtMiddleName = new TextBox();
    /** Widget for entering user secret answer. */
    private TextBox txtSecretAnswer = new TextBox();
    /** Widget for entering user secret question. */
    private TextBox txtSecretQuestion = new TextBox();
    /** Widget for entering user password. */
    private PasswordTextBox txtPassword = new PasswordTextBox();
    /** Widget for re entering user password. */
    private PasswordTextBox txtConfirmPassword = new PasswordTextBox();

    public UserDisplay() {
        table.setWidget(0, 0, new Label(constants.label_name()));
        table.setWidget(1, 0, new Label(constants.label_first_name()));
        table.setWidget(2, 0, new Label(constants.label_middle_name()));
        table.setWidget(3, 0, new Label(constants.label_last_name()));
        table.setWidget(4, 0, new Label(constants.label_email()));
        table.setWidget(5, 0, new Label(constants.label_phone_no()));
        table.setWidget(6, 0, new Label(constants.label_password()));
        table.setWidget(7, 0, new Label(constants.label_confirm_password()));
        table.setWidget(8, 0, new Label(constants.label_secret_question()));
        table.setWidget(9, 0, new Label(constants.label_secret_answer()));
        table.setWidget(10, 0, new Label(constants.label_user_status()));

        table.setWidget(0, 1, txtName);
        table.setWidget(1, 1, txtFirstName);
        table.setWidget(2, 1, txtMiddleName);
        table.setWidget(3, 1, txtLastName);
        table.setWidget(4, 1, txtEmail);
        table.setWidget(5, 1, txtPhoneNo);
        table.setWidget(6, 1, txtPassword);
        table.setWidget(7, 1, txtConfirmPassword);
        table.setWidget(8, 1, txtSecretQuestion);
        table.setWidget(9, 1, txtSecretAnswer);
        table.setWidget(10, 1, cboStatus);

        txtName.setWidth("100%");
        txtFirstName.setWidth("100%");
        txtMiddleName.setWidth("100%");
        txtLastName.setWidth("100%");
        txtEmail.setWidth("100%");
        txtPhoneNo.setWidth("100%");
        txtPassword.setWidth("100%");
        txtConfirmPassword.setWidth("100%");
        txtSecretQuestion.setWidth("100%");
        txtSecretAnswer.setWidth("100%");
        cboStatus.setWidth("100%");

        bindUserStatusTypes();
        FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
        cellFormatter.setWidth(0, 0, "20%");
        table.getRowFormatter().removeStyleName(0, "FlexTable-Header");
        Utilities.maximizeWidget(table);
        tabs.add(table, constants.label_properties());
        Utilities.maximizeWidget(tabs);
        tabs.selectTab(0);
        setEnabled(false);
        super.setUpKeyHandlers();
        super.addFocusHandlers();
    }

    @Override
    public HasText getUserName() {
        return txtName;
    }

    @Override
    public HasText getFName() {
        return txtFirstName;
    }

    @Override
    public HasText getMiddleName() {
        return txtMiddleName;
    }

    @Override
    public HasText getLastName() {
        return txtLastName;
    }

    @Override
    public TextBox getEmail() {
        return txtEmail;
    }

    @Override
    public HasText getPhoneNumber() {
        return txtPhoneNo;
    }

    @Override
    public PasswordTextBox getPassword() {
        return txtPassword;
    }

    @Override
    public PasswordTextBox getConfirmPassword() {
        return txtConfirmPassword;
    }

    @Override
    public HasText getSecretQuestion() {
        return txtSecretQuestion;
    }

    @Override
    public HasText getSecretAnswer() {
        return txtSecretAnswer;
    }

    @Override
    public ListBox getUserStatus() {
        return cboStatus;
    }

    private void bindUserStatusTypes() {
        for (String type : Utilities.getUserStatusTypes()) {
            cboStatus.addItem(type);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        txtName.setEnabled(enabled);
        txtFirstName.setEnabled(enabled);
        txtMiddleName.setEnabled(enabled);
        txtLastName.setEnabled(enabled);
        txtEmail.setEnabled(enabled);
        txtSecretQuestion.setEnabled(enabled);
        txtSecretAnswer.setEnabled(enabled);
        txtPassword.setEnabled(enabled);
        txtConfirmPassword.setEnabled(enabled);
        txtPhoneNo.setEnabled(enabled);
    }

    @Override
    public void setUser(User user) {
        txtName.setText(user.getName());
        txtFirstName.setText(user.getFirstName());
        txtMiddleName.setText(user.getMiddleName());
        txtLastName.setText(user.getLastName());
        txtEmail.setText(user.getEmail());
        txtSecretQuestion.setText(user.getSecretQuestion());
        txtSecretAnswer.setText(user.getSecretAnswer());
        txtPhoneNo.setText(user.getPhoneNo());
        txtPassword.setText("");
        txtConfirmPassword.setText("");

        // Setting the User Status
        cboStatus.setSelectedIndex(user.getStatus());

        if (RolesListUtil.getPermissionResolver().isEditPermission(Permission.PERM_EDIT_USERS))
            setEnabled(true);
        else
            setEnabled(false);
    }


}
