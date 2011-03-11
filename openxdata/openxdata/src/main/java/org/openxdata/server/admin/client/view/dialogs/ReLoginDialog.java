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
package org.openxdata.server.admin.client.view.dialogs;

import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.controller.callback.OpenXDataAsyncCallback;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.widget.OpenXDataButton;
import org.openxdata.server.admin.client.view.widget.OpenXDataFlexTable;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataException;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * This dialog is fired when the <code>User</code>
 * session has expired and they are required to re login. 
 * <p>
 * The idea is to take the <code>User</code> back to where they were with all their work intact.
 * </P>
 * 
 * @author Angel
 *
 */
public class ReLoginDialog extends DialogBox {
	
	/**
	 * The login <code>button</code> on the <code>Dialog.</code>
	 */
	private Button loginBtn = null;
	
	/**
	 * User name <code>text box.</code>
	 */
	private static TextBox userNameTextBox = null;
	
	/**
	 * Password <code>text box.</code>
	 */
	private static TextBox passwordTextBox = null;
	
	/**
	 * Utility <code>label</code> to display the ancillary messages
	 */
	private static Label utilLabel = null;
	
	/** 
	 * <code>Singleton instance</code> of this <code>Object.</code>
	 * */
	private static ReLoginDialog INSTANCE;
	
	/**
	 * <code>Private Constructor</code> 
	 * that takes a <code>String</code> which is to be used as the <code>Dialog title.</code>
	 * 
	 * @param dialogName <code>Title</code> of the <code>Dialog.</code>.
	 * <p>
	 * Dialog Name will also be used in the 
	 * <code> toString()</code> method to return 
	 * a <code>String</code> representation of the <code>Object.</code>
	 * </p>
	 */
	private ReLoginDialog(String dialogName){
		
		assert(dialogName.length() > 0);
		
		setText(dialogName);
		
		setUpWidgets();
		setupEventListeners();
	}
	
	/**
	 * Initializes the <code>widgets</code> for the <code>Control.</code>
	 */
	private void setUpWidgets() {
		
		// Table to organize widgets.
		FlexTable table = new OpenXDataFlexTable();	
		FlexCellFormatter formatter = table.getFlexCellFormatter();
		
		table.setWidget(0, 0, new Label("To Resume, Enter your credentials."));
		formatter.setColSpan(0, 0, 7);
		formatter.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
		
		table.setWidget(1, 0, utilLabel = new Label(" "));
		formatter.setColSpan(1, 0, 7);
		
		Label nameLabel = new Label("UserName:");
		table.setWidget(2, 0, nameLabel);
		formatter.setColSpan(2, 0, 2);
		formatter.setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_LEFT);
		
		userNameTextBox = new TextBox();
		userNameTextBox.setWidth("70%");
		table.setWidget(2, 1, userNameTextBox);
		formatter.setWidth(2, 1, "70%");
		
		Label passLabel = new Label("Password:");
		table.setWidget(3, 0, passLabel);
		formatter.setColSpan(3, 0, 2);
		formatter.setHorizontalAlignment(3, 0, HasHorizontalAlignment.ALIGN_LEFT);
		
		passwordTextBox = new PasswordTextBox();
		passwordTextBox.setWidth("70%");
		table.setWidget(3, 1, passwordTextBox);
		formatter.setWidth(3, 1, "70%");
		
		loginBtn = new OpenXDataButton("Re-Login");
		loginBtn.addStyleName("btn");
		table.setWidget(4, 0, loginBtn);
		formatter.setHorizontalAlignment(4, 0, HasHorizontalAlignment.ALIGN_LEFT);
				
		VerticalPanel panel = new VerticalPanel();
		
		Utilities.maximizeWidget(panel);
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.add(table);
		
		setWidget(panel);
		
