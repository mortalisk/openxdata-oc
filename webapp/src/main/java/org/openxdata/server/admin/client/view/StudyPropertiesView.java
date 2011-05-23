package org.openxdata.server.admin.client.view;

import org.openxdata.server.admin.client.internationalization.OpenXdataConstants;
import org.openxdata.server.admin.client.view.widget.OpenXDataButton;
import org.openxdata.server.admin.client.view.widget.OpenXDataFlexTable;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.StudyDef;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import org.openxdata.server.admin.client.view.event.EditableEvent;

/**
 * Shows properties of study, form, or form version.
 * 
 * @author daniel
 * 
 */
public class StudyPropertiesView extends Composite  {
	
	private static OpenXdataConstants constants = GWT
	        .create(OpenXdataConstants.class);
	
	/** Widget for organising widgets in a tabular format. */
	private FlexTable table = new OpenXDataFlexTable();
	
	/**
	 * Widget used to specify whether the selected form version is the default
	 * in its parent form.
	 */
	private CheckBox chkDefault = new CheckBox();
	
	/** Widget for entering the name of the study, form, or form version. */
	private TextBox txtName = new TextBox();
	
	/** Widget for entering the description of the study, form, or form version. */
	private TextBox txtDescription = new TextBox();
	
	/** Widget for entering the key of the study, form, or form version. */
	private TextBox txtKey = new TextBox();
	
	/**
	 * The currently selected object. This could be a study, form, or form
	 * version.
	 */
	private Object propertiesObj;
	
        private final EventBus eventBus;
	
	/**
	 * Creates a new instance of the studies properties view.
	 * 
	 * @param itemChangeListener
	 *            the listener for <tt>Study, Form, Form version</tt> property
	 *            change events.
	 */
	public StudyPropertiesView(EventBus eventBus) {
		this.eventBus = eventBus;
		initWidgets();
	}
	
	/**
	 * Sets up the widgets.
	 */
	private void initWidgets() {
		
		table.setWidget(0, 0, new Label(constants.label_name()));
		table.setWidget(1, 0, new Label(constants.label_description()));
		table.setWidget(2, 0, new Label("Study Key"));
		table.setWidget(3, 0, new Label(constants.label_is_default()));
		
		table.setWidget(0, 1, txtName);
		table.setWidget(1, 1, txtDescription);
		table.setWidget(2, 1, txtKey);
		table.setWidget(3, 1, chkDefault);
		
		txtName.setWidth("100%");
		txtDescription.setWidth("100%");
		txtKey.setWidth("100%");
		
		new OpenXDataButton("Design Form Version");
		
		// table.setWidget(5, 0, btnDesignFormVersion);
		
		FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
		cellFormatter.setWidth(0, 0, "20%");
		cellFormatter.setColSpan(5, 0, 2);
		
		showExtra(false);
		
		initWidget(table);
		setupEventListeners();
		
		setEnabled(false);
	}
	
