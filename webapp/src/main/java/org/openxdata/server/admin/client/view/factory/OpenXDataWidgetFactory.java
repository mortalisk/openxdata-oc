package org.openxdata.server.admin.client.view.factory;

import org.openxdata.server.admin.client.permissions.UIViewLabels;
import org.openxdata.server.admin.client.view.DatasetView;
import org.openxdata.server.admin.client.view.treeview.DatasetTreeView;
import org.openxdata.server.admin.client.view.treeview.OpenXDataBaseTreeView;
import org.openxdata.server.admin.client.view.treeview.listeners.ContextMenuInitListener;
import org.openxdata.server.admin.client.view.widget.OpenXDataMenuBar;
import org.openxdata.server.admin.client.view.widget.OpenXDataNotificationBar;
import org.openxdata.server.admin.client.view.widget.OpenXDataStackPanel;
import org.openxdata.server.admin.client.view.widget.OpenXDataToolBar;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Abstracts away the concrete implementation of the 
 * <tt>{@link OpenXDataViewFactory} by providing a common access <tt>interface.</tt>
 * 
 *
 */
@SuppressWarnings("deprecation")
public interface OpenXDataWidgetFactory {
	
	
	/**
	 * Retrieves the <tt>ReportsTreeView</tt>
	 * 
	 * @return Instance of {@link DatasetTreeView}
	 */
	DatasetTreeView getReportsTreeView();
	
	/**
	 * Retrieves the <tt>ReportView</tt>
	 * 
	 * @return Instance of {@link DatasetView}
	 */
	DatasetView getReportView();
	
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
	OpenXDataNotificationBar getNotificationLabel();
	
	
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
