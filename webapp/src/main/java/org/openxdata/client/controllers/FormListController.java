package org.openxdata.client.controllers;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.client.AppMessages;
import org.openxdata.client.Emit;
import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.RefreshablePublisher;
import org.openxdata.client.model.FormSummary;
import org.openxdata.client.util.PagingUtil;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.client.views.FormListView;
import org.openxdata.server.admin.client.service.FormServiceAsync;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataParsingException;
import org.openxdata.server.admin.model.paging.PagingLoadResult;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.FilterPagingLoadConfig;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

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
    
    public void forwardToDataCapture(FormDefVersion formVersion){
    	GWT.log("FormListController : forwardToDataCapture");
        Dispatcher dispatcher = Dispatcher.get();
        AppEvent event = new AppEvent(DataCaptureController.DATACAPTURE);
        event.setData("formVersion", formVersion);
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
    
    public void forwardToNewStudyFormWizard(FormSummary formSummary) {
    	GWT.log("FormListController : forwardToNewStudyFormWizard");
        Dispatcher dispatcher = Dispatcher.get();
        AppEvent event = new AppEvent(NewStudyFormController.NEWSTUDYFORM);
        if (formSummary != null) {
	        event.setData("formDef", formSummary.getFormDefinition());
        }
    	dispatcher.dispatch(event);
    }
    
    public void forwardToEditStudyFormController(FormSummary formSummary){
    	
       	GWT.log("FormListController : forwardToEditStudyFormController");
        Dispatcher dispatcher = Dispatcher.get();
        AppEvent event = new AppEvent(EditStudyFormController.EDITSTUDYFORM);
        event.setData("formVersion", formSummary.getFormVersion());
        
    	dispatcher.dispatch(event);
    }
    
    public void forwardToDeleteStudyFormController(FormSummary formSummary){
    	GWT.log("FormListController : forwardToDeleteStudyFormController");
        Dispatcher dispatcher = Dispatcher.get();
        AppEvent event = new AppEvent(DeleteStudyFormController.DELETESTUDYFORM);
        event.setData("formDef", formSummary.getFormDefinition());
        event.setData("formVersion", formSummary.getFormVersion());
    	dispatcher.dispatch(event);
    }
    
    public void getFormVersions(FilterPagingLoadConfig loadConfig,
    		final AsyncCallback<com.extjs.gxt.ui.client.data.PagingLoadResult<FormSummary>> callback) throws OpenXDataParsingException {
    	GWT.log("FormListController : getForms");
    	User user = Registry.get(Emit.LOGGED_IN_USER_NAME);    	
        formService.getFormVersions(user, PagingUtil.createPagingLoadConfig(loadConfig), new EmitAsyncCallback<PagingLoadResult<FormDefVersion>>() {
            @Override
            public void onSuccess(PagingLoadResult<FormDefVersion> result) {
            	ProgressIndicator.hideProgressBar();
            	List<FormSummary> results = new ArrayList<FormSummary>();
                List<FormDefVersion> data = result.getData();
                formListView.setAllFormSummaries(results); // reset the list
                for (FormDefVersion f : data) {
                    results.add(formListView.createFormSummary(f));
                }
                formListView.setAllFormSummaries(results); // set the list
                callback.onSuccess(new BasePagingLoadResult<FormSummary>(results, result.getOffset(), result.getTotalLength()));
            }
        });
    }
    
    public void hasFormData(final FormDefVersion formDefVersion) {
    	GWT.log("FormListController : hasFormData");
        formService.getFormResponseCount(formDefVersion.getId(), new EmitAsyncCallback<Integer>() {
            @Override
			public void onSuccess(Integer result) {
                if (result > 0) {
                    formListView.setFormStatus(formDefVersion.getFormDef(), true);
                } else {
                    formListView.setFormStatus(formDefVersion.getFormDef(), false);
                }
                formListView.setNumberOfFormResponses(formDefVersion, result);
            }
        });
    }

	public void forwardToItemExportController(FormSummary formSummary) {
		GWT.log("FormListController : forwardToItemExportController");
        Dispatcher dispatcher = Dispatcher.get();
        AppEvent event = new AppEvent(ItemExportController.EXPORTITEM);
        event.setData("formDef", formSummary.getFormDefinition());
        event.setData("formVersion", formSummary.getFormVersion());
    	dispatcher.dispatch(event);
	}

	public void forwardToItemImportController(Editable editable) {
		GWT.log("FormListController : forwardToItemImportController");
        Dispatcher dispatcher = Dispatcher.get();
        AppEvent event = new AppEvent(ItemImportController.IMPORTITEM);
        event.setData("editable", editable);
    	dispatcher.dispatch(event);
	}
}