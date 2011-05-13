package org.openxdata.client.controllers;

import org.openxdata.client.AppMessages;
import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.RefreshablePublisher;
import org.openxdata.client.service.UserServiceAsync;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.client.views.DataCaptureView;
import org.openxdata.server.admin.client.service.FormServiceAsync;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.User;


import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;

public class DataCaptureController extends Controller {
    AppMessages appMessages = GWT.create(AppMessages.class); 
    public final static EventType DATACAPTURE = new EventType();

    private UserServiceAsync userService;
    private FormServiceAsync formService;
    
    
    public DataCaptureController(UserServiceAsync aUserService, FormServiceAsync aFormService) {
        super();
        registerEventTypes(DATACAPTURE);
        userService = aUserService;
        formService = aFormService;
    }
    
    @Override
    protected void initialize() {
    	GWT.log("SurveyCaptureController : initialize");
    }

    @Override
    public void handleEvent(AppEvent event) {
    	GWT.log("DataCaptureController : handleEvent");
        EventType type = event.getType();
        if (type == DATACAPTURE) {
        	DataCaptureView dataCaptureView = new DataCaptureView(this);
            forwardToView(dataCaptureView, event);
        }
    }

    public void submit(final DataCaptureView view, final FormDef formDef, final FormData formData) {
    	GWT.log("DataCaptureController : onSubmit");
    	ProgressIndicator.showProgressBar();
        userService.getLoggedInUser(new EmitAsyncCallback<User>() {
            @Override
			public void onSuccess(User result) {
            	// set the creator or changedBy user (for tracking)
                if (formData.getCreator() == null) {
                	formData.setCreator(result);
               	} else {
               		formData.setChangedBy(result);
               	}
                // submit the data
                formService.saveFormData(formData, new EmitAsyncCallback<FormData>() {
                    @Override
					public void onSuccess(FormData result) {
                    	ProgressIndicator.hideProgressBar();
                    	MessageBox.alert(appMessages.success(), appMessages.dataSavedSucessfully(""+result.getFormDataId()), null);
                        RefreshablePublisher.get().publish(
                        		new RefreshableEvent(RefreshableEvent.Type.CAPTURE, result));
                        view.close(false);
                    }
                });
            }
        });
    }
}