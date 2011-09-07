package org.openxdata.client.views;

import org.openxdata.client.AppMessages;
import org.openxdata.client.controllers.EditStudyFormController;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

public class StudyFormHasDataChoiceView extends Dialog {

	AppMessages messages = GWT.create(AppMessages.class);

	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);

		this.setHeading(messages.existingDataTitle());
		this.addText(messages.existingDataMessage());
		this.setHideOnButtonClick(true);
		
	}
	
	@Override
	protected void createButtons() {
		
		// Put here because this method is called before class initialization.
		AppMessages messages = GWT.create(AppMessages.class);
		getButtonBar().add(new FillToolItem());
		final Dispatcher dispatcher = Dispatcher.get();

		Button createNewVersion = new Button(messages.createNewVersion());
		createNewVersion.addSelectionListener(new SelectionListener<ButtonEvent>() {
					public void componentSelected(ButtonEvent ce) {
						AppEvent event = new AppEvent(EditStudyFormController.CREATENEWVERSION);
						dispatcher.dispatch(event);
						StudyFormHasDataChoiceView.this.hide();
					}
				});

		Button openReadOnly = new Button(messages.openReadOnly());
		openReadOnly.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				AppEvent event = new AppEvent(EditStudyFormController.OPENREADONLY);
				dispatcher.dispatch(event);
				StudyFormHasDataChoiceView.this.hide();
			}
		});
		
		getButtonBar().add(openReadOnly);
		getButtonBar().add(createNewVersion);
	}
}
