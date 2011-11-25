package org.openxdata.client.controllers;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.client.AppMessages;
import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.RefreshablePublisher;
import org.openxdata.client.model.FormDataSummary;
import org.openxdata.client.util.PagingUtil;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.client.views.UnprocessedDataView;
import org.openxdata.server.admin.client.service.FormServiceAsync;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDataHeader;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.paging.PagingLoadResult;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class UnprocessedDataController  extends Controller {
	AppMessages appMessages = GWT.create(AppMessages.class); 
	public final static EventType UNPROCESSED_DATA = new EventType();

	private UnprocessedDataView unprocessedDataView;
	private FormServiceAsync formService;
	
	public UnprocessedDataController(FormServiceAsync aFormService) {
		super();
		formService = aFormService;
		registerEventTypes(UNPROCESSED_DATA);
	}
	
	@Override
	protected void initialize() {
		GWT.log("UnprocessedDataController : initialize");
		unprocessedDataView = new UnprocessedDataView(this);
		RefreshablePublisher.get().subscribe(RefreshableEvent.Type.REFRESH_UNEXPORTED_DATA, unprocessedDataView);
	}

	@Override
	public void handleEvent(AppEvent event) {
		GWT.log("UnprocessedDataController : handleEvent");
		EventType type = event.getType();
		if (type == UNPROCESSED_DATA) {
			forwardToView(unprocessedDataView, event);
		}
	}
	
	public void getFormData(final PagingLoadConfig pagingLoadConfig, 
			final AsyncCallback<com.extjs.gxt.ui.client.data.PagingLoadResult<FormDataSummary>> callback) {
		formService.getUnexportedFormData(PagingUtil.createPagingLoadConfig(pagingLoadConfig), 
				new EmitAsyncCallback<PagingLoadResult<FormDataHeader>>() {
			@Override
			public void onSuccess(PagingLoadResult<FormDataHeader> result) {
				List<FormDataSummary> results = new ArrayList<FormDataSummary>();
				List<FormDataHeader> data = result.getData();
				for (FormDataHeader fdh : data) {
					results.add(new FormDataSummary(fdh));
				}
				ProgressIndicator.hideProgressBar();
				callback.onSuccess(new BasePagingLoadResult<FormDataSummary>(results, result.getOffset(), result.getTotalLength()));
			}
		});
	}
	
	public void forwardToDataCapture(FormDataSummary formDataSummary) {
		GWT.log("UnprocessedDataController : forwardToDataCapture");
		final FormDataHeader formDataHeader = formDataSummary.getFormDataHeader();
		final AppEvent event = new AppEvent(DataCaptureController.DATACAPTURE);
		formService.getFormVersion(formDataHeader.getFormDefVersionId(), new EmitAsyncCallback<FormDefVersion>() {
			@Override
			public void onSuccess(FormDefVersion result) {
				event.setData("formVersion", result);
				formService.getFormData(formDataHeader.getId(), new EmitAsyncCallback<FormData>() {
					@Override
					public void onSuccess(FormData result) {
						event.setData("formData", result);
						Dispatcher.get().dispatch(event);
					}
				});
			}
		});
	}
	
	public void deleteFormData(final List<FormDataSummary> formDataList) {
		formService.deleteFormData(convertFormDataSummaryList(formDataList), new EmitAsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				GWT.log("Successful delete of form data");
				ProgressIndicator.hideProgressBar();
				RefreshablePublisher.get().publish(new RefreshableEvent(RefreshableEvent.Type.REFRESH_UNEXPORTED_DATA));
			}
		});
	}
	
	public void reprocessFormData(List<FormDataSummary> formDataList) {
		formService.exportFormData(convertFormDataSummaryList(formDataList), new EmitAsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				GWT.log("Successful reprocessing of form data");
				ProgressIndicator.hideProgressBar(); 
				MessageBox.info(appMessages.manageUnprocessedData(), appMessages.reprocessMessage(), new Listener<MessageBoxEvent>() {
					@Override
					public void handleEvent(MessageBoxEvent be) {
						if (be.getButtonClicked().getItemId().equals(Dialog.OK)){
							RefreshablePublisher.get().publish(new RefreshableEvent(RefreshableEvent.Type.REFRESH_UNEXPORTED_DATA));
						}
					}
				});
			}
		});
	}
	
	private List<Integer> convertFormDataSummaryList(List<FormDataSummary> summaryList) {
		List<Integer> formDataList = new ArrayList<Integer>();
		if (summaryList != null) {
			for (FormDataSummary fds : summaryList) {
				formDataList.add(fds.getFormDataHeader().getId());
			}
		}
		return formDataList;
	}
 }
