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
package org.openxdata.server.admin.client.view.factory;

import org.openxdata.server.admin.client.controller.MainViewController;
import org.openxdata.server.admin.client.presenter.MainPresenter;
import org.openxdata.server.admin.client.presenter.ParameterPresenter;
import org.openxdata.server.admin.client.presenter.RolePermissionMapPresenter;
import org.openxdata.server.admin.client.presenter.RolePresenter;
import org.openxdata.server.admin.client.presenter.SettingPresenter;
import org.openxdata.server.admin.client.presenter.TaskPresenter;
import org.openxdata.server.admin.client.presenter.UserFormMapPresenter;
import org.openxdata.server.admin.client.presenter.UserPresenter;
import org.openxdata.server.admin.client.presenter.UserReportGroupMapPresenter;
import org.openxdata.server.admin.client.presenter.UserReportMapPresenter;
import org.openxdata.server.admin.client.presenter.UserRoleMapPresenter;
import org.openxdata.server.admin.client.presenter.UserStudyMapPresenter;
import org.openxdata.server.admin.client.presenter.tree.RolezListPresenter;
import org.openxdata.server.admin.client.presenter.tree.SettingListPresenter;
import org.openxdata.server.admin.client.presenter.tree.TasksListPresenter;
import org.openxdata.server.admin.client.presenter.tree.UsersListPresenter;
import org.openxdata.server.admin.client.view.MainDisplay;
import org.openxdata.server.admin.client.view.MainView;
import org.openxdata.server.admin.client.view.ParameterDisplay;
import org.openxdata.server.admin.client.view.DatasetView;
import org.openxdata.server.admin.client.view.RoleDisplay;
import org.openxdata.server.admin.client.view.SettingDisplay;
import org.openxdata.server.admin.client.view.StudyView;
import org.openxdata.server.admin.client.view.TaskDisplay;
import org.openxdata.server.admin.client.view.UserDisplay;
import org.openxdata.server.admin.client.view.mapping.RolePermissionMapDisplay;
import org.openxdata.server.admin.client.view.mapping.UserFormMapDisplay;
import org.openxdata.server.admin.client.view.mapping.UserReportGroupMapDisplay;
import org.openxdata.server.admin.client.view.mapping.UserReportMapDisplay;
import org.openxdata.server.admin.client.view.mapping.UserRoleMapDisplay;
import org.openxdata.server.admin.client.view.mapping.UserStudyMapDisplay;
import org.openxdata.server.admin.client.view.treeview.DatasetTreeView;
import org.openxdata.server.admin.client.view.treeview.RoleTreeDisplay;
import org.openxdata.server.admin.client.view.treeview.SettingsTreeDisplay;
import org.openxdata.server.admin.client.view.treeview.StudiesTreeView;
import org.openxdata.server.admin.client.view.treeview.TasksTreeDisplay;
import org.openxdata.server.admin.client.view.treeview.UsersTreeDisplay;
import org.openxdata.server.admin.client.view.widget.OpenXDataMenuBar;
import org.openxdata.server.admin.client.view.widget.OpenXDataNotificationBar;
import org.openxdata.server.admin.client.view.widget.OpenXDataStackPanel;
import org.openxdata.server.admin.client.view.widget.OpenXDataToolBar;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/**
 * Binds the classes and providers using a Guice module.
 * 
 * @author Angel
 * 
 */
public class OpenXdataClientModule extends AbstractGinModule {

    /*
     * (non-Javadoc)
     *
     * @see com.google.gwt.inject.client.AbstractGinModule#configure()
     */
    @Override
    protected void configure() {

        // Bindings
        bind(OpenXDataWidgetFactory.class).to(OpenXDataViewFactory.class).in(
                Singleton.class);
        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);

        // TreeViews
        bind(StudiesTreeView.class).in(Singleton.class);
        bind(DatasetTreeView.class).in(Singleton.class);

        // Views
        bind(DatasetView.class).in(Singleton.class);
        bind(StudyView.class).in(Singleton.class);
        bind(OpenXDataNotificationBar.class).in(Singleton.class);

        // Map views
        bind(MainView.class).in(Singleton.class);

        // Other ImportanWidgets
        bind(OpenXDataStackPanel.class).in(Singleton.class);
        bind(OpenXDataMenuBar.class).toProvider(MenuBarProvider.class).in(Singleton.class);
        bind(OpenXDataToolBar.class).toProvider(ToolBarProvider.class).in(Singleton.class);

        bind(MainViewController.class).in(Singleton.class);

        bind(MainPresenter.class).in(Singleton.class);
        bind(MainPresenter.Display.class).to(MainDisplay.class).in(Singleton.class);

        bind(UsersListPresenter.class).in(Singleton.class);
        bind(UsersListPresenter.Display.class).to(UsersTreeDisplay.class).in(Singleton.class);

        bind(RolezListPresenter.class).in(Singleton.class);
        bind(RolezListPresenter.Display.class).to(RoleTreeDisplay.class).in(Singleton.class);

        bind(UserPresenter.class).in(Singleton.class);
        bind(UserPresenter.Display.class).to(UserDisplay.class).in(Singleton.class);

        bind(UserRoleMapPresenter.class).in(Singleton.class);
        bind(UserRoleMapPresenter.Display.class).to(UserRoleMapDisplay.class).in(Singleton.class);

        bind(UserStudyMapPresenter.class).in(Singleton.class);
        bind(UserStudyMapPresenter.Display.class).to(UserStudyMapDisplay.class).in(Singleton.class);

        bind(UserFormMapPresenter.class).in(Singleton.class);
        bind(UserFormMapPresenter.Display.class).to(UserFormMapDisplay.class).in(Singleton.class);

        bind(UserReportGroupMapPresenter.class).in(Singleton.class);
        bind(UserReportGroupMapPresenter.Display.class).to(UserReportGroupMapDisplay.class).in(Singleton.class);

        bind(UserReportMapPresenter.class).in(Singleton.class);
        bind(UserReportMapPresenter.Display.class).to(UserReportMapDisplay.class).in(Singleton.class);

        bind(RolePresenter.class).in(Singleton.class);
        bind(RolePresenter.Display.class).to(RoleDisplay.class).in(Singleton.class);

        bind(RolePermissionMapPresenter.class).in(Singleton.class);
        bind(RolePermissionMapPresenter.Display.class).to(RolePermissionMapDisplay.class).in(Singleton.class);

        bind(TasksListPresenter.class).in(Singleton.class);
        bind(TasksListPresenter.Display.class).to(TasksTreeDisplay.class).in(Singleton.class);

        bind(TaskPresenter.class).in(Singleton.class);
        bind(TaskPresenter.Display.class).to(TaskDisplay.class).in(Singleton.class);

        bind(ParameterPresenter.class).in(Singleton.class);
        bind(ParameterPresenter.Display.class).to(ParameterDisplay.class).in(Singleton.class);

        bind(SettingListPresenter.class).in(Singleton.class);
        bind(SettingListPresenter.Display.class).to(SettingsTreeDisplay.class).in(Singleton.class);

        bind(SettingPresenter.class).in(Singleton.class);
        bind(SettingPresenter.Display.class).to(SettingDisplay.class).in(Singleton.class);
    }
}
