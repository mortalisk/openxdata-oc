package org.openxdata.client.controllers;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.model.FormSummary;
import org.openxdata.client.util.PagingUtil;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.client.views.ItemAccessListField;
import org.openxdata.server.admin.client.service.FormServiceAsync;
import org.openxdata.server.admin.model.FormDefHeader;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.paging.PagingLoadResult;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Controller for mapping access of Forms to a User.
 */
public class FormUserAccessController implements ItemAccessController<FormSummary> {
	
	private User user;
	private FormServiceAsync formService;
	
	public FormUserAccessController(FormServiceAsync formService) {
		this.formService = formService;
	}
	
	public FormUserAccessController(FormServiceAsync formService, User user) {
		this.formService = formService;
		this.user = user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

	@Override
    public void getMappedData(
            PagingLoadConfig pagingLoadConfig,
            final AsyncCallback<com.extjs.gxt.ui.client.data.PagingLoadResult<FormSummary>> callback) {
		formService.getMappedFormNames(user.getId(), PagingUtil.createPagingLoadConfig(pagingLoadConfig), 
				new EmitAsyncCallback<PagingLoadResult<FormDefHeader>>() {
            @Override
            public void onSuccess(PagingLoadResult<FormDefHeader> result) {
                ProgressIndicator.hideProgressBar();
            	callback.onSuccess(new BasePagingLoadResult<FormSummary>(convertFormResults(result), 
            			result.getOffset(), result.getTotalLength()));
            }
        });
    }

	@Override
    public void getUnMappedData(
            PagingLoadConfig pagingLoadConfig,
            final AsyncCallback<com.extjs.gxt.ui.client.data.PagingLoadResult<FormSummary>> callback) {
		formService.getUnmappedFormNames(user.getId(), PagingUtil.createPagingLoadConfig(pagingLoadConfig), 
				new EmitAsyncCallback<PagingLoadResult<FormDefHeader>>() {
            @Override
            public void onSuccess(PagingLoadResult<FormDefHeader> result) {
                ProgressIndicator.hideProgressBar();
            	callback.onSuccess(new BasePagingLoadResult<FormSummary>(convertFormResults(result), 
            			result.getOffset(), result.getTotalLength()));
            }
        });
    }
	
	private List<FormSummary> convertFormResults(PagingLoadResult<FormDefHeader> result) {
        List<FormSummary> results = new ArrayList<FormSummary>();
    	List<FormDefHeader> forms = result.getData();
        for (FormDefHeader fd : forms) {
        	results.add(new FormSummary(fd));
        }
        return results;
    }

	@Override
    public void addMapping(List<FormSummary> formsToAdd, final ItemAccessListField<FormSummary> userAccessListField) {
	    formService.saveMappedUserFormNames(user.getId(), convertFormList(formsToAdd), null, new EmitAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
               userAccessListField.refresh();
            }
        });
    }

	@Override
    public void deleteMapping(List<FormSummary> formsToDelete, final ItemAccessListField<FormSummary> userAccessListField) {
		formService.saveMappedUserFormNames(user.getId(), null, convertFormList(formsToDelete), new EmitAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
               userAccessListField.refresh();
            }
        });
    }
	
	private List<FormDefHeader> convertFormList(List<FormSummary> formList) {
		List<FormDefHeader> forms = new ArrayList<FormDefHeader>();
		for (FormSummary fd : formList) {
			forms.add(fd.getFormDefHeader());
		}
		return forms;
	}
}
