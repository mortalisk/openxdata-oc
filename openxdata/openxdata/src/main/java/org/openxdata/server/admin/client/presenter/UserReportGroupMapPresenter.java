package org.openxdata.server.admin.client.presenter;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import java.util.List;
import org.openxdata.server.admin.client.controller.callback.OpenXDataAsyncCallback;
import org.openxdata.server.admin.client.controller.callback.SaveAsyncCallback;
import org.openxdata.server.admin.client.controller.facade.MainViewControllerFacade;
import org.openxdata.server.admin.client.permissions.util.RolesListUtil;
import org.openxdata.server.admin.client.service.ReportServiceAsync;
import org.openxdata.server.admin.client.service.UserServiceAsync;
import org.openxdata.server.admin.client.view.event.LoadRequetEvent;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.ReportGroup;
import org.openxdata.server.admin.model.Role;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserReportGroupMap;

/**
 *
 * @author kay
 */
public class UserReportGroupMapPresenter extends ExtendedBaseMapPresenter<UserReportGroupMap, User, ReportGroup> {

    public interface Display extends BaseDisplay<ReportGroup> {
    }
    private ReportServiceAsync reportService;
    private UserServiceAsync userService;

    @Inject
    public UserReportGroupMapPresenter(EventBus eventBus, Display display) {
        super(display, eventBus, User.class, ReportGroup.class, UserReportGroupMap.class);
        reportService = ReportServiceAsync.Util.getInstance();
        userService = UserServiceAsync.Util.getInstance();
    }

    @Override
    protected boolean isMappedToItem(User user, ReportGroup reportGroup) {
        if (user.hasAdministrativePrivileges()) return true;
        List<UserReportGroupMap> userMappedReportGroups = permissionResolver.getUserMappedReportGroups(user, maps);
        for (UserReportGroupMap userReportGroupMap : userMappedReportGroups) {
            if (userReportGroupMap.getReportGroupId() == reportGroup.getReportGroupId())
                return true;
        }
        return false;
    }

    @Override
    protected UserReportGroupMap createNewMap(User selectedItem, ReportGroup systemItem) {
        UserReportGroupMap map = new UserReportGroupMap();
        map.setUserId(selectedItem.getId());
        map.setReportGroupId(systemItem.getId());
        return map;
    }

    @Override
    protected void onMapSaveComplete(List<? extends Editable> studiesAdded) {
        Role added = null;
        if (!selectedItem.hasRole(new Role("Role_View_ReportGroups_")) && !studiesAdded.isEmpty())
            added = RolesListUtil.addViewReportGroupsRoleOnReportGroupMap(selectedItem);

        if (added == null) {
            MainViewControllerFacade.loadAllUserMappedReportGroups(true);
            return;
        }
        saveSelectedUser(added);
    }

    private void saveSelectedUser(final Role finalRole) {
        userService.saveUser(selectedItem, new OpenXDataAsyncCallback<Void>() {

            @Override
            public void onSuccess(Void result) {
                if (finalRole.isNew())
                    eventBus.fireEvent(new LoadRequetEvent<Role>(Role.class));
                eventBus.fireEvent(new LoadRequetEvent<User>(User.class));
                selectedItem.setDirty(false);
                MainViewControllerFacade.loadAllUserMappedReportGroups(true);
            }
        });
    }

    @Override
    protected void persistDelete(UserReportGroupMap userStudyMap, SaveAsyncCallback saveAsync) {
        reportService.deleteUserMappedReportGroup(userStudyMap, saveAsync);
    }

    @Override
    protected void persistSave(UserReportGroupMap userStudyMap, SaveAsyncCallback saveAsync) {
        reportService.saveUserMappedReportGroup(userStudyMap, saveAsync);
    }

    @Override
    public UserReportGroupMap searchMap(int userId, int studyId, List<UserReportGroupMap> userStudyMaps) {
        for (UserReportGroupMap userReportGroupMap : userStudyMaps) {
            if (userReportGroupMap.getUserId() == userId && userReportGroupMap.getReportGroupId() == studyId)
                return userReportGroupMap;
        }
        return null;
    }
}
