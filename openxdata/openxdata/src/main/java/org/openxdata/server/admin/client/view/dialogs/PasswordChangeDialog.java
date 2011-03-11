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
 * Allows <code>Administrator User</code> to change the default password on initial login.
 * 
 * @author Angel
 *
 */
public class PasswordChangeDialog extends DialogBox {

	private Button okButton;
	private Button cancelButton;
	
	private TextBox oldPasswordTextBox;
	private TextBox newPasswordTextBox;
	private TextBox repeatNewPasswordTextBox;
	
	public PasswordChangeDialog(){
		setText("Enter Details to Change Your Password");
	}
	
	/**
	 * Sets up the dialog with the necessary widgets.
	 */
	private void setupWidgets() {
	
		FlexTable table = new OpenXDataFlexTable();		
		FlexCellFormatter formatter = table.getFlexCellFormatter();
		
		formatter.setColSpan(0, 0, 7);
		formatter.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
		
		Label oldPasswordLabel = new Label("Old Password: ");
		formatter.setColSpan(1, 0, 7);
		oldPasswordLabel.setWordWrap(false);
		table.setWidget(1, 0, oldPasswordLabel);
		formatter.setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_LEFT);
		
		oldPasswordTextBox = new PasswordTextBox();
		formatter.setWidth(1, 1, "80%");
		oldPasswordTextBox.setWidth("95%");
		table.setWidget(1, 1, oldPasswordTextBox);
		
