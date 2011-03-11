package org.openxdata.client.views;

import java.util.Date;

import org.openxdata.client.AppMessages;
import org.openxdata.client.controllers.DataCaptureController;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.runner.client.FormRunnerEntryPoint;
import org.openxdata.sharedlib.client.controller.SubmitListener;
import org.openxdata.sharedlib.client.util.FormUtil;
import org.openxdata.runner.client.widget.FormRunnerWidget;

import com.extjs.gxt.ui.client.Style.Scroll;
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
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import org.openxdata.sharedlib.client.locale.FormsConstants;
import org.openxdata.sharedlib.client.view.FormRunnerView.Images;

public class DataCaptureView extends View implements SubmitListener {
    final AppMessages appMessages = GWT.create(AppMessages.class);    
    final FormsConstants formsConstants = GWT.create(FormsConstants.class);
    
    private FormDef formDef;
    private FormData formData;
	private FormRunnerWidget widget;
	private boolean displayCloseWarning = true;
	private final Window window = new Window();

    public DataCaptureView(Controller controller) {
        super(controller);
    }
    
    @Override
    protected void initialize() {
        GWT.log("DataCaptureView : initialize");
        FormUtil.retrieveUserDivParameters();
        FormRunnerEntryPoint.registerAuthenticationCallback();
        
        widget = new FormRunnerWidget((Images)GWT.create(Images.class));
        widget.setSubmitListener(this);

	    window.setPlain(true);  
	    window.setHeading(appMessages.dataCapture());    
	    window.setMaximizable(true);
        window.setMinimizable(false);
        window.setDraggable(false);
	    window.setResizable(false);
	    window.setModal(true);
	    window.setSize(com.google.gwt.user.client.Window.getClientWidth(), com.google.gwt.user.client.Window.getClientHeight());
        window.add(widget);
	    // FIXME: note there are some issues with the purcform widget if you allow the window to be resized (i.e. more than one open at a time)
	    window.setScrollMode(Scroll.AUTO);
	    window.addListener(Events.BeforeHide, windowListener);
        window.setModal(true);
    }
    
    final Listener<ComponentEvent> windowListener = new WindowListener();
    class WindowListener implements Listener<ComponentEvent> {
    	@Override
		public void handleEvent(ComponentEvent be) {
    		if (displayCloseWarning) {
    			be.setCancelled(true);
    			be.stopEvent();
    			MessageBox.confirm(appMessages.cancel(), formsConstants.cancelFormPrompt(), new Listener<MessageBoxEvent>() {
	    			@Override
	    			public void handleEvent(MessageBoxEvent be) {
	    				if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
	    					window.removeListener(Events.BeforeHide, windowListener);
	    					window.hide(); 
	    					window.addListener(Events.BeforeHide, windowListener);
	    				}
	    			}
	        	});
    		}
    	}
    };
    
    @Override
    protected void handleEvent(final AppEvent event) {
        if (event.getType() == DataCaptureController.DATACAPTURE) {
        	 GWT.log("DataCaptureView : handleEvent - DataCaptureController.DATACAPTURE");
        	 Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                 @Override
				public void execute() {
                	 ProgressIndicator.showProgressBar();
                	 formDef = event.getData("formDef");
                	 formData = event.getData("formData");
                     window.setHeading(appMessages.captureData() + " : " +formDef.getName());
                     FormDefVersion formDefVersion = formDef.getDefaultVersion();
		             if (formDefVersion != null && formDefVersion.getXform() != null) {
		            	 if (formData == null) {
		            		 //widget.loadForm(formDefVersion.getXform(), formDefVersion.getLayout(), "");
		            		 widget.loadForm(formDefVersion.getFormDefVersionId(),formDefVersion.getXform(),null,formDefVersion.getLayout(),null);
		            	 } else {
		            		 //widget.loadForm(0, formDefVersion.getXform(), formData.getData(), formDefVersion.getLayout(), "");
		            		 widget.loadForm(formData.getFormDataId(),formDefVersion.getXform(),formData.getData(),formDefVersion.getLayout(),null);
		            	 }
		                  window.show();
		          	      window.maximize();
		          	      ProgressIndicator.hideProgressBar();
		             } else {
		            	 ProgressIndicator.hideProgressBar();
		            	 MessageBox.alert(appMessages.error(), appMessages.errorWhileRetrievingForms(), null);
		             }
                 }
             });
         }
    }
    
    @Override
	public void onCancel() {
    	GWT.log("DataCaptureView : cancelled");
    	close(false);
    }

    @Override
	public void onSubmit(String xml) {
    	GWT.log("DataCaptureView : submitted");
    	if (formData == null) {
    		formData = new FormData();
    		formData.setFormDefVersionId(formDef.getDefaultVersion().getFormDefVersionId());
    		//formData.setDescription(Utilities.getDescriptionTemplate(xformXml,xml)); // FIXME: figure out what to do about the description
    		formData.setDateCreated(new Date());
    	} else {
    		formData.setDateChanged(new Date());
    	}
    	formData.setData(xml);
    	Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
			public void execute() {
            	((DataCaptureController)DataCaptureView.this.getController()).submit(DataCaptureView.this, formDef, formData);
            }
    	});
    }
    
    /**
     * Closes the window
     */
    public void close(boolean displayWarning) {
    	displayCloseWarning = displayWarning;
    	window.hide();
    }
}
