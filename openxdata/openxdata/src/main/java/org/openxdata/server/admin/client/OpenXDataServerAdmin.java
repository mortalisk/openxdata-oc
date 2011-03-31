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
package org.openxdata.server.admin.client;

import org.openxdata.server.admin.client.controller.callback.OpenXDataAsyncCallback;
import org.openxdata.server.admin.client.controller.facade.MainViewControllerFacade;
import org.openxdata.server.admin.client.listeners.LoginListener;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.LoginView;
import org.openxdata.server.admin.client.view.MainView;
import org.openxdata.server.admin.client.view.dialogs.PasswordChangeDialog;
import org.openxdata.server.admin.client.view.widget.factory.OpenXDataWidgetFactory;
import org.openxdata.server.admin.model.User;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import java.util.HashSet;
import org.openxdata.server.admin.client.permissions.PermissionResolver;
import org.openxdata.server.admin.client.permissions.util.RolesListUtil;
import org.openxdata.server.admin.client.view.event.LogOutEvent;
import org.openxdata.server.admin.client.view.factory.OpenXDataWidgetGinInjector;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.sharedlib.client.locale.FormsConstants;
import org.openxdata.sharedlib.client.util.FormUtil;

/**
 * Entry point classes define <code>onModuleLoad()</code>. This acts as the
 * controller for starting the application.
 * 
 * @author Angel
 */
