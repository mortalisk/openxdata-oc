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
package org.openxdata.server.admin.client.tools;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.controller.callback.OpenXDataAsyncCallback;
import org.openxdata.server.admin.client.view.widget.OpenXDataButton;
import org.openxdata.server.admin.client.view.widget.OpenXDataFlexTable;
import org.openxdata.server.admin.model.User;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.purc.purcforms.client.util.FormUtil;


/**
 * This is a dialog box which lets the user install a mobile application to
 * one or more phones.
 * 
 * @author daniel
 *
 */
public class MobileInstaller extends DialogBox implements ClickHandler{

	/** The main widget for this dialog box. */
	private VerticalPanel verticalPanel = new VerticalPanel();
	
	/** For entering the modem port number. */
	private TextBox txtPort = new TextBox();
	
	/** For entering the modem baud rate. */
	private TextBox txtBaudrate = new TextBox();
	
	/** For entering OTA installation url. */
	private TextBox txtUrl = new TextBox();
	
	/** For enetering installation prompt text. */
	private TextBox txtPromptText = new TextBox();
	
	/** Table to hold the list of installaton phone numbers. */
	private FlexTable table = new OpenXDataFlexTable();
	
	/** Button to click when adding new phone numbers. */
	private Button btnAdd = new OpenXDataButton("Add New");	
	
	/**
	 * Creates a new instance of this class.
	 */
	public MobileInstaller(){
		
		setText("Mobile Installer");
		
		txtPort.setText("COM6");
		txtPort.setWidth("50px");
		txtBaudrate.setText("9600");
		txtBaudrate.setWidth("50px");
		txtPromptText.setText("Download EpihandyMobile");
		txtUrl.setText("http://cit3.mak.ac.ug/~smuwanga/epihandy");
		txtUrl.setWidth("450px");
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(5);
		horizontalPanel.add(new Label("Modem Port:"));
		horizontalPanel.add(txtPort);
		
		horizontalPanel.add(new Label("Baudrate:"));
		horizontalPanel.add(txtBaudrate);
		
		horizontalPanel.add(new Label("Prompt Text:"));
		horizontalPanel.add(txtPromptText);
		
		verticalPanel.add(horizontalPanel);
		
		horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(5);
		horizontalPanel.add(new Label("OTA Url:"));
		horizontalPanel.add(txtUrl);
		
		verticalPanel.add(horizontalPanel);
		
		table.setStyleName("cw-FlexTable");
		table.setWidget(0, 0,new Label("Phone Number (International Format)"));
		table.setWidget(0, 1,new Label("Action"));
		verticalPanel.add(table);
		
		btnAdd.addClickHandler(this);
		verticalPanel.add(btnAdd);
		
		horizontalPanel = new HorizontalPanel();
		horizontalPanel.setWidth("100%");
		horizontalPanel.setSpacing(20);
		horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.add(new OpenXDataButton("Install", new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				install();
			}
		}));
		
		horizontalPanel.add(new Button("Close", new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				hide();
			}
		}));
		
		horizontalPanel.setCellHorizontalAlignment(horizontalPanel.getWidget(0), HasHorizontalAlignment.ALIGN_LEFT);
		horizontalPanel.setCellHorizontalAlignment(horizontalPanel.getWidget(1), HasHorizontalAlignment.ALIGN_RIGHT);
		
		verticalPanel.add(horizontalPanel);
		
		setWidget(verticalPanel);
		
		loadPhoneNos();
		
		addNewPhoneNo("");
	}
	
	/**
	 * Called when one click the AddNew or Remove Phone number button.
	 * 
	 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
	 */
	@Override
	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		if (source.equals(btnAdd)){
			addNewPhoneNo("");
		}
		else{
			//User clicked the remove button.
			int rowCount = table.getRowCount();
			if(rowCount == 2)
				return; //Must always have atleast one phone number text field
			
			//Remove the selected phone number from the table.
			for(int row = 0; row < rowCount; row++){
				if(source.equals(table.getWidget(row, 1))){
					table.removeRow(row);
					break;
				}
			}
		}
	}
	
	/**
	 * Adds a new phone number to install.
	 * 
	 * @param phoneNo the phone number.
	 */
	private void addNewPhoneNo(String phoneNo){
		int row = table.getRowCount();
		
		TextBox txtPhoneNo = new TextBox();
		txtPhoneNo.setText(phoneNo);
		table.setWidget(row, 0,txtPhoneNo);
		
		Button button = new OpenXDataButton("Remove");
		button.addClickHandler(this);
		table.setWidget(row, 1,button);

		table.getFlexCellFormatter().setWidth(row, 1, "10%");
		table.getWidget(row, 0).setWidth("100%");
	}
	
	/**
	 * Starts the installation process.
	 */
	private void install(){
		FormUtil.dlg.setText("Sending Installation Service Messages");
		FormUtil.dlg.center();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				try{
					//Create a list of phone numbers to pass to the server.
					List<String> phonenos = new ArrayList<String>();
					int count = table.getRowCount();
					for(int row = 1; row < count; row++){
						String phoneno = ((TextBox)table.getWidget(row, 0)).getText();
						if(phoneno != null && phoneno.trim().length() > 0)
							phonenos.add(phoneno);
					}
						
					//If no phone number entered, just do nothing.
					if(phonenos.size() == 0){
						FormUtil.dlg.hide();
						return;
					}
					
					//Call the server installation service.
					Context.getUtilityService().installMobileApp(phonenos, txtUrl.getText(), txtPort.getText(), Integer.parseInt(txtBaudrate.getText()), txtPromptText.getText(), new OpenXDataAsyncCallback<Boolean>() {
						@Override
						public void onOtherFailure(Throwable caught) {
							FormUtil.dlg.hide();
							Window.alert(caught.getMessage());
						}

						@Override
						public void onSuccess(Boolean object) {
							FormUtil.dlg.hide();
							//TODO add message for internationalization purposes
							String msg = "Installation Service Messages sent Successfully";
							if(!object)
								msg = "Failed to send Installation Service Messages. Check server log";
							Window.alert(msg);
						}
					});
				}
				catch(Exception ex){
					FormUtil.dlg.hide();
					FormUtil.displayException(ex);
				}
			}
		});
	}
	
	/**
	 * Automatically loads phone numbers for those users who were
	 * registered with phone numbers.
	 */
	private void loadPhoneNos(){
		List<User> users = Context.getUsers();
		assert(users != null); //we should always have atleast one user.
		for(User user : users){
			String phoneNo = user.getPhoneNo();
			if(phoneNo != null && phoneNo.trim().length() > 0)
				addNewPhoneNo(phoneNo);
		}
	}
}
