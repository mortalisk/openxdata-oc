package org.openxdata.server.admin.client.view.factory;

import org.openxdata.server.admin.client.presenter.MainPresenter;
import org.openxdata.server.admin.client.view.DatasetView;
import org.openxdata.server.admin.client.view.treeview.DatasetTreeView;
import org.openxdata.server.admin.client.view.widget.OpenXDataLabel;
import org.openxdata.server.admin.client.view.widget.OpenXDataMenuBar;
import org.openxdata.server.admin.client.view.widget.OpenXDataNotificationBar;
import org.openxdata.server.admin.client.view.widget.OpenXDataStackPanel;
import org.openxdata.server.admin.client.view.widget.OpenXDataToolBar;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

/**
 * Declares Methods that will return the required types to the client.
 * 
 * 
 */
@GinModules(OpenXdataClientModule.class)
public interface OpenXDataWidgetGinInjector extends Ginjector {


    public OpenXDataWidgetFactory widgetFactory();

    public OpenXDataNotificationBar getNotificationBar();

    public EventBus getEventBus();

    public DatasetTreeView getReportsTreeView();

    public DatasetView getReportView();

    public OpenXDataWidgetFactory getWidgetFactory();

    public OpenXDataStackPanel getOpenXdataStackPanel();

    public OpenXDataMenuBar getOpenXDataMenuBar();

    public OpenXDataLabel getNotificationLabel();

    public OpenXDataToolBar getOpenXDataToolBar();

    public MainPresenter getMainPresenter();
}
