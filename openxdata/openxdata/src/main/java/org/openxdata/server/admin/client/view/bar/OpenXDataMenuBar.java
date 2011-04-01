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
package org.openxdata.server.admin.client.view.bar;

import java.util.List;
import java.util.ArrayList;

import org.openxdata.server.admin.client.internationalization.OpenXdataConstants;
import org.openxdata.server.admin.client.permissions.util.RolesListUtil;
import org.openxdata.server.admin.client.view.event.dispatcher.EventDispatcher;
import org.openxdata.server.admin.client.view.listeners.OpenXDataExportImportApplicationEventListener;
import org.openxdata.server.admin.client.view.listeners.OpenXDataViewApplicationEventListener;
import org.openxdata.server.admin.client.view.listeners.OpenXDataViewExtendedApplicationEventListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import org.openxdata.server.admin.client.view.event.LogOutEvent;
import org.openxdata.server.admin.client.view.event.MobileInstallEvent;
import org.openxdata.server.admin.client.view.event.ViewEvent;
import org.openxdata.server.admin.model.FormDefVersion;

/**
 * Encapsulates <code>Menu Bar</code> controls on the view.
 * 
 * @author Angel
 *
 */
public class OpenXDataMenuBar extends Composite implements EventDispatcher {

    private static OpenXdataConstants constants = GWT.create(
            OpenXdataConstants.class);
    /**
     * The menu bar control to bind widgets to
     */
    private MenuBar menuBar;
    /**
     * Menu bar representing the File menu
     */
    private OpenXDataBaseMenuBar fileMenu;
    /**
     * Menu bar representing the View menu
     */
    private OpenXDataBaseMenuBar viewMenu;
    /**
     * Menu bar representing the Tools menu
     */
    private OpenXDataBaseMenuBar toolsMenu;
    /** List of registered <tt>Event Listeners.</tt>*/
    private List<OpenXDataViewApplicationEventListener> viewApplicationEventListeners;

    /**
     * Constructs an instance of this class.
     */
    public OpenXDataMenuBar() {

        menuBar = new MenuBar();
        viewApplicationEventListeners = new ArrayList<OpenXDataViewApplicationEventListener>();
    }

    /**
     * Finalizes the <tt>Menu Bar</tt> preparation by binding the help menu.
     */
    private void constructHelpMenuBar() {
        initWidget(menuBar);
    }

    /**
     * Constructor a <tt>Menu Bar</tt> instance for <tt>User</tt> with administrative privileges.
     */
    public void constructMenuBarInstanceOfAdministratorUser() {

        constructFileMenuBar(fileMenu);
        constructViewMenuBar(viewMenu);
        constructToolsMenuBar(toolsMenu);
        constructHelpMenuBar();
    }

    /**
     * Constructs a <tt>Menu Bar</tt> according to <tt>User Permissions.</tt>
     */
    public void constructMenuBarInstanceOfUserWithPermissions() {

        if (RolesListUtil.getPermissionResolver().isAddPermission() || RolesListUtil.
                getPermissionResolver().isEditPermission()) {

            constructFileMenuBar(fileMenu);
        }

        if (RolesListUtil.getPermissionResolver().isViewPermission()) {
            constructViewMenuBar(viewMenu);
        }

        if (RolesListUtil.getPermissionResolver().isDeletePermission()) {
            constructToolsMenuBar(toolsMenu);
        }

        constructHelpMenuBar();
    }

    /**
     * Constructs a menu bar with actions to enable deleting of objects.
     *
     * @param toolsMenu <code>Tools menu bar</code> to bind controls to.
     */
    private void constructToolsMenuBar(MenuBar toolsMenu) {
        toolsMenu = new OpenXDataBaseMenuBar(true);

        if (RolesListUtil.getPermissionResolver().isExtraPermission(
                "Perm_Mobile_Installer")) {
            toolsMenu.addItem("Mobile Installer", true, new Command() {

                @Override
                public void execute() {
                   fireEvent(new MobileInstallEvent());
                }
            });
        }

        menuBar.addItem(constants.label_tools(), toolsMenu);
    }

    /**
     * Constructs a menu bar with controls to enable viewing of objects.
     *
     * @param viewMenu <code>View Menu bar</code> to bind controls to.
     */
    private void constructViewMenuBar(MenuBar viewMenu) {

        viewMenu = new OpenXDataBaseMenuBar(true);
        if (RolesListUtil.getPermissionResolver().isAdmin()) {
            viewMenu.addItem(constants.label_refresh(), true, new Command() {

                @Override
                public void execute() {
                    notifyOnRefreshEventListeners();
                }
            });

            viewMenu.addSeparator();
            viewMenu.addItem(constants.label_format(), true, new Command() {

                @Override
                public void execute() {
                    notifyOnFormatEventListeners();
                }
            });
            viewMenu.addSeparator();
            viewMenu.addItem(constants.label_datalist(),true,new Command() {

                @Override
                public void execute() {
                   fireEvent(new ViewEvent<FormDefVersion>(FormDefVersion.class));
                }
            });
            viewMenu.addSeparator();

            menuBar.addItem(constants.label_view(), viewMenu);
        }
    }

