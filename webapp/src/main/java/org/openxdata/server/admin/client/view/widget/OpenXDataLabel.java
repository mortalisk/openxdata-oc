package org.openxdata.server.admin.client.view.widget;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Label;

/**
 * Extends the <tt>GWT Label</tt>.
 * <p>
 * We want to check the <tt>text</tt> to see if its null and then make the back ground transparent.
 * </p>
 * 
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
