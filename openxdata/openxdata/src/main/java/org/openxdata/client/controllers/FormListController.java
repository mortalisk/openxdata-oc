package org.openxdata.client.controllers;

import java.util.List;

import org.openxdata.client.AppMessages;
import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.RefreshablePublisher;
import org.openxdata.client.model.FormSummary;
import org.openxdata.client.views.FormListView;
import org.openxdata.server.admin.client.service.FormServiceAsync;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;


import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.GWT;

public class FormListController extends Controller {
    AppMessages appMessages = GWT.create(AppMessages.class); 
    public final static EventType FORMLIST = new EventType();

    private FormListView formListView;
    private FormServiceAsync formService;
    
    public FormListController(FormServiceAsync aFormService) {
        super();
        formService = aFormService;
        registerEventTypes(FORMLIST);
    }
    
    @Override
    protected void initialize() {
        GWT.log("FormListController : initialize");
        formListView = new FormListView(this);
        RefreshablePublisher.get().subscribe(RefreshableEvent.Type.CAPTURE, formListView);
        RefreshablePublisher.get().subscribe(RefreshableEvent.Type.UPDATE_STUDY, formListView);
        RefreshablePublisher.get().subscribe(RefreshableEvent.Type.CREATE_STUDY, formListView);
        RefreshablePublisher.get().subscribe(RefreshableEvent.Type.DELETE, formListView);
    }

    @Override
    public void handleEvent(AppEvent event) {
    	GWT.log("FormListController : handleEvent");
        EventType type = event.getType();
        if (type == FORMLIST) {
            forwardToView(formListView, event);
        }
    }
    
    public void forwardToDataCapture(FormDef formDef){
    	GWT.log("FormListController : forwardToDataCapture");
        Dispatcher dispatcher = Dispatcher.get();
        AppEvent event = new AppEvent(DataCaptureController.DATACAPTURE);
        event.setData("formDef", formDef);
    	dispatcher.dispatch(event);
    }
        
    public void forwardToFormPrint(String id){
    	GWT.log("FormListController : forwardToFormPrint");
    	Dispatcher dispatcher = Dispatcher.get();
    	dispatcher.dispatch(FormPrintController.FORMPRINTVIEW);
    }     
        
    public void forwardToFormResponses(FormSummary formSummary){
    	GWT.log("FormListController : forwardToFormResponses");
    	Dispatcher dispatcher = Dispatcher.get();
    	dispatcher.dispatch(FormResponsesController.BROWSE, formSummary);
    }
    
    public void forwardToNewStudyFormWizard() {
    	GWT.log("FormListController : forwardToNewStudyFormWizard");
        Dispatcher dispatcher = Dispatcher.get();
        AppEvent event = new AppEvent(NewStudyFormController.NEWSTUDYFORM);
    	dispatcher.dispatch(event);
    }
    
    public void forwardToEditStudyFormController(FormDef formDef){
    	
       	GWT.log("FormListController : forwardToEditStudyFormController");
        Dispatcher dispatcher = Dispatcher.get();
        AppEvent event = new AppEvent(EditStudyFormController.EDITSTUDYFORM);
        event.setData("formDef", formDef);
        
    	dispatcher.dispatch(event);
    }
    
    public void forwardToDeleteStudyFormController(FormDef form){
    	GWT.log("FormListController : forwardToDeleteStudyFormController");
        Dispatcher dispatcher = Dispatcher.get();
        AppEvent event = new AppEvent(DeleteStudyFormController.DELETESTUDYFORM);
        event.setData(form);
    	dispatcher.dispatch(event);
    }

    public void forwardToFormVersionController(FormDef form){
    	GWT.log("FormListController : forwardToFormVersionController");
        Dispatcher dispatcher = Dispatcher.get();
        AppEvent event = new AppEvent(FormVersionsController.FORMVERSIONLIST);
        event.setData(form);
    	dispatcher.dispatch(event);
    }
    
    public void getForms() {
    	GWT.log("FormListController : getForms");
        formService.getFormsForCurrentUser(new EmitAsyncCallback<List<FormDef>>() {

            @Override
            public void onSuccess(List<FormDef> result) {
                formListView.setFormData(result);
            }
        });
    }
    
    public void hasFormData(final FormDefVersion formDefVersion) {
    	GWT.log("FormListController : hasFormData");
        formService.getFormResponseCount(formDefVersion.getFormDefVersionId(), new EmitAsyncCallback<Integer>() {
            @Override
			public void onSuccess(Integer result) {
                if (result > 0) {
                    formListView.setFormStatus(formDefVersion.getFormDef(), true);
                } else {
                    formListView.setFormStatus(formDefVersion.getFormDef(), false);
                }
                formListView.setNumberOfFormResponses(formDefVersion.getFormDef(), result);
            }
        });
    }
}