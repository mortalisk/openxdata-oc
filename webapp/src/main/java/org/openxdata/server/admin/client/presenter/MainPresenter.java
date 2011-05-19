package org.openxdata.server.admin.client.presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;

import org.openxdata.server.admin.client.OpenXDataAppMessages;
import org.openxdata.server.admin.client.controller.MainViewController;
import org.openxdata.server.admin.client.controller.facade.MainViewControllerFacade;
import org.openxdata.server.admin.client.listeners.StackPanelListener;
import org.openxdata.server.admin.client.permissions.util.RolesListUtil;
import org.openxdata.server.admin.client.presenter.tree.RolezListPresenter;
import org.openxdata.server.admin.client.presenter.tree.SettingListPresenter;
import org.openxdata.server.admin.client.presenter.tree.TasksListPresenter;
import org.openxdata.server.admin.client.presenter.tree.UsersListPresenter;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.MobileInstaller;
import org.openxdata.server.admin.client.view.DatasetView;
import org.openxdata.server.admin.client.view.StudyView;
import org.openxdata.server.admin.client.view.constants.OpenXDataStackPanelConstants;
import org.openxdata.server.admin.client.view.event.LogOutEvent;
import org.openxdata.server.admin.client.view.event.MobileInstallEvent;
import org.openxdata.server.admin.client.view.event.PresenterChangeEvent;
import org.openxdata.server.admin.client.view.event.ViewAppListenerChangeEvent;
import org.openxdata.server.admin.client.view.event.ViewEvent;
import org.openxdata.server.admin.client.view.images.OpenXDataImages;
import org.openxdata.server.admin.client.view.listeners.OpenXDataViewApplicationEventListener;
import org.openxdata.server.admin.client.view.treeview.DatasetTreeView;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.Role;
import org.openxdata.server.admin.model.SettingGroup;
import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.admin.model.User;

/**
 *
 * @author kay
 */
public class MainPresenter implements IPresenter<MainPresenter.Display> {

	private OpenXDataAppMessages appMessages = GWT.create(OpenXDataAppMessages.class);
	
    public interface Display extends WidgetDisplay {

        public void addStack(WidgetDisplay display, String stackText);

        public void setCurrentDisplay(WidgetDisplay display);

        public void addMobileInstallHandler(MobileInstallEvent.Handler handler);

        public void addViewDataHandler(ViewEvent.Handler<FormDefVersion> handler);

        public void addLogoutHandler(LogOutEvent.Handler handler);

        public void setStackPanelListener(StackPanelListener stackListener);

        public void setApplicationEventListener(OpenXDataViewApplicationEventListener listener);

        public void resize(int width, int height);
    }
    private OpenXDataImages images;
    private OpenXDataViewApplicationEventListener currentListener;
    private UsersListPresenter usersListPresenter;
    private RolezListPresenter rolezPresenter;
    private TasksListPresenter tasksListPresenter;
    private SettingListPresenter settingListPresenter;
    private DatasetTreeView reportTreeView;
    private final DatasetView reportView;
    private final StudyView studyView;
    private EventBus eventBus;
    private Display display;

    @Inject
    public MainPresenter(EventBus eventBus, Display display,
            UsersListPresenter usersListPresenter,
            UserPresenter userPresenter,
            RolezListPresenter rolezPresenter,
            RolePresenter rolePresenter,
            TasksListPresenter tasksListPresenter,
            TaskPresenter taskPresenter,
            SettingListPresenter settingListPresenter,
            SettingPresenter settingPresenter,
            DatasetTreeView reportTreeView,
            MainViewController controller,
            DatasetView reportView,
            StudyView studyView) {
        this.eventBus = eventBus;
        this.usersListPresenter = usersListPresenter;
        this.rolezPresenter = rolezPresenter;
        this.tasksListPresenter = tasksListPresenter;
        this.settingListPresenter = settingListPresenter;
        this.reportTreeView = reportTreeView;
        this.display = display;
        images = WidgetDisplay.images;
        this.reportView = reportView;
        this.studyView = studyView;
        bindHandlers();
        bindUI();

    }

