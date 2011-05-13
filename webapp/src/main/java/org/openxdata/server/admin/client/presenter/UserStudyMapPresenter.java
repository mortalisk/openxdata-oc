package org.openxdata.server.admin.client.presenter;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.openxdata.server.admin.client.controller.callback.OpenXDataAsyncCallback;
import org.openxdata.server.admin.client.controller.callback.SaveAsyncCallback;
import org.openxdata.server.admin.client.controller.facade.MainViewControllerFacade;
import org.openxdata.server.admin.client.permissions.util.RolesListUtil;
import org.openxdata.server.admin.client.service.StudyManagerServiceAsync;
import org.openxdata.server.admin.client.service.UserServiceAsync;
import org.openxdata.server.admin.client.view.event.LoadRequetEvent;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.Role;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserStudyMap;

/**
 *
 * @author kay
 */
public class UserStudyMapPresenter extends ExtendedBaseMapPresenter<UserStudyMap, User, StudyDef> {

    public interface Display extends BaseDisplay<StudyDef> {
    }
    private StudyManagerServiceAsync studyService;
    private UserServiceAsync userService;

    @Inject
    public UserStudyMapPresenter(EventBus eventBus, Display display) {
        super(display, eventBus, User.class, StudyDef.class, UserStudyMap.class);
        studyService = StudyManagerServiceAsync.Util.getInstance();
        userService = UserServiceAsync.Util.getInstance();
        deletedMaps = new ArrayList<UserStudyMap>();
    }

    @Override
    protected void onMapSaveComplete(List<? extends Editable> studiesAdded) {
        Role added = null;
        if (!selectedItem.hasRole(new Role("Role_View_Studies_")) && !studiesAdded.isEmpty())
            added = RolesListUtil.addViewStudiesRoleOnStudyMap(selectedItem);

//        List<UserStudyMap> userMappedStudies = permissionResolver.getUserMappedStudies(user, userStudyMaps);
//        if(userMappedStudies.isEmpty())
//            Role roleRemoved = RolesListUtil.removeViewStudiesRoleAddedOnMap(user);

        if (added == null) {
            MainViewControllerFacade.loadAllUserMappedStudies(true);
            return;
        }
        saveUser(added);
    }

    private void saveUser(final Role finalRole) {
        userService.saveUser(selectedItem, new OpenXDataAsyncCallback<Void>() {

            @Override
            public void onSuccess(Void result) {
                if (finalRole.isNew()) {
                    //Reload both the users and the roles to synchronise with the DB
                    eventBus.fireEvent(new LoadRequetEvent<Role>(Role.class));
                    eventBus.fireEvent(new LoadRequetEvent<User>(User.class));
                }
                selectedItem.setDirty(false);
                MainViewControllerFacade.loadAllUserMappedStudies(true); //TODO: This should be done through and evenBus
            }
        });
    }

    @Override
    protected boolean isMappedToItem(User user, StudyDef studyDef) {
        if (user.hasAdministrativePrivileges()) return true;
        List<UserStudyMap> userMappedStudies = permissionResolver.getUserMappedStudies(user, maps);
        for (UserStudyMap userStudyMap : userMappedStudies) {
            if (userStudyMap.getStudyId() == studyDef.getId())
                return true;
        }
        return false;

    }

    @Override
    public UserStudyMap searchMap(int userId, int studyId, List<UserStudyMap> userStudyMaps) {
        for (UserStudyMap userStudyMap : userStudyMaps) {
            if (userStudyMap.getUserId() == userId && userStudyMap.getStudyId() == studyId)
                return userStudyMap;
        }
        return null;
    }

    @Override
    protected UserStudyMap createNewMap(User selectedItem, StudyDef systemItem) {
        UserStudyMap userStudyMap = new UserStudyMap();
        userStudyMap.addStudy(systemItem);
        userStudyMap.addUser(selectedItem);
        return userStudyMap;
    }

    @Override
    protected void persistDelete(UserStudyMap userStudyMap, SaveAsyncCallback saveAsync) {
        studyService.deleteUserMappedStudy(userStudyMap, saveAsync);
    }

    @Override
    protected void persistSave(UserStudyMap userStudyMap, SaveAsyncCallback saveAsync) {
        studyService.saveUserMappedStudy(userStudyMap, saveAsync);
    }
}
