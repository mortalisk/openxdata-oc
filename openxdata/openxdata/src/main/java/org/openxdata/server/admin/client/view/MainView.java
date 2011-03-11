/*
 *  Licensed to the OpenXdata Foundation (OXDF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The OXDF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, 
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 *  Copyright 2010 http://www.openxdata.org.
 */
package org.openxdata.server.admin.client.view;

import org.openxdata.server.admin.client.controller.MainViewController;
import org.openxdata.server.admin.client.locale.OpenXdataText;
import org.openxdata.server.admin.client.locale.TextConstants;
import org.openxdata.server.admin.client.permissions.util.RolesListUtil;
import org.openxdata.server.admin.client.presenter.IPresenter;
import org.openxdata.server.admin.client.tools.MobileInstaller;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.bar.OpenXDataMenuBar;
import org.openxdata.server.admin.client.view.bar.OpenXDataToolBar;
import org.openxdata.server.admin.client.view.event.LogOutEvent;
import org.openxdata.server.admin.client.view.event.MobileInstallEvent;
import org.openxdata.server.admin.client.view.images.OpenXDataImages;
import org.openxdata.server.admin.client.view.widget.OpenXDataStackPanel;
import org.openxdata.server.admin.client.view.widget.factory.OpenXDataWidgetFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.inject.Inject;
import org.openxdata.server.admin.client.presenter.RolePresenter;
import org.openxdata.server.admin.client.presenter.TaskPresenter;
import org.openxdata.server.admin.client.presenter.UserPresenter;
import org.openxdata.server.admin.client.presenter.tree.RolezListPresenter;
import org.openxdata.server.admin.client.presenter.tree.TasksListPresenter;
import org.openxdata.server.admin.client.presenter.tree.UsersListPresenter;
import org.openxdata.server.admin.client.view.event.PresenterChangeEvent;
import org.openxdata.server.admin.client.view.event.ViewAppListenerChangeEvent;
import org.openxdata.server.admin.client.view.listeners.OpenXDataViewApplicationEventListener;

/**
 * Application Main View.
 * <p>
 * This view deals with setting up of the various contained views, switching
 * between them, and calling methods onto them.
 * </p>
 * 
 * @author daniel
 * @author Angel
 * 
 */
public class MainView extends Composite implements ResizeHandler {

    /** OpenXdata Stack Panel to hold Tree Views. */
    private OpenXDataStackPanel openXdataStackPanel;
    private EventBus eventBus;
    private OpenXDataWidgetFactory widgetFactory;
    private OpenXDataMenuBar menuBar;
    private OpenXDataToolBar toolBar;
    private final UsersListPresenter usersListPresenter;
    private HorizontalSplitPanel splitPanel;
    private OpenXDataViewApplicationEventListener currentListener;
    private final RolezListPresenter rolezListPresenter;
    private final TasksListPresenter tasksListPresenter;

    /**
     * Constructs an instance of this class with a <code>Logout Listener</code>
     *
     * @param logoutListener
     *            <code>Logout Listener for this class</code>.
     */
    @Inject
    public MainView(EventBus eventBus, MainViewController controller,
            OpenXDataWidgetFactory widgetFactory,
            UsersListPresenter usersListPresenter,
            UserPresenter userPresenter,
            RolezListPresenter rolezListPresenter,
            RolePresenter rolePresenter,
            TasksListPresenter tasksListPresenter,
            TaskPresenter taskPresenter) {
        this.eventBus = eventBus;
        this.widgetFactory = widgetFactory;
        this.toolBar = widgetFactory.getOpenXDataToolBar();
        this.menuBar = widgetFactory.getOpenXDataMenuBar();
        this.usersListPresenter = usersListPresenter;
        this.splitPanel = widgetFactory.getHorizontalSplitPanel();
        this.rolezListPresenter = rolezListPresenter;
        this.tasksListPresenter = tasksListPresenter;
        bindHandlers();
        setUp();
   
       

    }

