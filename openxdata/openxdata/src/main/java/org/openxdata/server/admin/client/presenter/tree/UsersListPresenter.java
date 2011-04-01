package org.openxdata.server.admin.client.presenter.tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.controller.callback.OpenXDataAsyncCallback;
import org.openxdata.server.admin.client.controller.callback.SaveAsyncCallback;
import org.openxdata.server.admin.client.locale.OpenXdataText;
import org.openxdata.server.admin.client.locale.TextConstants;
import org.openxdata.server.admin.client.service.UserServiceAsync;
import org.openxdata.server.admin.client.util.MainViewControllerUtil;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.event.EditableEvent;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.User;
import org.purc.purcforms.client.util.FormUtil;

/**
 *
 * @author kay
 */
public class UsersListPresenter extends BaseTreePresenter<User, IBaseTreeDisplay<User>> {

    public interface Display extends IBaseTreeDisplay<User> {

        public static String displayName = "Users";
    }
    private UserServiceAsync userService;

    @Inject
    public UsersListPresenter(EventBus eventBus, Display display) {
        super(display, eventBus, User.class);
        userService = UserServiceAsync.Util.getInstance();
    }

    @Override
    protected User getNewItem() {
        User user = new User(CONSTANTS.label_new_user());
        user.setCreator(Context.getAuthenticatedUser());
        user.setDateCreated(new Date());
        user.setDirty(true);
        return user;
    }

    @Override
    protected boolean canView() {
        return permissionResolver.isViewPermission(Permission.PERM_VIEW_USERS);
    }

    @Override
    protected boolean canDelete() {
        User selected = display.getSelected();
        if (selected != null && selected.isNew())
            return true;
        Utilities.displayMessage("You cannot delete already Saved Users because the System"
                + " uses them for other system functions like logging. "
                + "Disabling a User is the recommended practice.");
        return false;
    }

    @Override
    protected boolean canAdd() {
        return permissionResolver.isAddUsers();
    }

    @Override
    protected void loadItems() {
        Utilities.showProgress(OpenXdataText.get(TextConstants.LOADING_USERS));
        GWT.log("UsersListPresenter : loadAndShowUsers" + OpenXdataText.get(TextConstants.LOADING_USERS));
        userService.getUsers(new OpenXDataAsyncCallback<List<User>>() {

            @Override
            public void onSuccess(List<User> result) {
                replaceAuthenticatedUser(result);
                FormUtil.dlg.hide();
            }
        });
    }

    private void replaceAuthenticatedUser(List<User> result) {
        for (int i = 0; i < result.size(); i++) {
            User user = result.get(i);
            if (user.getUserId() == Context.getAuthenticatedUser().getUserId()) {
                user = Context.getAuthenticatedUser();
                result.set(i, user);
            }
        }
        eventBus.fireEvent(new EditableEvent<User>(result, User.class));
        setItems(result);
    }

    @Override
    protected String getFailedMessage() {
        return OpenXdataText.get(TextConstants.PROBLEM_SAVING_USERS);
    }

    @Override
    protected boolean saveDirtyItems(List<User> dirtyItems, SaveAsyncCallback callback) {
        if (!validate()) return false;
        for (User user : dirtyItems) {
            callback.setCurrentItem(user);
            MainViewControllerUtil.setEditableProperties(user);
            userService.saveUser(user, callback);
        }
        return true;
    }

    private boolean validate() {
        Map<String, User> map = new HashMap<String, User>();

        for (User user : super.items) {
            if (!Utilities.validateUserPassword(user)) {
                String error =
                        "The User password specified is less than the default length that is specified in the system. "
                        + "The Password should be equal or more than " + Context.getSetting("defaultUserPasswordLength", "6")
                        + " characters. The User will not be saved!";
                Utilities.displayMessage(error);
                return false;
            }
            String name = user.getName().toLowerCase();
            if (map.containsKey(name)) {//Check for duplicate user name
                Utilities.displayMessage(CONSTANTS.label_existinguser() + user.getName() + ". Cannot Continue!");
                return false;
            } else {
                map.put(name, user);
            }
        }
        return true;
    }

    @Override
    protected void persistDelete(List<User> deletedItems, SaveAsyncCallback callback) {
        for (User user : deletedItems) {
            callback.setCurrentItem(user);
            callback.onSuccess(null);//just simulate deleting to let the callaback continue counting
        }
        deletedItems.clear();
    }

    @Override
    protected String getSuccessMessage() {
        return OpenXdataText.get(TextConstants.USERS_SAVED_SUCCESSFULLY);
    }
}
