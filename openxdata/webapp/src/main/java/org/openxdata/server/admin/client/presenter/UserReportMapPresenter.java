package org.openxdata.server.admin.client.presenter;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import org.openxdata.server.admin.client.controller.callback.OpenXDataAsyncCallback;
import org.openxdata.server.admin.client.controller.callback.SaveAsyncCallback;
import org.openxdata.server.admin.client.controller.facade.MainViewControllerFacade;
import org.openxdata.server.admin.client.permissions.util.RolesListUtil;
import org.openxdata.server.admin.client.service.ReportServiceAsync;
import org.openxdata.server.admin.client.service.UserServiceAsync;
import org.openxdata.server.admin.client.view.event.EditableEvent;
import org.openxdata.server.admin.client.view.event.LoadRequetEvent;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.Report;
import org.openxdata.server.admin.model.ReportGroup;
import org.openxdata.server.admin.model.Role;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserReportMap;

/**
 *
 * @author kay
 */
public class UserReportMapPresenter extends ExtendedBaseMapPresenter<UserReportMap, User, Report> {

    public interface Display extends BaseDisplay<Report> {
    }
    private ReportServiceAsync reportService;
    private UserServiceAsync userService;

    @Inject
    public UserReportMapPresenter(EventBus eventBus, Display display) {
        super(display, eventBus, User.class, Report.class, UserReportMap.class);
        reportService = ReportServiceAsync.Util.getInstance();
        userService = UserServiceAsync.Util.getInstance();
        bindHandlers();
    }

    private void bindHandlers() {
        EditableEvent.addHandler(eventBus, new EditableEvent.HandlerAdaptor<ReportGroup>() {

            @Override
            public void onLoaded(List<ReportGroup> items) {
                loadReport(items);
            }
        }).forClass(ReportGroup.class);
    }

    @Override
    protected boolean isMappedToItem(User user, Report itemToMap) {
        if (user.hasAdministrativePrivileges()) return true;
        List<UserReportMap> userReportMaps = permissionResolver.getUserMappedReports(user, maps);
        for (UserReportMap userReportMap : userReportMaps) {
            if (userReportMap.getReportId() == itemToMap.getReportId())
                return true;
        }
        return false;
    }

    @Override
    protected UserReportMap createNewMap(User selectedItem, Report systemItem) {
        UserReportMap reportMap = new UserReportMap();
        reportMap.addUser(selectedItem);
        reportMap.addReport(systemItem);
        return reportMap;
    }

    @Override
    protected void onMapSaveComplete(List<? extends Editable> mapsAdded) {
        Role added = null;
        if (!selectedItem.hasRole(new Role("Role_View_Reports_")) && !mapsAdded.isEmpty())
            added = RolesListUtil.addViewReportsRoleOnReportMap(selectedItem);

        if (added == null) {
            MainViewControllerFacade.loadAllUserMappedReports(true);
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
                MainViewControllerFacade.loadAllUserMappedReports(true);
            }
        });
    }

    @Override
    protected void persistDelete(UserReportMap userReport, SaveAsyncCallback saveAsync) {
        reportService.deleteUserMappedReport(userReport, saveAsync);
    }

    @Override
    protected void persistSave(UserReportMap userReport, SaveAsyncCallback saveAsync) {
        reportService.saveUserMappedReport(userReport, saveAsync);
    }

    @Override
    public UserReportMap searchMap(int userId, int studyId, List<UserReportMap> userStudyMaps) {
        for (UserReportMap userReportMap : userStudyMaps) {
            if (userReportMap.getUserId() == userId && userReportMap.getReportId() == studyId)
                return userReportMap;
        }
        return null;
    }

    private void loadReport(List<ReportGroup> groups) {
        List<Report> reports = new ArrayList<Report>();
        for (ReportGroup reportGroup : groups) {
            if (reportGroup.getReports() != null)
                reports.addAll(reportGroup.getReports());
        }
        setSystemItemsToMap(reports);
    }
}
