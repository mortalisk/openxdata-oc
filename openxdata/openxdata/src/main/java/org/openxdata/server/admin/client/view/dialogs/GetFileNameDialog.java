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

import org.openxdata.server.admin.client.listeners.GetFileNameDialogEventListener;
import org.openxdata.server.admin.client.view.widget.OpenXDataButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * This dialog box is used to get the name of a file from a user.
 * Something like a Save As.
 * An example is when one is exporting data to CSV and needs to give
 * the name of the CSV file.
 * 
 * @author daniel
 *
 */
public class GetFileNameDialog extends DialogBox{

	/** The main widget for this dialog box. */
	private VerticalPanel verticalPanel = new VerticalPanel();
	
	/** Listener to file selected event. */
	private GetFileNameDialogEventListener eventListener;
	
	/** text field for entering the file name. */
	private TextBox txtFileName = new TextBox();
	
	
	/**
	 * Creates a new instance of this dialog box.
	 * 
	 * @param eventListener the listener to file name selected event.
	 * @param title the dialog box title.
	 * @param commandText the text for the OK button.
	 * @param defaultName the default file name.
	 */
	public GetFileNameDialog(GetFileNameDialogEventListener eventListener, String title, String commandText,String defaultName){
		this.eventListener = eventListener;
		initWidgets(title,commandText,defaultName);
	}
	
	/**
	 * Builds the widgets for this dialog box.
	 * 
	 * @param title the dialog box title.
	 * @param commandText text for the OK button.
	 * @param defaultName the default file name.
	 */
	public void initWidgets(String title, String commandText, String defaultName){
		verticalPanel.setSpacing(5);
		txtFileName.setWidth("250px");
		verticalPanel.add(txtFileName);
		verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(20);
		horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.add(new OpenXDataButton(commandText, new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				eventListener.onSetFileName(txtFileName.getText());
				hide();
			}
		}));
		
		horizontalPanel.add(new Button("Cancel", new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				eventListener.onSetFileName(null);
				hide();	
			}
		}));
		
		verticalPanel.add(horizontalPanel);
		
		setWidget(verticalPanel);
		
		setText(title);
		txtFileName.setText(defaultName);
		txtFileName.setFocus(true);
	}
}
