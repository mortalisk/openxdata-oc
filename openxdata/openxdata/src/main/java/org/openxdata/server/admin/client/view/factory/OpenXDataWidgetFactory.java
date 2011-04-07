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
import org.openxdata.server.admin.client.view.widget.OpenXDataMenuBar;
import org.openxdata.server.admin.client.view.widget.OpenXDataNotificationBar;
import org.openxdata.server.admin.client.view.widget.OpenXDataToolBar;
import org.openxdata.server.admin.client.view.treeview.OpenXDataBaseTreeView;
import org.openxdata.server.admin.client.view.treeview.ReportsTreeView;
import org.openxdata.server.admin.client.view.treeview.StudiesTreeView;
import org.openxdata.server.admin.client.view.treeview.listeners.ContextMenuInitListener;
import org.openxdata.server.admin.client.view.widget.OpenXDataLabel;
import org.openxdata.server.admin.client.view.widget.OpenXDataStackPanel;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Abstracts away the concrete implementation of the 
 * <tt>{@link OpenXDataViewFactory} by providing a common access <tt>interface.</tt>
 * 
 * @author Angel
 *
 */
public interface OpenXDataWidgetFactory {
	
	/**
	 * Retrieves the <tt>StudiesTreeView</tt>
	 * 
	 * @return Instance of {@link StudiesTreeView}
	 */
	StudiesTreeView getStudiesTreeView();
	
	/**
	 * Retrieves the <tt>ReportsTreeView</tt>
	 * 
	 * @return Instance of {@link ReportsTreeView}
	 */
	ReportsTreeView getReportsTreeView();
	
	/**
	 * Retrieves the <tt>StudyView</tt>
	 * 
	 * @return Instance of {@link StudyView}
	 */
	StudyView getStudyView();
	
	/**
	 * Retrieves the <tt>ReportView</tt>
	 * 
	 * @return Instance of {@link ReportView}
	 */
	ReportView getReportView();
	
	/**
	 * Retrieves the <tt>MainView</tt>
	 * 
	 * @return Instance of {@link MainView}
	 */
	MainView getMainView();
	
	
	/**
	 * Retrieves the <tt>OpenXdata Stack Panel object</tt> that has been configured for this session.
	 * 
	 * @return Instance of {@link OpenXDataStackPanel}
	 */
	OpenXDataStackPanel getOpenXdataStackPanel();
	
	/**
	 * Retrieves the <tt>Horizontal Split Panel object</tt> that has been configured for this session.
	 * 
	 * @return Instance of {@link HorizontalSplitPanel}
	 */
	HorizontalSplitPanel getHorizontalSplitPanel();
	
	/**
	 * Retrieves the <tt>Vertical Panel object</tt> that has been configured for this session.
	 * 
	 * @return Instance of {@link VerticalPanel}
	 */
	VerticalPanel getVerticalPanel();
	
	/**
	 * Retrieves the <tt>Menu Bar</tt> that has been configured for this session.
	 * 
	 * @return Instance of {@link OpenXDataMenuBar}
	 */
	OpenXDataMenuBar getOpenXDataMenuBar();
	
	/**
	 * Retrieves the <tt>Tool Bar</tt> that has been configured for this session.
	 * 
	 * @return Instance of {@link OpenXDataMenuBar}
	 */
	OpenXDataToolBar getOpenXDataToolBar();

	/**
	 * Retrieves the <tt>Notification Label</tt> that has been configured for this session.
	 * 
	 * @return Instance of {@link Label}
	 */
	OpenXDataLabel getNotificationLabel();
	
	
	/**
	 * Retrieves the <tt>Notification Bar</tt> configured for this session.
	 * @return instance of {@link OpenXDataNotificationBar}
	 */
	OpenXDataNotificationBar getNotificationBar();
	
	/**
	 * Retrieves the <tt>Context Menu</tt> configured for a particular {@link OpenXDataBaseTreeView}.
	 * 
	 * @param contextMenuListener <tt>Context Menu Listener</tt> that will handle events on the <tt>Context Menu.</tt>
	 * @param labels Labels to bind to the <tt>Context Menu.</tt>
	 * @param treeViewName Name of the <tt>Tree View</tt> where we shall bind the <tt>Context Menu.</tt>
	 * 
	 * @return instance of {@link PopupPanel}
	 */
	PopupPanel getContextMenu(ContextMenuInitListener contextMenuListener, UIViewLabels labels, String treeViewName);

        EventBus getEventBus();

        public  void setInjector(OpenXDataWidgetGinInjector injector);
}