		if(Context.getAuthenticatedUser() != null)
			userNameTextBox.setText(Context.getAuthenticatedUser().getName());
	}

	/**
	 * Sets up <code>event listeners</code>
	 * for the <code>Controls</code> on the <code>widget.</code>
	 */
	private void setupEventListeners() {
		passwordTextBox.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				char keyCode = event.getCharCode();
				if(keyCode == KeyCodes.KEY_ENTER)
					try {
						reAuthenticate();
					} catch (OpenXDataException e) {
						Window.alert(e.getLocalizedMessage());
					}
			}
		});
		
		loginBtn.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				try {
					reAuthenticate();
				} catch (OpenXDataException e) {
					Window.alert(e.getLocalizedMessage());
				}}});
	}

	/**
	 * Returns an instance of <code>ReLoginDialog.</code>
	 * 
	 * @param dialogName <code>Title</code> to be displayed on the <code>Dialog.</code>
	 * @return <code>ReLoginDialog instance.</code>
	 */
	public static ReLoginDialog instanceOfReLoginDialog(String dialogName){
		
		if(utilLabel != null)
			utilLabel.setText("");
		
		if(userNameTextBox != null)
			userNameTextBox.setText(Context.getAuthenticatedUser().getName());
		
		if(passwordTextBox != null)
			passwordTextBox.setText("");
		
		if (INSTANCE == null) {
			 INSTANCE = new ReLoginDialog(dialogName);
		}
		
		return INSTANCE;
		
	}
	
	/**
	 * Initiates calls to re authenticate the <code>User.</code>
	 * 
	 * @throws OpenXDataException 
	 * <P><code>
	 * if user name text box == null or
	 * if password text box == null.
	 * </code></p>
	 */
	protected void reAuthenticate() throws OpenXDataException {
		
		User currentlyLoggedOnUser = Context.getAuthenticatedUser();
		
		if(userNameTextBox.getText().length() <= 0)
			throw new OpenXDataException("Enter User Name to Proceed");
		
		if(passwordTextBox.getText().length() <= 0)
			throw new OpenXDataException("Enter Password to Proceed");
		
		String username = userNameTextBox.getText();
		String password = passwordTextBox.getText();
		
		checkCredentials(username, password, currentlyLoggedOnUser);		
	}

	/**
	 * Re Authenticates the <code>User</code> on the <code>Server.</code>
	 * 
	 * @param username the current <code>User's</code> user name.
	 * @param password the current <code>User's</code> password.
	 * @param currentlyLoggedOnUser Currently logged on <code>User.</code>
	 */
	private void checkCredentials(String username, String password, final User currentlyLoggedOnUser ) {
		Context.getAuthenticationService().authenticate(username, password, new OpenXDataAsyncCallback<User>() {

			@Override
			public void onSuccess(User result) {
				onReAuthenticationPassed(result, currentlyLoggedOnUser);
				
			}

			@Override
			public void onOtherFailure(Throwable throwable) {
				onReAuthenticationFailed(throwable.getLocalizedMessage());
				
			}
		});
	}

	/**
	 * Displays failure message to user if Re Authentication
	 * fails due to wrong credentials or new login.
	 * 
	 * @param message Message to display.
	 */
	protected void onReAuthenticationFailed(String message) {
		utilLabel.setText(message);
		DOM.setStyleAttribute(utilLabel.getElement(), "color", "red");
		
		passwordTextBox.setText("");
	}

	/**
	 * Returns the user to the point they were prior
	 * to the session time out.
	 * 
	 * @param user User object to authenticate.
	 * @param currentlyLoggedOnUser Currently Logged on user.
	 */
	protected void onReAuthenticationPassed(User user, User currentlyLoggedOnUser) {
		if(user != null){
			if(user.getUserId() == currentlyLoggedOnUser.getUserId()){
				this.hide();
			}				
			else{
				String newLoginMsg = 
					"System cannot login you in because " +
					"your credentials DO NOT match those of " +
					"the currently logged on User. Current User's data might be lost!";
				
				onReAuthenticationFailed(newLoginMsg);
			}				
		}			
		else{
			String msg = 
				"Authentication Failed! " +
				"User credentials do not match!";
			
			onReAuthenticationFailed(msg);
		}
	}
}