	/**
	 * Sets up event listeners.
	 */
	private void setupEventListeners() {
		
		txtName.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent arg0) {
				updateName();
			}
		});
		txtName.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				updateName();
				if (event.getCharCode() == KeyCodes.KEY_ENTER)
					txtDescription.setFocus(true);
			}
		});
		
		txtDescription.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent arg0) {
				updateDescription();
			}
		});
		txtDescription.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				updateDescription();
				if (event.getCharCode() == KeyCodes.KEY_ENTER)
					if (txtKey.isVisible())
						txtKey.setFocus(true);
			}
		});
		
		txtKey.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent arg0) {
				updateKey();
			}
		});
		txtKey.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				updateKey();
			}
		});
		
		chkDefault.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				((FormDefVersion) propertiesObj).setIsDefault(chkDefault
				        .getValue());
                                eventBus.fireEvent(new EditableEvent<Object>(propertiesObj));
						
				// Only one version can be active at a time for each form.
				if (chkDefault.getValue())
					((FormDefVersion) propertiesObj).getFormDef()
					        .turnOffOtherDefaults(
					                (FormDefVersion) propertiesObj);
			}
		});
	}
	
	/**
	 * Updates a study, form, or version with the new name as typed by the user.
	 * 
	 * @param sender
	 *            the widget having the new name.
	 */
	private void updateName() {
		if (propertiesObj instanceof StudyDef)
			((StudyDef) propertiesObj).setName(txtName.getText());
		else if (propertiesObj instanceof FormDef)
			((FormDef) propertiesObj).setName(txtName.getText());
		else if (propertiesObj instanceof FormDefVersion)
			((FormDefVersion) propertiesObj).setName(txtName.getText());
		
		eventBus.fireEvent(new EditableEvent<Object>(propertiesObj));
	}
	
	/**
	 * Updates a study, form, or version with the new description as typed by
	 * the user.
	 * 
	 * @param sender
	 *            the widget having the new description.
	 */
	private void updateDescription() {
		if (propertiesObj instanceof StudyDef)
			((StudyDef) propertiesObj).setDescription(txtDescription.getText());
		else if (propertiesObj instanceof FormDef)
			((FormDef) propertiesObj).setDescription(txtDescription.getText());
		else if (propertiesObj instanceof FormDefVersion)
			((FormDefVersion) propertiesObj).setDescription(txtDescription
			        .getText());
		
		eventBus.fireEvent(new EditableEvent<Object>(propertiesObj));
	}
	
	private void updateKey() {
		if (propertiesObj instanceof StudyDef)
			((StudyDef) propertiesObj).setStudyKey(txtKey.getText());
		
		eventBus.fireEvent(new EditableEvent<Object>(propertiesObj));
	}
	
	
	
	public void onItemSelected(Composite sender, Object item) {
		setEnabled(item != null);
		
		propertiesObj = item;
		
		if (item == null) {
			showExtra(false);
			clear();
		} else {
			if (item instanceof StudyDef) {
				StudyDef studyDef = (StudyDef) item;
				txtName.setText(studyDef.getName());
				txtDescription.setText(studyDef.getDescription());
				txtKey.setText(studyDef.getStudyKey());
				showExtra(false);
				FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
				cellFormatter.setVisible(2, 0, true);
				cellFormatter.setVisible(2, 1, true);
			} else if (item instanceof FormDef) {
				FormDef formDef = (FormDef) item;
				txtName.setText(formDef.getName());
				txtDescription.setText(formDef.getDescription());
				showExtra(false);
			} else if (item instanceof FormDefVersion) {
				FormDefVersion formDefVersion = (FormDefVersion) item;
				txtName.setText(formDefVersion.getName());
				txtDescription.setText(formDefVersion.getDescription());
				chkDefault.setValue(formDefVersion.getIsDefault());
				showExtra(true);
				FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
				cellFormatter.setVisible(2, 0, false);
				cellFormatter.setVisible(2, 1, false);
			}
		}
	}
	
	/**
	 * Sets input focus to the first widget.
	 */
	public void setFocus() {
		txtName.setFocus(true);
		txtName.selectAll();
	}
	
	/**
	 * Determines whether to display extra widgets for form version or not.
	 * 
	 * @param show
	 *            set to true to display these extra widgets, else set to false.
	 */
	private void showExtra(boolean show) {
		FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
		cellFormatter.setVisible(2, 0, show);
		cellFormatter.setVisible(2, 1, show);
		cellFormatter.setVisible(3, 0, show);
		cellFormatter.setVisible(3, 1, show);
		cellFormatter.setVisible(4, 0, show);
		cellFormatter.setVisible(5, 0, show);
	}
	
	/*
	 * public void setFormDefVersionXml(int formId, String xformsXml, String
	 * layoutXml) { if(propertiesObj == null || !(propertiesObj instanceof
	 * FormDefVersion)) return;
	 * 
	 * FormDefVersion formDefVersion = (FormDefVersion)propertiesObj;
	 * if(formDefVersion.getFormDefVersionId() != formId) return; }
	 */

	/**
	 * Enables or disables data entry widgets.
	 * 
	 * @param enabled
	 *            set to true to enable, else set to false to disable.
	 */
	private void setEnabled(boolean enabled) {
		txtName.setEnabled(enabled);
		chkDefault.setEnabled(enabled);
		txtDescription.setEnabled(enabled);
		txtKey.setEnabled(enabled);
	}
	
	/**
	 * Clears the contents of widgets.
	 */
	private void clear() {
		propertiesObj = null;
		txtName.setText(null);
		txtDescription.setText(null);
		txtKey.setText(null);
		chkDefault.setValue(false);
	}
}
