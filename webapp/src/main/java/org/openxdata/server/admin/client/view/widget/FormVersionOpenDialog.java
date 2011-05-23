package org.openxdata.server.admin.client.view.widget;

import org.openxdata.server.admin.client.internationalization.OpenXdataConstants;
import org.openxdata.server.admin.client.view.listeners.FormVersionOpenDialogListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Custom <code>widget</code> used 
 * to get the selected option when an <code>object</code> has <code>data.</code>
 * An <code>Object</code> can be a <code>Study, Form, Form version, report </code> etc.
 * 
 * @author Mark
 *
 */
public class FormVersionOpenDialog extends DialogBox{

    private static OpenXdataConstants constants = GWT.create(OpenXdataConstants.class);
	
	private int selectedOption = -1;
	
	private Label lblCaption;
	private HorizontalPanel panel = new HorizontalPanel();
	private VerticalPanel vPanel = new VerticalPanel();
	
	private Button btnCancel;
	private Button btnOpenReadOnly;
	private Button btnCreateNewVersion;
	 
	private FormVersionOpenDialogListener formVersionDialog;
	
	/**
	 * Constructor that takes a member listening for the selected option
	 * 
	 * @param formVersionDialog - member that is listening for the selected option
	 */
	public FormVersionOpenDialog(FormVersionOpenDialogListener formVersionDialog){
		this.formVersionDialog = formVersionDialog;
		
		initWidgets();		
		setupEventListeners();
	}

	/**
	 * Method to set up event listeners for the widgets
	 */
	private void setupEventListeners() {
		btnCancel.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				setSelectedOption(2);
				formVersionDialog.onOptionSelected(getSelectedOption());
				hide();
			}
			
		});
		
		btnOpenReadOnly.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				setSelectedOption(1);
				formVersionDialog.onOptionSelected(getSelectedOption());
				hide();
			}
			
		});
		
		btnCreateNewVersion.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				setSelectedOption(0);
				formVersionDialog.onOptionSelected(getSelectedOption());
				hide();
			}
			
		});
		
	}

	/**
	 * Member to initialize components to be bound to the widget
	 */
	private void initWidgets() {
		
		btnCancel = new OpenXDataButton(constants.label_cancel());
		btnOpenReadOnly = new OpenXDataButton(constants.label_openreadonly());
		btnCreateNewVersion = new OpenXDataButton(constants.label_createnew());
		
		lblCaption = new Label(constants.label_formDialogpromptmessage());
		
		vPanel.add(lblCaption);
		
		vPanel.setSpacing(5);
		vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		vPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		panel.setSpacing(20);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	
		
		panel.add(btnOpenReadOnly);
		panel.add(btnCreateNewVersion);
		panel.add(btnCancel);
		
		vPanel.add(panel);		
		setWidget(vPanel);		
		setText(constants.label_formversionedittitle());
	}
	
	/**
	 * Method that sets the selected option
	 * @param option - option to set
	 */
	public void setSelectedOption(int option){
		this.selectedOption = option;
	}
	
	/**
	 * Method to return the selected option
	 * @return - selected option
	 */
	public int getSelectedOption(){
		return this.selectedOption;
	}

}