		Label newPasswordLabel = new Label("New Password: ");
		formatter.setColSpan(2, 0, 7);
		newPasswordLabel.setWordWrap(false);
		table.setWidget(2, 0, newPasswordLabel);
		formatter.setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_LEFT);
		
		newPasswordTextBox = new PasswordTextBox();
		formatter.setWidth(2, 1, "80%");
		newPasswordTextBox .setWidth("95%");
		table.setWidget(2, 1, newPasswordTextBox);		
		
		Label repeatNewPasswordLabel = new Label("Re-Enter Password: ");
		formatter.setColSpan(3, 0, 7);
		repeatNewPasswordLabel.setWordWrap(false);		
		table.setWidget(3, 0, repeatNewPasswordLabel);
		formatter.setHorizontalAlignment(3, 0, HasHorizontalAlignment.ALIGN_LEFT);
		
		repeatNewPasswordTextBox = new PasswordTextBox();
		formatter.setWidth(3, 1, "80%");
		repeatNewPasswordTextBox .setWidth("95%");
		table.setWidget(3, 1, repeatNewPasswordTextBox);
		
		okButton = new OpenXDataButton("Ok");
		okButton.setWidth("100px");
		okButton.setStyleName("btn");
		table.setWidget(4, 0, okButton);
		formatter.setHorizontalAlignment(4, 0, HasHorizontalAlignment.ALIGN_LEFT);
		
		cancelButton = new OpenXDataButton("Cancel");
		cancelButton.setWidth("100px");
		cancelButton.setStyleName("btn");
		table.setWidget(4, 1, cancelButton);
		formatter.setHorizontalAlignment(4, 0, HasHorizontalAlignment.ALIGN_LEFT);
		
		VerticalPanel panel = new VerticalPanel();
		
		Utilities.maximizeWidget(panel);
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		panel.add(table);
		
		setWidget(panel);
	}

	/**
	 * Sets the widgets <code>Event Listeners.</code>
	 */
	private void setupEventListeners() {
		okButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				checkIfPasswordsMatch();				
			}});
		
		cancelButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				abortChangePasswordOperation();
				
			}});
	}

	/**
	 * Resets the Administrator password to the new one specified by the <tt>User.</tt>
	 * 
	 * @param passwordMatched Flag indicating if the password check operation matched.
	 * 
	 */
	protected void changeAdministratorPassword(Boolean passwordMatched) {
		
		if(passwordMatched){
			if(newPasswordTextBox.getText().length() > 0 &&
					repeatNewPasswordTextBox.getText().length() > 0){
				
				String newPassword = newPasswordTextBox.getText();
				String repeatedNewPassword = repeatNewPasswordTextBox.getText();
				
				if(newPassword.equals(repeatedNewPassword)){
					for(User x : Context.getUsers()){
						if(x.getName().equals("admin")){
							saveUserDetails(x);
							break;
						}
					}
				}
				else{
					String newPasswordMismatchMessage =	"The new passwords do not match! Enter similar passwords to proceed.";
					Utilities.displayMessage(newPasswordMismatchMessage);
					newPasswordTextBox.setText("");
					repeatNewPasswordTextBox.setText("");
				}
			}
		} 
		else{
				String passwordMismatchMessage =
					"The password you entered is not the same as the current administrator password. Check to proceed!";
				
				Utilities.displayMessage(passwordMismatchMessage);
				oldPasswordTextBox.setText("");
		}
	}
	
	/**
	 * Initiates the Saving of the new details of the administrator.
	 * 
	 * @throws OpenXDataException 
	 */
	protected void checkIfPasswordsMatch()  {
		if(oldPasswordTextBox.getText().length() > 0){
			String oldPassword = oldPasswordTextBox.getText();			
			Context.getUtilityService().checkIfPasswordsMatchOnAdministrator("admin", oldPassword, new OpenXDataAsyncCallback<Boolean>(){

				@Override
				public void onOtherFailure(Throwable throwable) {
					Utilities.displayMessage(throwable.getLocalizedMessage());
					
				}
				
				@Override
				public void onSuccess(Boolean passwordMatched) {
					changeAdministratorPassword(passwordMatched);
					
				}});
		}
	}
	
	/**
	 * Save the new administrator details.
	 */
	protected void saveUserDetails(User adminUser) {
		
		String newPassword = newPasswordTextBox.getText();		
		if(newPassword.equals("admin")){
			Utilities.displayMessage("The new password is the same as the old one. For security purposes, you are recommended to enter a different " +
					"strong password.");
			
			newPasswordTextBox.setText("");
			repeatNewPasswordTextBox.setText("");
		}
		else{
			if(validateUserPassword()){
                adminUser.setClearTextPassword(newPassword);
                Context.getUserService().saveUser(adminUser, new OpenXDataAsyncCallback<Void>() {

                    @Override
					public void onOtherFailure(Throwable throwable) {
                        Utilities.displayMessage(throwable.getLocalizedMessage());
                    }

                    @Override
					public void onSuccess(Void result) {
                        finalizePasswordChange();
                    }
                });
            }
		}
	}

	/**
	 * Checks that the <code>User</code> password is of the system specified length.
	 * 
	 * @param adminUser <code>User</code> to check password on
	 * @return <code>True only and only if(password.length >= systemPasswordLength)</code>
	 */
	private boolean validateUserPassword() {
		int newPasswordLength = newPasswordTextBox.getText().length();
		String systemSettingPasswordLengthValue = Context.getSetting("defaultPasswordLength", "6");
		
		int passwordLengthValue = Integer.parseInt(systemSettingPasswordLengthValue);
		if(newPasswordLength >= passwordLengthValue){
			return true;
		}
		else{
			String message =
				"The User password specified is less than the default length that is specified in the system. " +
				"The Password should be equal or more than " + Context.getSetting("defaultUserPasswordLength", "6") + " characters.";
			
			Utilities.displayMessage(message);
			return false;
		}
	}

	/**
	 * Aborts the whole change password operation.
	 */
	protected void abortChangePasswordOperation() {		
		String abortedPasswordChangeNotificationMessage = 
			"You have chosen to cancel the Password Change Operation. It is advisable that you do this as soon as possible!";
		
		Utilities.displayMessage(abortedPasswordChangeNotificationMessage);		
		this.hide();
		
	}
	
	/**
	 * Finalizes the Password Change Operation by notifying the <tt>User.</tt>
	 * 
	 * @param result
	 */
	protected void finalizePasswordChange() {
		String message = "Administrator Password Changed successfully";
		Utilities.displayMessage(message);
		this.hide();
		
	}
	
	/**
	 * Initializes this <tt>Dialog</tt> for display.
	 */
	public void initializeDialog(){
		setupWidgets();
		setupEventListeners();
		
		center();
	}
}
