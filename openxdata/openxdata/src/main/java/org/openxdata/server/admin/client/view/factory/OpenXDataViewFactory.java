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

import org.openxdata.server.admin.client.permissions.UIViewLabels;
import org.openxdata.server.admin.client.view.MainView;
import org.openxdata.server.admin.client.view.ReportView;
import org.openxdata.server.admin.client.view.StudyView;
import org.openxdata.server.admin.client.view.bar.OpenXDataMenuBar;
import org.openxdata.server.admin.client.view.bar.OpenXDataNotificationBar;
import org.openxdata.server.admin.client.view.bar.OpenXDataToolBar;
import org.openxdata.server.admin.client.view.contextmenu.OpenXDataContextMenu;
import org.openxdata.server.admin.client.view.treeview.ReportsTreeView;
import org.openxdata.server.admin.client.view.treeview.StudiesTreeView;
import org.openxdata.server.admin.client.view.treeview.listeners.ContextMenuInitListener;
import org.openxdata.server.admin.client.view.widget.OpenXDataLabel;
import org.openxdata.server.admin.client.view.widget.OpenXDataStackPanel;
import org.openxdata.server.admin.client.view.widget.factory.OpenXDataWidgetFactory;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Default implementation of the {@link OpenXDataWidgetFactory}.
 * 
 * <p>
 * The <code>Composites</code> returned from this factory are too abstract to be
 * implemented AS IS. In some cases, you might be required to cast to known
 * <tt>types</tt> before implementing custom behavior.
 * </p>
 * 
 * @version 1.0
 * 
 * @author Angel
 * 
 */
public class OpenXDataViewFactory implements OpenXDataWidgetFactory {

    /** Label for showing notifications to the <tt>User.</tt> */
    private OpenXDataLabel notificationLabel;
    /** Vertical Panel to align <tt>Widgets.</tt> */
    private VerticalPanel verticalPanel;
    /** HorizontalSplitPanel to align main widgets. */
    private HorizontalSplitPanel horizontalSplitClient;
    /** Widget for Mapping Permissions to Roles */
    private static OpenXDataWidgetGinInjector injector;

    /** Constructs an instance of this <tt>class.</tt> */
    public OpenXDataViewFactory() {
    }

    @Override
    public StudiesTreeView getStudiesTreeView() {
        return injector.getStudiesTreeView();
    }

    @Override
    public ReportsTreeView getReportsTreeView() {
        return injector.getReportsTreeView();
    }

    @Override
    public ReportView getReportView() {
        return injector.getReportView();

    }

    @Override
    public StudyView getStudyView() {
        return injector.getStudyView();
    }

    @Override
    public MainView getMainView() {
        return injector.getMainView();
    }

    @Override
    public OpenXDataMenuBar getOpenXDataMenuBar() {
        return injector.getOpenXDataMenuBar();
    }

    @Override
    public OpenXDataToolBar getOpenXDataToolBar() {
        return injector.getOpenXDataToolBar();
    }

    @Override
    public VerticalPanel getVerticalPanel() {
        if (verticalPanel == null) {
            verticalPanel = new VerticalPanel();

            verticalPanel.setWidth("100%");

            // Menu Bar
            verticalPanel.add(getOpenXDataMenuBar());

            // Notification Bar
            verticalPanel.add(getNotificationBar());

            // Tool Bar
            verticalPanel.add(getOpenXDataToolBar());

        }

        return verticalPanel;
    }

    @Override
    public OpenXDataNotificationBar getNotificationBar() {
        return injector.getNotificationBar();
    }

    @Override
    public OpenXDataLabel getNotificationLabel() {
        if (notificationLabel == null)
            notificationLabel = new OpenXDataLabel(" ");

        return notificationLabel;
    }

    @Override
    public HorizontalSplitPanel getHorizontalSplitPanel() {
        if (horizontalSplitClient == null) {
            horizontalSplitClient = new HorizontalSplitPanel();

            horizontalSplitClient.setSplitPosition("20%");
//			horizontalSplitClient.setRightWidget(getStudyView());
        }

        return horizontalSplitClient;
    }

    @Override
    public OpenXDataStackPanel getOpenXdataStackPanel() {
        return injector.getOpenXdataStackPanel();

    }

    @Override
    public PopupPanel getContextMenu(
            ContextMenuInitListener contextMenuListener,
            UIViewLabels contextMenuLabels, String treeViewName) {
        return new OpenXDataContextMenu().createContextMenu(
                contextMenuListener, contextMenuLabels, treeViewName);
    }

    @Override
    public EventBus getEventBus() {
        return injector.getEventBus();
    }

    @Override
    public void setInjector(OpenXDataWidgetGinInjector injector) {
        OpenXDataViewFactory.injector = injector;
    }
}
