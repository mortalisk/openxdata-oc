package org.openxdata.client.views;

import org.openxdata.client.AppMessages;
import org.openxdata.client.util.ProgressIndicator;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 * 
 *
 */
abstract class ActionOptionView extends View {
	
	protected Radio firstRadio;
	protected Radio secondRadio;
	protected Radio thirdRadio;
	
	protected Button execButton;
	protected Button cancelButton;
		
	protected FormPanel formPanel;
	protected Window window = new Window();

	protected AppMessages appMessages = GWT.create(AppMessages.class);
	
	protected ActionOptionView(Controller controller){
		super(controller);
	}
	
	protected abstract void action();
	
	@Override
	protected void initialize() {
		
		formPanel = new FormPanel();
		formPanel.setFrame(false);
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		formPanel.setHeaderVisible(false);
		FormLayout layout = new FormLayout();
		layout.setLabelWidth(0);
		formPanel.setLayout(layout);
		
		final RadioGroup radioGroup = new RadioGroup("RadioGroup");
		radioGroup.setLabelSeparator("");
		radioGroup.setFieldLabel("");
		radioGroup.setOrientation(Orientation.VERTICAL);
		
		firstRadio = new Radio();
		firstRadio.setHideLabel(true);
		firstRadio.setBoxLabel(getFirstRadioLabel());
		radioGroup.add(firstRadio);
		
		secondRadio = new Radio();
		secondRadio.setBoxLabel(getSecondRadioLabel());
		secondRadio.setHideLabel(true);
		radioGroup.add(secondRadio);
		
		thirdRadio = new Radio();
		thirdRadio.setHideLabel(true);
		thirdRadio.setBoxLabel(getThirdRadioLabel());
		radioGroup.add(thirdRadio);
		
		formPanel.add(radioGroup);
		
		execButton = new Button(appMessages.delete());
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
						cancel();
					}
				});
			}
		});
		
		window.addListener(Events.BeforeHide, windowListener);
		window.setModal(true);
	}
	
	abstract String getThirdRadioLabel();

	abstract String getSecondRadioLabel();

	abstract String getFirstRadioLabel();

	final Listener<ComponentEvent> windowListener = new WindowListener();
	
	class WindowListener implements Listener<ComponentEvent> {
		@Override
		public void handleEvent(ComponentEvent be) {
			be.setCancelled(true);
			be.stopEvent();
			MessageBox.confirm(appMessages.cancel(), appMessages.areYouSure(),
			        new Listener<MessageBoxEvent>() {
				        @Override
				        public void handleEvent(MessageBoxEvent be) {
					        if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
						        window.removeListener(Events.BeforeHide,
						                windowListener);
						        closeWindow();
						        window.addListener(Events.BeforeHide,
						                windowListener);
					        }
				        }
			        });
		}
	};
	
	public void cancel() {
		MessageBox.confirm(appMessages.cancel(), appMessages.areYouSure(),
		        new Listener<MessageBoxEvent>() {
			        @Override
			        public void handleEvent(MessageBoxEvent be) {
				        if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
					        closeWindow();
				        }
			        }
		        });
	}
	
	/**
	 * Conceals the window from the User.
	 */
	public void closeWindow() {
		window.removeListener(Events.BeforeHide, windowListener);
		window.hide();
		ProgressIndicator.hideProgressBar();
		window.addListener(Events.BeforeHide, windowListener);
		
	}
}
