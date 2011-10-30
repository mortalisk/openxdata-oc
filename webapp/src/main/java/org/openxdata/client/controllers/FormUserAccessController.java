package org.openxdata.client.controllers;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.model.FormSummary;
import org.openxdata.client.util.PagingUtil;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.client.views.ItemAccessListField;
import org.openxdata.server.admin.client.service.FormServiceAsync;
import org.openxdata.server.admin.model.FormDef;
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
		formService.getMappedForms(user.getId(), PagingUtil.createPagingLoadConfig(pagingLoadConfig), 
				new EmitAsyncCallback<PagingLoadResult<FormDef>>() {
            @Override
            public void onSuccess(PagingLoadResult<FormDef> result) {
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
		formService.getUnmappedForms(user.getId(), PagingUtil.createPagingLoadConfig(pagingLoadConfig), 
				new EmitAsyncCallback<PagingLoadResult<FormDef>>() {
            @Override
            public void onSuccess(PagingLoadResult<FormDef> result) {
                ProgressIndicator.hideProgressBar();
            	callback.onSuccess(new BasePagingLoadResult<FormSummary>(convertFormResults(result), 
            			result.getOffset(), result.getTotalLength()));
            }
        });
    }
	
	private List<FormSummary> convertFormResults(PagingLoadResult<FormDef> result) {
        List<FormSummary> results = new ArrayList<FormSummary>();
    	List<FormDef> forms = result.getData();
        for (FormDef fd : forms) {
        	results.add(new FormSummary(fd));
        }
        return results;
    }

	@Override
    public void addMapping(List<FormSummary> formsToAdd, final ItemAccessListField<FormSummary> userAccessListField) {
	    formService.saveMappedUserForms(user.getId(), convertFormList(formsToAdd), null, new EmitAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
               userAccessListField.refresh();
            }
        });
    }

	@Override
    public void deleteMapping(List<FormSummary> formsToDelete, final ItemAccessListField<FormSummary> userAccessListField) {
		formService.saveMappedUserForms(user.getId(), null, convertFormList(formsToDelete), new EmitAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
               userAccessListField.refresh();
            }
        });
    }
	
	private List<FormDef> convertFormList(List<FormSummary> formList) {
		List<FormDef> forms = new ArrayList<FormDef>();
		for (FormSummary fd : formList) {
			forms.add(fd.getFormDefinition());
		}
		return forms;
	}
}