    /**
     * Constructs a menu bar that provides controls to enable adding of objects.
     *
     * @param fileMenu <code> The File Menu bar</code> to bind controls to.
     */
    private void constructFileMenuBar(MenuBar fileMenu) {

        fileMenu = new OpenXDataBaseMenuBar(true);
        boolean addPermissionLessSeparator = true;

        if (RolesListUtil.getPermissionResolver().isAddStudies() || RolesListUtil.
                getPermissionResolver().isAddUsers() || RolesListUtil.
                getPermissionResolver().isAddRoles() || RolesListUtil.
                getPermissionResolver().isAddTasks() || RolesListUtil.
                getPermissionResolver().isAddSettings() || RolesListUtil.
                getPermissionResolver().isAddReportGroups()) {

            fileMenu.addItem(constants.label_new(), true, new Command() {

                @Override
                public void execute() {
                    notifyOnNewItemEventListeners();
                }
            });

            fileMenu.addSeparator();

            fileMenu.addItem(constants.label_save(), true, new Command() {

                @Override
                public void execute() {
                    notifyOnSaveEventListeners();
                }
            });

            fileMenu.addSeparator();


            addPermissionLessSeparator = false;
        }

        if (RolesListUtil.getPermissionResolver().isExtraPermission(
                "Perm_Open_Items_Via_File_Menu")) {
            fileMenu.addItem(constants.label_open(), true, new Command() {

                @Override
                public void execute() {
                    notifyOnOpenEventListeners();
                }
            });

            fileMenu.addSeparator();
        }

        if (RolesListUtil.getPermissionResolver().isExtraPermission(
                "Perm_Export_Studies") || RolesListUtil.getPermissionResolver().
                isExtraPermission("Perm_Import_Studies") || RolesListUtil.
                getPermissionResolver().isExtraPermission("Perm_Import_Users")) {

            fileMenu.addItem(constants.label_data_import(), true, new Command() {

                @Override
                public void execute() {
                    notifyOnImportEventListeners();
                }
            });

            fileMenu.addItem(constants.label_data_export(), true, new Command() {

                @Override
                public void execute() {
                    notifyOnExportEventListeners();
                }
            });

            fileMenu.addSeparator();

            addPermissionLessSeparator = false;
        }

        fileMenu.addItem(constants.label_logout(), true, new Command() {

            @Override
            public void execute() {
                fireEvent(new LogOutEvent());
            }
        });

        if (addPermissionLessSeparator)
            fileMenu.addSeparator();

        menuBar.addItem(constants.label_file(), fileMenu);

    }

    @Override
    public void notifyOnExportEventListeners() {
        for (OpenXDataViewApplicationEventListener xViewAppEventListener :
                viewApplicationEventListeners) {
            if (xViewAppEventListener instanceof OpenXDataExportImportApplicationEventListener) {
                ((OpenXDataExportImportApplicationEventListener) xViewAppEventListener).
                        onExport();
            }
        }
    }

    @Override
    public void notifyOnImportEventListeners() {
        for (OpenXDataViewApplicationEventListener xViewAppEventListener :
                viewApplicationEventListeners) {
            if (xViewAppEventListener instanceof OpenXDataExportImportApplicationEventListener) {
                ((OpenXDataExportImportApplicationEventListener) xViewAppEventListener).
                        onImport();
            }
        }
    }

    @Override
    public void notifyOnOpenEventListeners() {
        for (OpenXDataViewApplicationEventListener xViewAppEventListener :
                viewApplicationEventListeners) {
            if (xViewAppEventListener instanceof OpenXDataExportImportApplicationEventListener) {
                ((OpenXDataExportImportApplicationEventListener) xViewAppEventListener).
                        onOpen();
            }
        }
    }

    @Override
    public void notifyOnSaveEventListeners() {
        for (OpenXDataViewApplicationEventListener xViewAppEventListener :
                viewApplicationEventListeners) {
            xViewAppEventListener.onSave();
        }
    }

    @Override
    public void notifyOnFormatEventListeners() {
        for (OpenXDataViewApplicationEventListener xViewAppEventListener :
                viewApplicationEventListeners) {
            if (xViewAppEventListener instanceof OpenXDataViewExtendedApplicationEventListener) {
                ((OpenXDataViewExtendedApplicationEventListener) xViewAppEventListener).
                        format();
            }
        }
    }

    @Override
    public void notifyOnRefreshEventListeners() {
        for (OpenXDataViewApplicationEventListener xViewAppEventListener :
                viewApplicationEventListeners) {
            xViewAppEventListener.onRefresh();
        }

    }

    @Override
    public void notifyOnNewItemEventListeners() {
        for (OpenXDataViewApplicationEventListener xViewAppEventListener :
                viewApplicationEventListeners) {
            xViewAppEventListener.onNewItem();
        }
    }

    @Override
    public void registerApplicationEventListener(
            OpenXDataViewApplicationEventListener eventListener) {
        viewApplicationEventListeners.add(eventListener);
    }

    @Override
    public void removeApplicationEventListener(
            OpenXDataViewApplicationEventListener eventListener) {
        viewApplicationEventListeners.remove(eventListener);
    }

    @Override
    public void registerAdvancedApplicationEventListener(
            OpenXDataViewExtendedApplicationEventListener eventListener) {
        viewApplicationEventListeners.add(eventListener);
    }

    @Override
    public void removeAdvancedApplicationEventListener(
            OpenXDataViewExtendedApplicationEventListener eventListener) {
        viewApplicationEventListeners.remove(eventListener);
    }

    @Override
    public void registerExportImportApplicationEventListener(
            OpenXDataExportImportApplicationEventListener eventListener) {
        viewApplicationEventListeners.add(eventListener);

    }

    @Override
    public void removeExportImportApplicationEventListener(
            OpenXDataExportImportApplicationEventListener eventListener) {
        viewApplicationEventListeners.remove(eventListener);
    }
}
