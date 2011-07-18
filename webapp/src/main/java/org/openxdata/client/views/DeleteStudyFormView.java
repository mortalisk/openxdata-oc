package org.openxdata.client.views;

import org.openxdata.client.AppMessages;
import org.openxdata.client.controllers.DeleteStudyFormController;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
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
 * Encapsulates UI functionality for Editing a given Study/Form/Form version.
 */
public class DeleteStudyFormView extends View {
	
	final AppMessages appMessages = GWT.create(AppMessages.class);
	
	private FormPanel formPanel;
	private Window window = new Window();
	
	private Radio deleteStudy;
	private Radio deleteForm;
	private Radio deleteFormVersion;
	
	private Button deleteButton;
	private Button cancelButton;
	
	private FormDef form;
	private FormDefVersion formVersion;
    
	public DeleteStudyFormView(Controller controller) {
		super(controller);
	}
	
	@Override
	protected void initialize() {
		GWT.log("DeleteStudyFormController : initialize");
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
		
		deleteStudy = new Radio();
		deleteStudy.setHideLabel(true);
		deleteStudy.setBoxLabel("Delete Study");
		radioGroup.add(deleteStudy);
		
		deleteForm = new Radio();
		deleteForm.setBoxLabel("Delete Form");
		deleteForm.setHideLabel(true);
		radioGroup.add(deleteForm);
		
		deleteFormVersion = new Radio();
		deleteFormVersion.setHideLabel(true);
		deleteFormVersion.setBoxLabel("Delete Form Version");
		radioGroup.add(deleteFormVersion);
		
		formPanel.add(radioGroup);
		
		deleteButton = new Button(appMessages.delete());
		deleteButton.addListener(Events.Select, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					@Override
					public void execute() {
						if (deleteStudy.getValue()) {
							checkItemHasData(form.getStudy());
						} else if (deleteForm.getValue()) {
							checkItemHasData(form);
						} else if (deleteFormVersion.getValue()) {
							checkItemHasData(formVersion);
						}
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
	
	private void delete() {
            final DeleteStudyFormController controller1 = (DeleteStudyFormController) this.getController();
            
            MessageBox.confirm(appMessages.delete(), appMessages.areYouSureDelete(), new Listener<MessageBoxEvent>() {

                @Override
                public void handleEvent(MessageBoxEvent be) {
                    if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                        ProgressIndicator.showProgressBar();
                        if (deleteStudy.getValue()) {
                            controller1.delete(form.getStudy());
                        } else if (deleteForm.getValue()) {
                            controller1.delete(form);
                        } else if (deleteFormVersion.getValue()) {
                            controller1.delete(formVersion);
                        }
                    }
                }
            });

	}
	
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
	 * Closes the Window and conceals it from the User.
	 */
	public void closeWindow() {
		window.removeListener(Events.BeforeHide, windowListener);
		window.hide();
		ProgressIndicator.hideProgressBar();
		window.addListener(Events.BeforeHide, windowListener);
		
	}
	
	   @Override
    protected void handleEvent(AppEvent event) {
        GWT.log("DeleteStudyFormView : handleEvent");
        if (event.getType() == DeleteStudyFormController.DELETESTUDYFORM) {
            GWT.log("DeleteStudyFormView : DeleteStudyFormController.DELETESTUDYFORM: Delete");

            formVersion = event.getData("formVersion");
            form = event.getData("formDef");

            // Initialize Window
            deleteStudy.setBoxLabel(deleteStudy.getBoxLabel()+" - "+form.getStudy().getName());
            deleteForm.setBoxLabel(deleteForm.getBoxLabel()+" - "+form.getName());
            if (formVersion != null) {
            	deleteFormVersion.setBoxLabel(deleteFormVersion.getBoxLabel()+" - "+formVersion.getName());
            	deleteFormVersion.show();
            } else {
            	deleteFormVersion.hide();
            }
            window.setAutoHeight(true);
            window.setWidth(425);
            window.setPlain(true);
            window.setHeading(appMessages.deleteStudyOrForm());
            window.add(formPanel);
            window.setDraggable(true);
            window.setResizable(true);

            window.addButton(deleteButton);
            window.addButton(cancelButton);

            window.show();
            ProgressIndicator.hideProgressBar();
        }

    }
	   
    public void checkItemHasData(Editable item){
        ((DeleteStudyFormController) this.getController()).itemHasData(item);
    }
    
    public void onItemDataCheckComplete(Boolean hasData) {
        if (!hasData) {
            delete();
        } else {
            MessageBox.alert(appMessages.viewResponses(),appMessages.unableToDeleteFormWithData(), null);
        }
    }
}