package org.openxdata.server.admin.client.presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.Inject;
import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.BasePropertyDisplay;
import org.openxdata.server.admin.client.view.event.ItemSelectedEvent;
import org.openxdata.server.admin.client.view.event.PresenterChangeEvent;
import org.openxdata.server.admin.client.view.event.EditableEvent;
import org.openxdata.server.admin.client.view.event.ViewEvent;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.User;

/**
 *
 * @author kay
 */
public class UserPresenter implements IPresenter<UserPresenter.Display> {

    public interface Display extends BasePropertyDisplay.Interface {

        public HasText getUserName();

        public HasText getFName();

        public HasText getMiddleName();

        public HasText getLastName();

        public TextBox getEmail();

        public HasText getPhoneNumber();

        public PasswordTextBox getPassword();

        public PasswordTextBox getConfirmPassword();

        public HasText getSecretQuestion();

        public HasText getSecretAnswer();

        public ListBox getUserStatus();

        public void setUser(User user);

        public void setEnabled(boolean enabled);
    }
    private UserReportGroupMapPresenter groupMapPresenter;
    private UserReportMapPresenter reportMapPresenter;
    private UserStudyMapPresenter userStudyMapPresenter;
    private UserFormMapPresenter formMapPresenter;
    private UserRoleMapPresenter userMapPresenter;
    private IPresenter<Display> thisPresenter = this;
    private EventBus eventBus;
    private Display display;
    private User user;

    @Inject
    public UserPresenter(EventBus eventBus, Display display,
            UserRoleMapPresenter userMapPresenter,
            UserStudyMapPresenter userStudyMapPresenter,
            UserFormMapPresenter formMapPresenter,
            UserReportGroupMapPresenter groupMapPresenter,
            UserReportMapPresenter reportMapPresenter) {

        GWT.log("Initialing User Presenter");
        this.display = display;
        this.eventBus = eventBus;
        this.userMapPresenter = userMapPresenter;
        this.userStudyMapPresenter = userStudyMapPresenter;
        this.groupMapPresenter = groupMapPresenter;
        this.formMapPresenter = formMapPresenter;
        this.reportMapPresenter = reportMapPresenter;
        if (permissionResolver.isViewPermission(Permission.PERM_VIEW_USERS)) {
            if (permissionResolver.isEditPermission(Permission.PERM_EDIT_USERS))
                bindUI();
            else
                display.disableAll();

        } else {
            display.showNoPermissionView();
        }
        bindHandlers();

    }

    private void bindUI() {
        display.addGeneralChangeHadler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                syncChange();
            }
        });

        display.getPassword().addBlurHandler(new BlurHandler() {

            @Override
            public void onBlur(BlurEvent event) {
                validPassword(display.getPassword().getText());
            }
        });

        display.getConfirmPassword().addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                syncPassword();
            }
        });

        display.getEmail().addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                syncEmail();
            }
        });
        display.getUserStatus().addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                 syncChange();
            }
        });
        addMappingTabs();

    }

    private void addMappingTabs() {
        if (!permissionResolver.isAddUsers()) return;

        if (permissionResolver.isAddRoles())
            display.addTab(userMapPresenter.getDisplay(), "User Role");


        if (permissionResolver.isAddStudies() && user != null && !user.isNew())
            display.addTab(userStudyMapPresenter.getDisplay(), "User Study Permissions");
        else
            display.removeTab(userStudyMapPresenter.getDisplay());


        if (permissionResolver.isAddPermission(Permission.PERM_ADD_FORMS) && user != null && !user.isNew())
            display.addTab(formMapPresenter.getDisplay(), "User Form Permissions");
        else
            display.removeTab(formMapPresenter.getDisplay());


        if (permissionResolver.isAddReportGroups() && user != null && !user.isNew())
            display.addTab(groupMapPresenter.getDisplay(), "User ReportGroup Permissions");
        else
            display.removeTab(groupMapPresenter.getDisplay());

        if (permissionResolver.isAddPermission(Permission.PERM_ADD_REPORTS) && user != null && !user.isNew())
            display.addTab(reportMapPresenter.getDisplay(), "User Report Map");
        else
            display.removeTab(reportMapPresenter.getDisplay());
    }

    private void bindHandlers() {
        ItemSelectedEvent.addHandler(eventBus, new ItemSelectedEvent.Handler<User>() {

            @Override
            public void onSelected(Composite sender, User item) {
                setUser(item);
            }
        }).forClass(User.class);

        ViewEvent.addHandler(eventBus, new ViewEvent.Handler<User>() {

            @Override
            public void onView() {
                eventBus.fireEvent(new PresenterChangeEvent(thisPresenter));
            }
        }).forClass(User.class);
    }

    private void setUser(User user) {
        this.user = user;
        addMappingTabs();
        display.setUser(user);
    }

    private void syncChange() {

        String userName = display.getUserName().getText();
        if (!userName.isEmpty())
            user.setName(userName);
        else
            display.getUserName().setText(user.getName());
        user.setFirstName(display.getFName().getText());
        user.setMiddleName(display.getMiddleName().getText());
        user.setLastName(display.getLastName().getText());
        user.setPhoneNo(display.getPhoneNumber().getText());
        user.setSecretQuestion(display.getSecretQuestion().getText());
        user.setSecretAnswer(display.getSecretAnswer().getText());
        user.setDirty(true);
        ListBox lstUserStatus = display.getUserStatus();
        user.setStatusAfterGiveType(lstUserStatus.getItemText(lstUserStatus.getSelectedIndex()));
        eventBus.fireEvent(new EditableEvent<User>(user));
    }

    private void syncPassword() {
        String password = display.getPassword().getText();
        if (!validPassword(password)) return;

        String confirmPassword = display.getConfirmPassword().getText();
        if (!password.equals(confirmPassword)) {
            Utilities.displayMessage("The Passwords entered do not match! Please check and rectify to proceed!");
            display.setEnabled(false);
            display.getConfirmPassword().setEnabled(true);
            display.getPassword().setEnabled(true);
            display.getConfirmPassword().setText("");
            return;
        }
        user.setClearTextPassword(password);
        user.setDirty(true);
        eventBus.fireEvent(new EditableEvent<User>(user));
    }

    private boolean validPassword(String password) {
        display.setEnabled(true);
        String minPasswordLength = Context.getSetting(
                "defaultPasswordLength", "6");
        int length = Integer.parseInt(minPasswordLength);
        if (password.isEmpty()) return true;
        if (password.length() >= length)
            return true;
        display.setEnabled(false);
        String message = "The User password specified is less than the default length that is specified in the system. "
                + "The Password should be equal or more than "
                + Context.getSetting("defaultUserPasswordLength",
                "6") + " characters.";
        display.getPassword().setEnabled(true);
        display.getConfirmPassword().setText("");
        Utilities.displayMessage(message);
        return false;
    }

    private void syncEmail() {
        String email = display.getEmail().getText();
        if (Utilities.validateEmail(email)) {
            user.setEmail(email);
            user.setDirty(true);
            eventBus.fireEvent(new EditableEvent<User>(user));
        } else {
            Utilities.displayMessage("The email address is not a valid. Check to see if it is of the required pattern.");
        }
    }

    @Override
    public Display getDisplay() {
        return display;
    }
}
