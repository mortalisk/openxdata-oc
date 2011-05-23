package org.openxdata.server.admin.client.view.widget;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * <code>Dialog</code> that notifies the <code>User</code> with a message.
 * 
 * @author Angel
 *
 */
public class OpenXDataMessageDialog extends DialogBox  {
	
	private String messageToDisplay = "";
	private Label messageLabel = new Label();
	private Button closeButton = new OpenXDataButton("Close");

	public OpenXDataMessageDialog(String dialogName){
		setText(dialogName);
		setupWidgets();
	}
	
	/**
	 * Sets up the dialog box with widgets.
	 */
	private void setupWidgets() {
		
		setText("System Notification");
		
		FlexTable table = new OpenXDataFlexTable();
		
		messageLabel.setWidth("400px");
		messageLabel.setHeight("100px");	
		closeButton.addStyleName("btn");
		
		table.setWidget(0, 0, messageLabel);
		table.setWidget(1, 0, closeButton);
		
		VerticalPanel panel = new VerticalPanel();
		panel.setSpacing(4);
		panel.add(table);
		panel.setCellHorizontalAlignment(closeButton, VerticalPanel.ALIGN_CENTER);	
		
		setWidget(panel);
		setupEventListeners();
		
		
		this.center();	
	}
	
	private void setupEventListeners(){
		closeButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				hide();}});
	}

	/**
	 * Sets the message to display.
	 * 
	 * @param messageToDisplay the messageToDisplay to set
	 */
	public void setMessageToDisplay(String messageToDisplay) {
		this.messageToDisplay = messageToDisplay;
		this.messageLabel.setText(messageToDisplay);
	}

	/**
	 * @return the messageToDisplay
	 */
	public String getMessageToDisplay() {
		return messageToDisplay;
	}

	public void onClick(Widget sender) {
		hide();
	}
}
