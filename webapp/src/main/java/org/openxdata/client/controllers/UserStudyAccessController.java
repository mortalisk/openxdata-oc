package org.openxdata.client.controllers;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.model.UserSummary;
import org.openxdata.client.util.PagingUtil;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.client.views.ItemAccessListField;
import org.openxdata.server.admin.client.service.StudyServiceAsync;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.paging.PagingLoadResult;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Controller for mapping access of Studies to Users.
 */
public class UserStudyAccessController implements ItemAccessController<UserSummary> {
	
	private StudyDef study;
	private StudyServiceAsync studyService;
	
	public UserStudyAccessController(StudyServiceAsync studyService) {
		this.studyService = studyService;
	}
	
	public UserStudyAccessController(StudyServiceAsync studyService, StudyDef study) {
		this.studyService = studyService;
		this.study = study;
	}
	
	public void setStudy(StudyDef study) {
		this.study = study;
	}

	@Override
    public void getMappedData(
            PagingLoadConfig pagingLoadConfig,
            final AsyncCallback<com.extjs.gxt.ui.client.data.PagingLoadResult<UserSummary>> callback) {
		studyService.getMappedUsers(study.getId(), PagingUtil.createPagingLoadConfig(pagingLoadConfig), 
				new EmitAsyncCallback<PagingLoadResult<User>>() {
            @Override
            public void onSuccess(PagingLoadResult<User> result) {
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
		studyService.getUnmappedUsers(study.getId(), PagingUtil.createPagingLoadConfig(pagingLoadConfig), 
				new EmitAsyncCallback<PagingLoadResult<User>>() {
            @Override
            public void onSuccess(PagingLoadResult<User> result) {
            	ProgressIndicator.hideProgressBar();
            	callback.onSuccess(new BasePagingLoadResult<UserSummary>(convertUserResults(result), 
            		   result.getOffset(), result.getTotalLength()));
            }
        });
    }
	
	private List<UserSummary> convertUserResults(PagingLoadResult<User> result) {
        List<UserSummary> results = new ArrayList<UserSummary>();
    	List<User> users = result.getData();
        for (User u : users) {
        	results.add(new UserSummary(u));
        }
        return results;
    }

	@Override
    public void addMapping(List<UserSummary> usersToAdd,
            final ItemAccessListField<UserSummary> userAccessListField) {
	    studyService.saveMappedStudyUsers(study.getId(), convertUserList(usersToAdd), null, new EmitAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
            	userAccessListField.refresh();
            }
        });
    }

	@Override
    public void deleteMapping(List<UserSummary> usersToDelete,
            final ItemAccessListField<UserSummary> userAccessListField) {
		studyService.saveMappedStudyUsers(study.getId(), null, convertUserList(usersToDelete), new EmitAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
            	userAccessListField.refresh();
            }
        });
    }
	
	private List<User> convertUserList(List<UserSummary> userList) {
		List<User> users = new ArrayList<User>();
		for (UserSummary u : userList) {
			users.add(u.getUser());
		}
		return users;
	}	
}