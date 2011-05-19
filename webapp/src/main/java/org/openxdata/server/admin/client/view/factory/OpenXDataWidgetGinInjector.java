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

import com.google.gwt.event.shared.EventBus;
import org.openxdata.server.admin.client.view.MainView;
import org.openxdata.server.admin.client.view.ReportView;
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

    public ReportView getReportView();

    public StudyView getStudyView();

    public OpenXDataWidgetFactory getWidgetFactory();

    public OpenXDataStackPanel getOpenXdataStackPanel();

    public OpenXDataMenuBar getOpenXDataMenuBar();

    public OpenXDataLabel getNotificationLabel();

    public OpenXDataToolBar getOpenXDataToolBar();

    public MainView getMainView();

    public MainPresenter getMainPresenter();
}
