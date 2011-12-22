package org.openxdata.client.controllers;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.model.UserSummary;
import org.openxdata.client.util.PagingUtil;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.client.views.ItemAccessListField;
import org.openxdata.server.admin.client.service.UserServiceAsync;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.UserHeader;
import org.openxdata.server.admin.model.paging.PagingLoadResult;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Controller for mapping access of Forms to Users.
 */
public class UserFormAccessController implements ItemAccessController<UserSummary> {
	
	private FormDef form;
	private UserServiceAsync userService;
	
	public UserFormAccessController(UserServiceAsync userService) {
		this.userService = userService;
	}
	
	public UserFormAccessController(UserServiceAsync userService, FormDef form) {
		this.userService = userService;
		this.form = form;
	}
	
	public void setForm(FormDef form) {
		this.form = form;
	}

	@Override
    public void getMappedData(
            PagingLoadConfig pagingLoadConfig,
            final AsyncCallback<com.extjs.gxt.ui.client.data.PagingLoadResult<UserSummary>> callback) {
		userService.getMappedFormUserNames(form.getId(), PagingUtil.createPagingLoadConfig(pagingLoadConfig), 
				new EmitAsyncCallback<PagingLoadResult<UserHeader>>() {
            @Override
            public void onSuccess(PagingLoadResult<UserHeader> result) {
                ProgressIndicator.hideProgressBar();
            	callback.onSuccess(new BasePagingLoadResult<UserSummary>(convertUserResults(result), 
            			result.getOffset(), result.getTotalLength()));
            }
        });
    }

	@Override
    public void getUnMappedData(
            PagingLoadConfig pagingLoadConfig,
            final AsyncCallback<com.extjs.gxt.ui.client.data.PagingLoadResult<UserSummary>> callback) {
		userService.getUnmappedFormUserNames(form.getId(), PagingUtil.createPagingLoadConfig(pagingLoadConfig), 
				new EmitAsyncCallback<PagingLoadResult<UserHeader>>() {
            @Override
            public void onSuccess(PagingLoadResult<UserHeader> result) {
                ProgressIndicator.hideProgressBar();
            	callback.onSuccess(new BasePagingLoadResult<UserSummary>(convertUserResults(result), 
            			result.getOffset(), result.getTotalLength()));
            }
        });
    }
	
	private List<UserSummary> convertUserResults(PagingLoadResult<UserHeader> result) {
        List<UserSummary> results = new ArrayList<UserSummary>();
    	List<UserHeader> users = result.getData();
        for (UserHeader uh : users) {
        	results.add(new UserSummary(uh));
        }
        return results;
    }

	@Override
    public void addMapping(List<UserSummary> usersToAdd,
            final ItemAccessListField<UserSummary> userAccessListField) {
	    userService.saveMappedFormUserNames(form.getId(), convertUserList(usersToAdd), null, new EmitAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
               userAccessListField.refresh();
            }
        });
    }

	@Override
    public void deleteMapping(List<UserSummary> usersToDelete,
            final ItemAccessListField<UserSummary> userAccessListField) {
		userService.saveMappedFormUserNames(form.getId(), null, convertUserList(usersToDelete), new EmitAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
               userAccessListField.refresh();
            }
        });
    }
	
	private List<UserHeader> convertUserList(List<UserSummary> userList) {
		List<UserHeader> users = new ArrayList<UserHeader>();
		for (UserSummary u : userList) {
			users.add(u.getUserHeader());
		}
		return users;
	}
}
