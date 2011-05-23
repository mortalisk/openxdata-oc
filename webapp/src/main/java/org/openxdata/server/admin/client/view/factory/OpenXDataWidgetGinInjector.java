package org.openxdata.server.admin.client.view.factory;

import com.google.gwt.event.shared.EventBus;
import org.openxdata.server.admin.client.view.MainView;
import org.openxdata.server.admin.client.view.DatasetView;
import org.openxdata.server.admin.client.view.StudyView;
import org.openxdata.server.admin.client.view.widget.OpenXDataMenuBar;
import org.openxdata.server.admin.client.view.widget.OpenXDataNotificationBar;
import org.openxdata.server.admin.client.view.widget.OpenXDataToolBar;
import org.openxdata.server.admin.client.view.treeview.DatasetTreeView;
import org.openxdata.server.admin.client.view.treeview.StudiesTreeView;
import org.openxdata.server.admin.client.view.widget.OpenXDataLabel;
import org.openxdata.server.admin.client.view.widget.OpenXDataStackPanel;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import org.openxdata.server.admin.client.presenter.MainPresenter;

/**
 * Declares Methods that will return the required types to the client.
 * 
 * @author Angel
 * 
 */
@GinModules(OpenXdataClientModule.class)
public interface OpenXDataWidgetGinInjector extends Ginjector {


    public OpenXDataWidgetFactory widgetFactory();

    public OpenXDataNotificationBar getNotificationBar();

    public EventBus getEventBus();

    public StudiesTreeView getStudiesTreeView();

    public DatasetTreeView getReportsTreeView();

    public DatasetView getReportView();

    public StudyView getStudyView();

    public OpenXDataWidgetFactory getWidgetFactory();

    public OpenXDataStackPanel getOpenXdataStackPanel();

    public OpenXDataMenuBar getOpenXDataMenuBar();

    public OpenXDataLabel getNotificationLabel();

    public OpenXDataToolBar getOpenXDataToolBar();

    public MainView getMainView();

    public MainPresenter getMainPresenter();
}
