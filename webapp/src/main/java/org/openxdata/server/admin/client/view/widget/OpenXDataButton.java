package org.openxdata.server.admin.client.view.widget;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

/**
 * Extends the <code>GWT Button</code> to add default style names.
 * 
 * @author Angel
 *
 */
public class OpenXDataButton extends Button {
	
	public OpenXDataButton(String buttonName){
		super(buttonName);
		this.setStyleName("btn");
	}
	
	/**
	 * @param string
	 * @param clickListener
	 */
	public OpenXDataButton(String buttonName, ClickHandler clickHandler) {
		super(buttonName, clickHandler);
	}

	public void setMaximumWidth(){
		this.setWidth("100%");
	}
}