    private void bindUI() {

        display.addStack(usersListPresenter.getDisplay(),
                Utilities.createHeaderHTML(images.users(), CONSTANTS.label_users()));
        display.addStack(rolezPresenter.getDisplay(),
                Utilities.createHeaderHTML(images.roles(), CONSTANTS.label_roles()));
        display.addStack(tasksListPresenter.getDisplay(),
                Utilities.createHeaderHTML(images.tasks(), CONSTANTS.label_tasks()));
        display.addStack(settingListPresenter.getDisplay(),
                Utilities.createHeaderHTML(images.settings(), CONSTANTS.label_settings()));
        display.addStack(datasetTreeView.getDisplay(),
                Utilities.createHeaderHTML(images.reports(), appMessages.datasets()));

        display.addMobileInstallHandler(new MobileInstallEvent.Handler() {

            @Override
            public void onInstall() {
                showMobileInstaller();
            }
        });

        display.addViewDataHandler(new ViewEvent.Handler<FormDefVersion>() {

            @Override
            public void onView() {
                showDataList();
            }
        });

        display.addLogoutHandler(new LogOutEvent.Handler() {

            @Override
            public void onLogout() {
                logOut();
            }
        });
        ClosingHandler closingHandler = new ClosingHandler() {

            @Override
            public void onWindowClosing(ClosingEvent event) {
                logOut();
            }
        };
        Window.addWindowClosingHandler(closingHandler);

        display.setStackPanelListener(new StackPanelListener() {

            @Override
            public void onSelectedIndexChanged(int newIndex) {
                switchToView(newIndex);
            }
        });

        display.setApplicationEventListener(getAppListener());

        eventBus.fireEvent(new ViewEvent<User>(User.class));

    }

    private void bindHandlers() {
        PresenterChangeEvent.Handler presenterChangeHandler = new PresenterChangeEvent.Handler() {

            @Override
            public void onChange(IPresenter<?> presenter) {
                display.setCurrentDisplay(presenter.getDisplay());
            }
        };
        eventBus.addHandler(PresenterChangeEvent.TYPE, presenterChangeHandler);

        ViewAppListenerChangeEvent.Handler viewAppListenerChangeHandler = new ViewAppListenerChangeEvent.Handler() {

            @Override
            public void onChange(OpenXDataViewApplicationEventListener listener) {
                currentListener = listener;
            }
        };
        eventBus.addHandler(ViewAppListenerChangeEvent.TYPE, viewAppListenerChangeHandler);
    }

    public OpenXDataViewApplicationEventListener getAppListener() {
        return new OpenXDataViewApplicationEventListener() {

            @Override
            public void onSave() {
                if (currentListener != null)
                    currentListener.onSave();
            }

            @Override
            public void onNewItem() {
                if (currentListener != null)
                    currentListener.onNewItem();
            }

            @Override
            public void onDeleteItem() {
                if (currentListener != null)
                    currentListener.onDeleteItem();
            }

            @Override
            public void onNewChildItem() {
                if (currentListener != null)
                    currentListener.onNewChildItem();
            }

            @Override
            public void onRefresh() {
                if (currentListener != null)
                    currentListener.onRefresh();
            }
        };
    }

    private void logOut() {
        eventBus.fireEvent(new LogOutEvent());
    }

    private void showDataList() {
        //TODO  widget code does not belong here
        final PopupPanel popup = new PopupPanel(false);
        VerticalPanel vp = new VerticalPanel();
        vp.add(studyView);
        VerticalPanel bottomPanel = new VerticalPanel();
        Button btn = new Button("Close");
        btn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                popup.hide();
            }
        });
        bottomPanel.setWidth("100%");
        bottomPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        bottomPanel.add(new HTML("<br>"));
        bottomPanel.add(btn);
        vp.add(bottomPanel);
        popup.add(vp);
        popup.center();
    }

    @Override
    public Display getDisplay() {
        return display;
    }

    private void showMobileInstaller() {
        if (permissionResolver.isExtraPermission("Perm_Mobile_Installer")) {
            new MobileInstaller().center();
        }
    }

    private void switchToView(int newIndex) {
        switch (newIndex) {
            case OpenXDataStackPanelConstants.INDEX_STUDIES:
                //load studies and maps
                break;
            case OpenXDataStackPanelConstants.INDEX_USERS:
                eventBus.fireEvent(new ViewEvent<User>(User.class));
                break;
            case OpenXDataStackPanelConstants.INDEX_ROLES:
                eventBus.fireEvent(new ViewEvent<Role>(Role.class));
                break;
            case OpenXDataStackPanelConstants.INDEX_TASKS:
                eventBus.fireEvent(new ViewEvent<TaskDef>(TaskDef.class));
                break;
            case OpenXDataStackPanelConstants.INDEX_SETTINGS:
                eventBus.fireEvent(new ViewEvent<SettingGroup>(SettingGroup.class));
                break;
            case OpenXDataStackPanelConstants.INDEX_REPORTS:
                if (RolesListUtil.getPermissionResolver().isViewPermission("Perm_View_Reports")) {
                    MainViewControllerFacade.loadAllUserMappedReports(false);
                    MainViewControllerFacade.loadReports(false);
                }
                display.setCurrentDisplay(reportView.getDisplay());
                break;
        }
    }
}
