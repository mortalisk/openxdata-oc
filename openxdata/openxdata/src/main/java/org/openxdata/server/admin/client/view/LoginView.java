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
package org.openxdata.server.admin.client.view;

import org.openxdata.server.admin.client.internationalization.OpenXDataFacade;
import org.openxdata.server.admin.client.internationalization.OpenXdataConstants;
import org.openxdata.server.admin.client.listeners.LoginListener;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.widget.OpenXDataButton;
import org.openxdata.server.admin.client.view.widget.OpenXDataFlexTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is the widget used for user login.
 * 
 * @author daniel
 * 
 */
public class LoginView extends Composite {
	
	/** Listener to login events. */
	private LoginListener loginListener;
	
	/** Text field for entering the user name. */
	private TextBox usernameText;
	
	/** Text field for entering the user password. */
	private PasswordTextBox passwordText;
	
	/** Table widget for laying out widgets in table format. */
	private FlexTable table = new OpenXDataFlexTable();
	
	/**
	 * 
	 * @param loginListener
	 *            the listener to login events.
	 */
	public LoginView(LoginListener loginListener) {
		this.loginListener = loginListener;
		setupWidget();
	}
	
	private void setupWidget() {
		OpenXdataConstants constants = GWT.create(OpenXdataConstants.class);
		
		table.setWidget(0, 0,
		        new Label(OpenXDataFacade.getDictionary().get("welcome")));
		table.setWidget(1, 0, new Label("                      "));
		
		Label passwordLabel = new Label(constants.label_username());
		table.setWidget(2, 0, passwordLabel);
		
		usernameText = new TextBox();
		usernameText.setWidth("95%");
		table.setWidget(2, 1, usernameText);
		
		usernameText.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					passwordText.setFocus(true);
				}
			}
		});
		
		passwordLabel = new Label(constants.label_password());
		table.setWidget(3, 0, passwordLabel);
		
		passwordText = new PasswordTextBox();
		passwordText.setWidth("95%");
		table.setWidget(3, 1, passwordText);
		
		passwordText.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					doLogin();
				}
			}
		});
		
		Button loginButton = new OpenXDataButton("Login", new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				doLogin();
			}
		});
		
		loginButton.setStyleName("btn");
		
		table.setWidget(4, 0, new Label(""));
		table.setWidget(5, 0, loginButton);
		
		FlexCellFormatter formatter = table.getFlexCellFormatter();
		formatter.setColSpan(4, 0, 2);
		formatter.setColSpan(5, 0, 2);
		formatter.setColSpan(0, 0, 2);
		
		formatter.setHorizontalAlignment(0, 0,
		        HasHorizontalAlignment.ALIGN_CENTER);
		formatter.setHorizontalAlignment(5, 0,
		        HasHorizontalAlignment.ALIGN_CENTER);
		formatter.setHorizontalAlignment(2, 0,
		        HasHorizontalAlignment.ALIGN_RIGHT);
		formatter.setHorizontalAlignment(3, 0,
		        HasHorizontalAlignment.ALIGN_RIGHT);
		
		formatter.setWidth(2, 1, "50%");
		formatter.setWidth(3, 1, "50%");
		
		table.addStyleName("FlexTable");
		table.getRowFormatter().addStyleName(0, "FlexTable-Header");
		
		VerticalPanel panel = new VerticalPanel();
		Utilities.maximizeWidget(panel);
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		panel.add(table);
		initWidget(panel);
	}
	
	private void doLogin() {
		String name = usernameText.getText();
		String password = passwordText.getText();
		
		loginListener.onLogin(name, password);
	}
	
	/**
	 * Clears the user password if any. This is normally every time this widget
	 * is to be redisplayed after the user has already entered a password.
	 */
	public void clearPassword() {
		passwordText.setText("");
	}
	
	/**
	 * This method should be called every time the user tries and fails to
	 * login.
	 */
	public void onUnSuccessfulLogin() {
		usernameText.setText("");
		passwordText.setText("");
		
		Label label = new Label("Invalid UserName or Password");
		table.setWidget(6, 1, label);
		FlexCellFormatter formatter = table.getFlexCellFormatter();
		
		formatter.setHorizontalAlignment(6, 0,
		        HasHorizontalAlignment.ALIGN_CENTER);
		DOM.setStyleAttribute(label.getElement(), "color", "red");
		
		usernameText.setFocus(true);
	}
}