public class OpenXDataServerAdmin implements EntryPoint, ResizeHandler,
        LoginListener {

    final FormsConstants i18n = GWT.create(FormsConstants.class);

        // mainView is not instantiated here because the form designer route panel
        // will be referencing
        // that of the login view and as a result widget drag will have no valid
        // drop targets
        private MainView mainView;
        // The Login View.
        private LoginView loginView = new LoginView(this);
        private final OpenXDataWidgetFactory widgetFactory;
        private static OpenXDataWidgetGinInjector injector = GWT.create(OpenXDataWidgetGinInjector.class);
        private EventBus eventBus;
        public OpenXDataServerAdmin() {
                widgetFactory = injector.getWidgetFactory();
                widgetFactory.setInjector(injector);
                eventBus = injector.getEventBus();
                bindHandlers();
        }

        /**
         * This is the entry point method.
         *
         */
        @Override
        public void onModuleLoad() {

                FormUtil.setupUncaughtExceptionHandler();

                FormUtil.retrieveUserDivParameters();

                Context.startup();

                // FormDesignerWidget designer = new FormDesignerWidget(true,true,true);

                // Hook the window resize event, so that we can adjust the UI.
                Window.addResizeHandler(this);

                // Get rid of scrollbars, and clear out the window's built-in margin,
                // because we want to take advantage of the entire client area.
                Window.enableScrolling(false);
                Window.setMargin("0px");

                // Different themes use different background colors for the body
                // element, but IE only changes the background of the visible content
                // on the page instead of changing the background color of the entire
                // page. By changing the display style on the body element, we force
                // IE to redraw the background correctly.
                RootPanel.getBodyElement().getStyle().setProperty("display", "none");
                RootPanel.getBodyElement().getStyle().setProperty("display", "");

                doLogin();

                FormUtil.dlg.hide(); // Temporary fix This comes from the form designer
                // where it loads and never closes
        }

        private void doLogin() {
                // note: I am purposefully using AsyncCallback and not
                // OpenXDataAsyncCallback because I do not want the
                // automatic re-login behaviour. In fact, I want to suppress any error
                // and just direct the user to the login screen
                Context.getUserService().getLoggedInUser(new AsyncCallback<User>() {

                        @Override
                        public void onFailure(Throwable caught) {
                                // some error happened, please go to the login screen (all
                                // errors are supressed)
                                RootPanel.get().add(loginView);
                        }

                        @Override
                        public void onSuccess(User user) {
                                // if a user is found, we can bypass the login screen
                                onSuccessfulLogin(user);
                        }
                });
        }

        /**
         * Validates the <tt>User</tt> attempting to log in and logs them in if they
         * are allowed access.
         */
        @Override
        public void onLogin(String userName, String password) {
                Context.getAuthenticationService().authenticate(userName, password,
                        new OpenXDataAsyncCallback<User>() {

                                @Override
                                public void onOtherFailure(Throwable caught) {
                                        FormUtil.displayException(caught);
                                }

                                @Override
                                public void onSuccess(User user) {
                                        if (user != null) {
                                                if (!user.isDisabled()) {
                                                        onSuccessfulLogin(user);
                                                } else {
                                                        loginView.onUnSuccessfulLogin("You account has been disabled. Contact the system administrator.");
                                                        logOut(false);
                                                }
                                        } else
                                                loginView.onUnSuccessfulLogin("Invalid UserName or Password");
                                }
                        });
        }

        /**
         * Finalizes the <tt>User</tt> login.
         *
         * @param user
         *            Authenticated <tt>User.</tt>
         */
        private void onSuccessfulLogin(User user) {

            FormUtil.dlg.setText(i18n.loading());
            FormUtil.dlg.center();
            if (user.getRoles().isEmpty()) {
                loginView.onUnSuccessfulLogin("You have No Permissions! Contact the Administrator");
                logOut(false);
                return;
            } else
                initPermissions(user);

                final User loggedInUser = user;
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                        @Override
                        public void execute() {
                                try {

                                        // Set the authenticated User
                                        // in the Context for global access.
                                        Context.setAuthenticatedUser(loggedInUser);

                                        while (RootPanel.get().getWidgetCount() > 0) {
                                                RootPanel.get().remove(0);
                                        }

                                        if (mainView == null) {


                                                // Inject Widget Factory into other classes.
                                                injectWidgetFactory(widgetFactory);

                                                // Construct the MainView.

                                                mainView = widgetFactory.getMainView();
                                                // Load Preliminary data.
                                                MainViewControllerFacade.loadPreliminaryViewData();

                                                RootPanel.get().add(mainView);

                                                FormUtil.dlg.hide();

                                                // Call the window resized handler to get the initial
                                                // sizes setup.
                                                // Doing this in a deferred command causes it to occur
                                                // after all widgets' sizes have been computed by the
                                                // browser.
                                                Scheduler.get().scheduleDeferred(
                                                        new ScheduledCommand() {

                                                                @Override
                                                                public void execute() {
                                                                        resizeMainView(Window.getClientWidth(),
                                                                                Window.getClientHeight());

                                                                        // We check User properties after all
                                                                        // operations have been completed.
                                                                        Scheduler.get().scheduleDeferred(
                                                                                new ScheduledCommand() {

                                                                                        @Override
                                                                                        public void execute() {
                                                                                                checkIfUserChangedProperties();
                                                                                        }
                                                                                });
                                                                }
                                                        });
                                        }
                                } catch (Exception ex) {
                                        FormUtil.dlg.hide();
                                        FormUtil.displayException(ex);
                                }
                        }
                });
        }

        /**
         * Injects the <tt>Widget Factory</tt> into other classes.
         *
         * @param widgetFactory
         *            <tt>Widget Factory</tt> that has been configured for this
         *            session to inject.
         */
        private void injectWidgetFactory(OpenXDataWidgetFactory widgetFactory) {

                // Inject Widget Factory into Utilities.
                Utilities.setWidgetFactory(widgetFactory);

                // Inject Widget Factory into MainViewControllerFacade
                MainViewControllerFacade.setWidgetFactory(widgetFactory);
        }

        /**
         * Checks if <tt>User</tt> changed properties.
         */
        private void checkIfUserChangedProperties() {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                        @Override
                        public void execute() {
                                if (Context.getAuthenticatedUser().isDefaultAdministrator()) {
                                        Context.getUserService().getUser("admin",
                                                new OpenXDataAsyncCallback<User>() {

                                                        @Override
                                                        public void onOtherFailure(Throwable throwable) {
                                                                Utilities.displayMessage(throwable.getLocalizedMessage());
                                                        }

                                                        @Override
                                                        public void onSuccess(User user) {
                                                                checkIfAdministratorChangedPassword(user);
                                                        }
                                                });
                                }

                        }
                });
        }

        /**
         * Checks if the administrator changed the default password.
         *
         * @param user
         *            <code>User</code> to check.
         */
        protected void checkIfAdministratorChangedPassword(User user) {
                Context.getUserService().checkIfUserChangedPassword(user,
                        new OpenXDataAsyncCallback<Boolean>() {

                                @Override
                                public void onOtherFailure(Throwable throwable) {
                                        Utilities.displayMessage(throwable.getLocalizedMessage());

                                }

                                @Override
                                public void onSuccess(Boolean passwordChanged) {
                                        displayDialogToChangePassword(passwordChanged);

                                }
                        });

        }

        /**
         * Display the dialog to allow the <code>Administrator User</code> to change
         * the default password.
         *
         * @param passwordChanged
         *            Flag to indicate administrator <tt>User Password</tt> was
         *            changed or not.
         */
        protected void displayDialogToChangePassword(final Boolean passwordChanged) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                        @Override
                        public void execute() {
                                if (!passwordChanged) {

                                        // Prompt User to Change Administrator Password.
                                        new PasswordChangeDialog().initializeDialog();

                                        // Alert the User of the operation.
                                        Utilities.displayMessage("For security reasons, the administrator should change their password on initial login. "
                                                + "Please change your password to avoid seeing this message and to protect your data.");
                                }
                        }
                });
        }

        /**
         * Logs out the current logged in <tt>User.</tt>
         */
        public void logOut(final boolean reloadPage) {
                Context.getUserService().logout(new OpenXDataAsyncCallback<Void>() {

                        @Override
                        public void onSuccess(Void result) {
                            if(reloadPage)
                            onSuccessfullLogout();
                        }

                        @Override
                        public void onOtherFailure(Throwable throwable) {
                                // Do nothing -- probably log the message.
                        }
                });
        }

        /**
         * Finalizes the <tt>User</tt> Logout operation.
         */
        protected void onSuccessfullLogout() {

                Window.Location.replace(GWT.getModuleBaseURL()
                        + "OpenXDataServerAdmin.html");

                loginView.clearPassword();
                Context.setAuthenticatedUser(null);

                Window.Location.reload();

        }

        /**
         * @see com.google.gwt.event.logical.shared.ResizeHandler#onResize(com.google.gwt.event.logical.shared.ResizeEvent)
         */
        @Override
        public void onResize(ResizeEvent event) {
                int width = event.getWidth();
                int height = event.getHeight();
                resizeMainView(width, height);
        }

        /**
         * Resizes the <tt>MainView</tt> in the <tt>browser.</tt>
         */
        private void resizeMainView(int width, int height) {
                if (mainView != null) {
                        mainView.resize(width, height);
                }
        }

        private void initPermissions(User user) {
                RolesListUtil.getInstance().setUserRoles(user.getRoles());
                if (RolesListUtil.getInstance().isAdmin()) {
                        GWT.log("User is admin");
                        // Construct a Permission Resolving object for this session for Administrative User
                        RolesListUtil.setPermissionResolver(new PermissionResolver(true, new HashSet<Permission>()));
                } else if (RolesListUtil.getInstance().hasUserGotRoles()) {
                         GWT.log("User is normal");
                        // Construct a Permission Resolving object for this session according to User Roles.
                        RolesListUtil.setPermissionResolver(new PermissionResolver(false, RolesListUtil.getInstance().getUserPermissions()));
                }else{
                    GWT.log("USer had no roles");
                }
        }

    private void bindHandlers() {
        LogOutEvent.Handler logOutHandler = new LogOutEvent.Handler() {

            @Override
            public void onLogout() {
               logOut(true);
            }
        };
        eventBus.addHandler(LogOutEvent.TYPE, logOutHandler);
    }
}
