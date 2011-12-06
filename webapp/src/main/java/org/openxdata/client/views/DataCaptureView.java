package org.openxdata.client.views;

import java.util.Date;

import org.openxdata.client.AppMessages;
import org.openxdata.client.controllers.DataCaptureController;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDefVersion;
import org.purc.purcforms.client.FormRunnerEntryPoint;
import org.purc.purcforms.client.controller.SubmitListener;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.view.FormRunnerView.Images;
import org.purc.purcforms.client.widget.FormRunnerWidget;

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

public class DataCaptureView extends View implements SubmitListener {
    final AppMessages appMessages = GWT.create(AppMessages.class);    
    
    private FormDefVersion formVersion;
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
    			MessageBox.confirm(appMessages.cancel(), LocaleText.get("cancelFormPrompt"), new Listener<MessageBoxEvent>() {
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
                	 formVersion = event.getData("formVersion");
                	 formData = event.getData("formData");
                     window.setHeading(appMessages.captureData() + " : " +formVersion.getFormDef().getName());
		             if (formVersion != null && formVersion.getXform() != null) {
		            	 
		            	 window.show(); 
		            	 window.maximize();
		            	 
		            	 if (formData == null) {
		            		 widget.loadForm(formVersion.getId(), formVersion.getXform(), null, formVersion.getLayout(), formVersion.getJavaScriptSrc());
		            	 } else {
		            		 widget.loadForm(formVersion.getId(), formVersion.getXform(), formData.getData(), formVersion.getLayout(), formVersion.getJavaScriptSrc());
		            	 }
		            	 
		            	// this layout function needs to be called, so the java script operations work 
		            	window.layout(); 
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
    		formData.setFormDefVersionId(formVersion.getId());
    		formData.setDateCreated(new Date());
    	} else {
    		formData.setDateChanged(new Date());
    	}
    	formData.setData(xml);
    	Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
			public void execute() {
            	((DataCaptureController)DataCaptureView.this.getController()).submit(DataCaptureView.this, formVersion, formData);
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