    /**
     * Initializes the widgets to be bound to the main view with Permission
     * Resolver Object to handle permissions.
     */
    private void setUp() {

        MobileInstallEvent.Handler mobInstallHandler = new MobileInstallEvent.Handler() {

            @Override
            public void onInstall() {
                mobileInstaller();
            }
        };
        menuBar.addHandler(mobInstallHandler, MobileInstallEvent.TYPE);

        LogOutEvent.Handler logOutHandler = new LogOutEvent.Handler() {

            @Override
            public void onLogout() {
                logOut();
            }
        };
        menuBar.addHandler(logOutHandler, LogOutEvent.TYPE);
        toolBar.addHandler(logOutHandler, LogOutEvent.TYPE);

        ClosingHandler closingHandler = new ClosingHandler() {

            @Override
            public void onWindowClosing(ClosingEvent event) {
                logOut();
            }
        };
        Window.addWindowClosingHandler(closingHandler);

        OpenXDataViewApplicationEventListener appListener = getAppListener();
        menuBar.registerApplicationEventListener(appListener);
        toolBar.registerApplicationEventListener(appListener);


        bindViewsToPanel();
    }

    /**
     * Binds tree views to the panel
     */
    private void bindViewsToPanel() {
        OpenXDataImages images = GWT.create(OpenXDataImages.class);

        // Stack Panel to organize MainView
        openXdataStackPanel = widgetFactory.getOpenXdataStackPanel();

        openXdataStackPanel.add(
                widgetFactory.getStudiesTreeView(),
                Utilities.createHeaderHTML(images.studies(),
                OpenXdataText.get(TextConstants.STUDIES)), true);

        openXdataStackPanel.add(
                //widgetFactory.getUsersTreeView(),
                usersListPresenter.getDisplay().asWidget(),
                Utilities.createHeaderHTML(images.users(),
                OpenXdataText.get(TextConstants.USERS)), true);

        openXdataStackPanel.add(
                //widgetFactory.getRolesTreeView(),
                rolezListPresenter.getDisplay().asWidget(),
                Utilities.createHeaderHTML(images.roles(),
                OpenXdataText.get(TextConstants.ROLES)), true);

        openXdataStackPanel.add(
               // widgetFactory.getTasksTreeView(),
                tasksListPresenter.getDisplay().asWidget(),
                Utilities.createHeaderHTML(images.tasks(),
                OpenXdataText.get(TextConstants.TASKS)), true);

        openXdataStackPanel.add(
                widgetFactory.getSettingsTreeView(),
                Utilities.createHeaderHTML(images.settings(),
                OpenXdataText.get(TextConstants.SETTINGS)), true);

        openXdataStackPanel.add(
                widgetFactory.getReportsTreeView(),
                Utilities.createHeaderHTML(images.reports(),
                OpenXdataText.get(TextConstants.REPORTS)), true);

        openXdataStackPanel.setWidth("100%");

        widgetFactory.getHorizontalSplitPanel().setLeftWidget(
                openXdataStackPanel);

        widgetFactory.getVerticalPanel().add(
                widgetFactory.getHorizontalSplitPanel());

        DockLayoutPanel dockPanel = new DockLayoutPanel(Unit.EM);
        dockPanel.add(widgetFactory.getVerticalPanel());

        Utilities.maximizeWidget(dockPanel);
        initWidget(dockPanel);
    }

    public void logOut() {
        eventBus.fireEvent(new LogOutEvent());
    }

    /**
     * Resets the size of the <tt>MainView.</tt>
     */
    public void resize(int width, int height) {
        widgetFactory.getHorizontalSplitPanel().setSize(width + "px",
                (height - 50) + "px");

        int shortcutHeight = height - openXdataStackPanel.getAbsoluteTop();// 8;
        if (shortcutHeight < 1) shortcutHeight = 1;

        openXdataStackPanel.setHeight(shortcutHeight + "px");
    }

    public void mobileInstaller() {
        if (RolesListUtil.getPermissionResolver().isExtraPermission("Perm_Mobile_Installer")) {
            new MobileInstaller().center();
        }
    }

    @Override
    public void onResize(ResizeEvent event) {
        int width = event.getWidth();
        int height = event.getHeight();
        resize(width, height);
    }

    private void bindHandlers() {
        PresenterChangeEvent.Handler presenterChangeHandler = new PresenterChangeEvent.Handler() {

            @Override
            public void onChange(IPresenter<?> presenter) {
                splitPanel.setRightWidget(presenter.getDisplay().asWidget());
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
}
