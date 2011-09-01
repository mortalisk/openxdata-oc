package org.openxdata.client.views;

import org.openxdata.client.AppMessages;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

abstract class ActionOptionView extends View {
	
	protected Radio firstRadio;
	protected Radio secondRadio;
	protected Radio thirdRadio;
	
	protected Button execButton;
	protected Button cancelButton;
		
	protected FormPanel formPanel;
	protected Window window = new Window();

	protected AppMessages appMessages = GWT.create(AppMessages.class);
	
	protected FormDef form;
	protected FormDefVersion formVersion;
	
	protected ActionOptionView(Controller controller){
		super(controller);
	}
	
	protected abstract void action();
	
	@Override
	protected void handleEvent(AppEvent event) {
        window.setAutoHeight(true);
        window.setWidth(425);
        window.setPlain(true);
        window.setHeading(getHeading());
        window.add(formPanel);
        window.setDraggable(true);
        window.setResizable(true);

        window.addButton(execButton);
        window.addButton(cancelButton);

        window.show();
        ProgressIndicator.hideProgressBar();
	}
	
	@Override
	protected void initialize() {
		createButtons();
		formPanel = new FormPanel();
		formPanel.setFrame(false);
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		formPanel.setHeaderVisible(false);
		
		final RadioGroup radioGroup = new RadioGroup("RadioGroup");
		radioGroup.setLabelSeparator("");
		radioGroup.setFieldLabel("");
		radioGroup.setOrientation(Orientation.VERTICAL);
		
		firstRadio = new Radio();
		firstRadio.setHideLabel(true);
		firstRadio.setBoxLabel(getFirstRadioLabel()+" ("+getStudyName()+")");
		radioGroup.add(firstRadio);
		firstRadio.addListener(Events.OnClick, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				onFirstRadioSelected();
			}
		});
		
		secondRadio = new Radio();
		secondRadio.setBoxLabel(getSecondRadioLabel()+" ("+getFormName()+")");
		secondRadio.setHideLabel(true);
		radioGroup.add(secondRadio);
		secondRadio.addListener(Events.OnClick, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				onSecondRadioSelected();
			}
		});
		
		thirdRadio = new Radio();
		thirdRadio.setHideLabel(true);
		thirdRadio.setBoxLabel(getThirdRadioLabel()+" ("+getFormVersionName()+")");
		radioGroup.add(thirdRadio);
		thirdRadio.addListener(Events.OnClick, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				onThirdRadioSelected();
			}
		});
		
		LayoutContainer radioGroupContainer = new LayoutContainer();
		FormLayout formLayout = new FormLayout();
		formLayout.setLabelSeparator("");
		formLayout.setLabelWidth(0);
		radioGroupContainer.setLayout(formLayout);
		radioGroupContainer.add(radioGroup);
		formPanel.add(radioGroupContainer);
		
		window.setModal(true);
	}
	
	protected void createButtons(){
		execButton = new Button(getExecuteButtonLabel());
		execButton.setEnabled(false);
		execButton.addListener(Events.Select, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					@Override
					public void execute() {
						action();
					}
				});
			}
		});
		
		cancelButton = new Button(appMessages.cancel());
		cancelButton.addListener(Events.Select, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						closeWindow();
					}
				});
			}
		});
	}
	
	protected void updateRadioButtons() {
		firstRadio.setBoxLabel(getFirstRadioLabel()+" ("+getStudyName()+")");
		secondRadio.setBoxLabel(getSecondRadioLabel()+" ("+getFormName()+")");
		thirdRadio.setBoxLabel(getThirdRadioLabel()+" ("+getFormVersionName()+")");
		if (formVersion == null) {
			thirdRadio.hide();
		}
	}
	
	protected String getFormVersionName() {
		if (formVersion != null) {
			FormDef form = formVersion.getFormDef();
			StringBuilder formVersionName = new StringBuilder();
			formVersionName.append(form.getStudy().getName());
			formVersionName.append(" ");
			formVersionName.append(form.getName());
			formVersionName.append(" ");
			formVersionName.append(formVersion.getName());
			return formVersionName.toString();
		}
		return "";
	}
	
	protected String getFormName() {
		if (form != null) {
			StringBuilder formName = new StringBuilder();
			formName.append(form.getStudy().getName());
			formName.append(" ");
			formName.append(form.getName());
			return formName.toString();
		}
		return "";
	}
	
	protected String getStudyName() {
		if (form != null) {
			StringBuilder studyName = new StringBuilder();
			studyName.append(form.getStudy().getName());
			return studyName.toString();
		}
		return "";
	}
	
	abstract String getHeading();
	
	abstract String getThirdRadioLabel();

	abstract String getSecondRadioLabel();

	abstract String getFirstRadioLabel();
	
	abstract String getExecuteButtonLabel();
	
	/**
	 * Called when the radio button is selected (implemented via Listener)
	 */
	protected void onFirstRadioSelected() {
		execButton.setEnabled(true);
	}
	
	/**
	 * Called when the radio button is selected (implemented via Listener)
	 */
	protected void onSecondRadioSelected() {
		execButton.setEnabled(true);
	}
	
	/**
	 * Called when the radio button is selected (implemented via Listener)
	 */
	protected void onThirdRadioSelected() {
		execButton.setEnabled(true);
	}

	/**
	 * Conceals the window from the User.
	 */
	public void closeWindow() {
		window.hide();
		ProgressIndicator.hideProgressBar();
		
	}
}
