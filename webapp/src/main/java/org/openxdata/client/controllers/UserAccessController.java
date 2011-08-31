package org.openxdata.client.controllers;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.model.UserSummary;
import org.openxdata.client.service.StudyServiceAsync;
import org.openxdata.client.util.PagingUtil;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.client.views.UserAccessListField;
import org.openxdata.server.admin.client.service.FormServiceAsync;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.paging.PagingLoadResult;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class UserAccessController extends Controller {

	/**
	 * Retrieves a page of the users currently mapped to the form
	 * @param formId Integer item identifier
	 * @param pagingLoadConfig  GXT PagingLoadConfig to specify the page size and number
	 * @param callback containing a PagingLoadResult for UserSummary to display in the next page
	 */
	public void getMappedFormUsers(Integer formId, PagingLoadConfig pagingLoadConfig, 
			final AsyncCallback<com.extjs.gxt.ui.client.data.PagingLoadResult<UserSummary>> callback) {
		getFormService().getMappedUsers(formId, PagingUtil.createPagingLoadConfig(pagingLoadConfig), 
				new EmitAsyncCallback<PagingLoadResult<User>>() {
            @Override
            public void onSuccess(PagingLoadResult<User> result) {
                ProgressIndicator.hideProgressBar();
            	callback.onSuccess(new BasePagingLoadResult<UserSummary>(convertUserResults(result), 
            			result.getOffset(), result.getTotalLength()));
            }
        });
	}
	
	/**
	 * Retrieves a page of the users currently mapped to the study
	 * @param studyId Integer item identifier
	 * @param pagingLoadConfig  GXT PagingLoadConfig to specify the page size and number
	 * @param callback containing a PagingLoadResult for UserSummary to display in the next page
	 */
	public void getMappedStudyUsers(Integer studyId, PagingLoadConfig pagingLoadConfig, 
			final AsyncCallback<com.extjs.gxt.ui.client.data.PagingLoadResult<UserSummary>> callback) {
		getStudyService().getMappedUsers(studyId, PagingUtil.createPagingLoadConfig(pagingLoadConfig), 
				new EmitAsyncCallback<PagingLoadResult<User>>() {
            @Override
            public void onSuccess(PagingLoadResult<User> result) {
            	ProgressIndicator.hideProgressBar();
            	callback.onSuccess(new BasePagingLoadResult<UserSummary>(convertUserResults(result), 
            		   result.getOffset(), result.getTotalLength()));
            }
        });
	}
	
	/**
	 * Retrieves a page of the users currently NOT mapped to the form
	 * @param formId Integer item identifier
	 * @param pagingLoadConfig GXT PagingLoadConfig to specify the page size and number
	 * @param callback containing a PagingLoadResult for UserSummary to display in the next page
	 */
	public void getUnMappedFormUsers(Integer formId, PagingLoadConfig pagingLoadConfig, 
			final AsyncCallback<com.extjs.gxt.ui.client.data.PagingLoadResult<UserSummary>> callback) {
		getFormService().getUnmappedUsers(formId, PagingUtil.createPagingLoadConfig(pagingLoadConfig), 
				new EmitAsyncCallback<PagingLoadResult<User>>() {
            @Override
            public void onSuccess(PagingLoadResult<User> result) {
                ProgressIndicator.hideProgressBar();
            	callback.onSuccess(new BasePagingLoadResult<UserSummary>(convertUserResults(result), 
            			result.getOffset(), result.getTotalLength()));
            }
        });
	}
	
	/**
	 * Retrieves a page of the users currently NOT mapped to the study
	 * @param studyId Integer item identifier
	 * @param pagingLoadConfig GXT PagingLoadConfig to specify the page size and number
	 * @param callback containing a PagingLoadResult for UserSummary to display in the next page
	 */
	public void getUnMappedStudyUsers(Integer studyId, PagingLoadConfig pagingLoadConfig,
            final AsyncCallback<com.extjs.gxt.ui.client.data.PagingLoadResult<UserSummary>> callback) {
		getStudyService().getUnmappedUsers(studyId, PagingUtil.createPagingLoadConfig(pagingLoadConfig), 
				new EmitAsyncCallback<PagingLoadResult<User>>() {
            @Override
            public void onSuccess(PagingLoadResult<User> result) {
            	ProgressIndicator.hideProgressBar();
            	callback.onSuccess(new BasePagingLoadResult<UserSummary>(convertUserResults(result), 
            		   result.getOffset(), result.getTotalLength()));
            }
        });
    }
	
	/**
	 * Updates the Form's user mapping
	 * @param formId Integer item identifier
	 * @param usersToAdd List of Users to add to the mapping
	 * @param usersToDelete List of Users to remove from the mapping
	 * @param userAccessListField UI component used for callback updates
	 */
	public void updateFormMapping(Integer formId, List<User> usersToAdd, List<User> usersToDelete, final UserAccessListField userAccessListField) {
		getFormService().saveMappedFormUsers(formId, usersToAdd, usersToDelete, new EmitAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
               userAccessListField.refresh();
            }
        });
	}
	
	/**
	 * Updates the Study's user mapping
	 * @param studyId Integer item identifier
	 * @param usersToAdd List of Users to add to the mapping
	 * @param usersToDelete List of Users to remove from the mapping
	 * @param userAccessListField UI component used for callback updates
	 */
	public void updateStudyMapping(Integer studyId, List<User> usersToAdd, List<User> usersToDelete, final UserAccessListField userAccessListField) {
		getStudyService().saveMappedStudyUsers(studyId, usersToAdd, usersToDelete, new EmitAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
            	userAccessListField.refresh();
            }
        });
	}
	
	public abstract StudyServiceAsync getStudyService();
	public abstract FormServiceAsync getFormService();
	
	private List<UserSummary> convertUserResults(PagingLoadResult<User> result) {
        List<UserSummary> results = new ArrayList<UserSummary>();
    	List<User> users = result.getData();
        for (User u : users) {
        	results.add(new UserSummary(u));
        }
        return results;
    }
	
}
