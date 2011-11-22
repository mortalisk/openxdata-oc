package org.openxdata.server.admin.client.view.factory;

import org.openxdata.server.admin.client.controller.MainViewController;
import org.openxdata.server.admin.client.presenter.MainPresenter;
import org.openxdata.server.admin.client.presenter.ParameterPresenter;
import org.openxdata.server.admin.client.presenter.RolePermissionMapPresenter;
import org.openxdata.server.admin.client.presenter.RolePresenter;
import org.openxdata.server.admin.client.presenter.SettingPresenter;
import org.openxdata.server.admin.client.presenter.TaskPresenter;
import org.openxdata.server.admin.client.presenter.tree.RolezListPresenter;
import org.openxdata.server.admin.client.presenter.tree.SettingListPresenter;
import org.openxdata.server.admin.client.presenter.tree.TasksListPresenter;
import org.openxdata.server.admin.client.view.DatasetView;
import org.openxdata.server.admin.client.view.MainDisplay;
import org.openxdata.server.admin.client.view.ParameterDisplay;
import org.openxdata.server.admin.client.view.RoleDisplay;
import org.openxdata.server.admin.client.view.SettingDisplay;
import org.openxdata.server.admin.client.view.TaskDisplay;
import org.openxdata.server.admin.client.view.mapping.RolePermissionMapDisplay;
import org.openxdata.server.admin.client.view.treeview.DatasetTreeView;
import org.openxdata.server.admin.client.view.treeview.RoleTreeDisplay;
import org.openxdata.server.admin.client.view.treeview.SettingsTreeDisplay;
import org.openxdata.server.admin.client.view.treeview.TasksTreeDisplay;
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
        bind(DatasetTreeView.class).in(Singleton.class);

        // Views
        bind(DatasetView.class).in(Singleton.class);
        bind(OpenXDataNotificationBar.class).in(Singleton.class);

        // Other ImportanWidgets
        bind(OpenXDataStackPanel.class).in(Singleton.class);
        bind(OpenXDataToolBar.class).toProvider(ToolBarProvider.class).in(Singleton.class);

        bind(MainViewController.class).in(Singleton.class);

        bind(MainPresenter.class).in(Singleton.class);
        bind(MainPresenter.Display.class).to(MainDisplay.class).in(Singleton.class);

        bind(RolezListPresenter.class).in(Singleton.class);
        bind(RolezListPresenter.Display.class).to(RoleTreeDisplay.class).in(Singleton.class);

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
