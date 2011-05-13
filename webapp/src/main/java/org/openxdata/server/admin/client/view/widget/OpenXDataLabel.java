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
package org.openxdata.server.admin.client.view.widget;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Label;

/**
 * Extends the <tt>GWT Label</tt>.
 * <p>
 * We want to check the <tt>text</tt> to see if its null and then make the back ground transparent.
 * </p>
 * 
 * @author Angel
 *
 */
public class OpenXDataLabel extends Label {
	
	/** Constructs an instance of this <tt>Class.</tt> */
	public OpenXDataLabel(){}
	
	/**
	 * Constructs an instance of this <tt>class</tt> with a message to display.
	 * 
	 * @param text <tt>Message</tt> to display.
	 */
	public OpenXDataLabel(String text){
		setText(text);
	}
	
	/**
	 * Sets the <tt>Message</tt> to display on the <tt>Label.</tt>
	 */
	@Override
	public void setText(String text){
		
		//Set the Message on the Super class
		super.setText(text);
		
		//Checks if the message is space to make the 
		//notification bar translucent but also make it abide on the MainView.
		if(text.equals("_")){
			
			//Setting the background to white
			DOM.setStyleAttribute(this.getElement(), "color", "white");
		}
		else{
			
			//Setting the background to black for other Message
			DOM.setStyleAttribute(this.getElement(), "color", "black");
		}
	}
	
	/**
	 * Displays the Error Message with correct formatting.
	 * 
	 * @param errorMessage <tt>Error Message</tt> to display.
	 */
	public void setFailureText(String errorMessage){
		
		//Make the formatting stand out for the User.
		DOM.setStyleAttribute(this.getElement(), "color", "red");
		
		//Set the Message on the Super class
		super.setText(errorMessage);
	}

	/**
	 * Sets the default text.
	 */
	public void setDefaultText() {
		this.setText("_");		
	}
}
