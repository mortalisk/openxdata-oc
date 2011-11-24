package org.openxdata.client.controllers;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.model.StudySummary;
import org.openxdata.client.util.PagingUtil;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.client.views.ItemAccessListField;
import org.openxdata.server.admin.client.service.StudyServiceAsync;
import org.openxdata.server.admin.model.StudyDefHeader;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.paging.PagingLoadResult;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Controller for mapping access of Studies to a User
 */
public class StudyUserAccessController implements ItemAccessController<StudySummary> {
	
	private User user;
	private StudyServiceAsync studyService;
	
	public StudyUserAccessController(StudyServiceAsync studyService) {
		this.studyService = studyService;
	}
	
	public StudyUserAccessController(StudyServiceAsync studyService, User user) {
		this.studyService = studyService;
		this.user = user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

	@Override
    public void getMappedData(
            PagingLoadConfig pagingLoadConfig,
            final AsyncCallback<com.extjs.gxt.ui.client.data.PagingLoadResult<StudySummary>> callback) {
		studyService.getMappedStudyNames(user.getId(), PagingUtil.createPagingLoadConfig(pagingLoadConfig), 
				new EmitAsyncCallback<PagingLoadResult<StudyDefHeader>>() {
            @Override
            public void onSuccess(PagingLoadResult<StudyDefHeader> result) {
                ProgressIndicator.hideProgressBar();
            	callback.onSuccess(new BasePagingLoadResult<StudySummary>(convertStudyResults(result), 
            			result.getOffset(), result.getTotalLength()));
            }
        });
    }

	@Override
    public void getUnMappedData(
            PagingLoadConfig pagingLoadConfig,
            final AsyncCallback<com.extjs.gxt.ui.client.data.PagingLoadResult<StudySummary>> callback) {
		studyService.getUnmappedStudyNames(user.getId(), PagingUtil.createPagingLoadConfig(pagingLoadConfig), 
				new EmitAsyncCallback<PagingLoadResult<StudyDefHeader>>() {
            @Override
            public void onSuccess(PagingLoadResult<StudyDefHeader> result) {
                ProgressIndicator.hideProgressBar();
            	callback.onSuccess(new BasePagingLoadResult<StudySummary>(convertStudyResults(result), 
            			result.getOffset(), result.getTotalLength()));
            }
        });
    }
	
	private List<StudySummary> convertStudyResults(PagingLoadResult<StudyDefHeader> result) {
        List<StudySummary> results = new ArrayList<StudySummary>();
    	List<StudyDefHeader> studies = result.getData();
        for (StudyDefHeader sd : studies) {
        	results.add(new StudySummary(sd));
        }
        return results;
    }

	@Override
    public void addMapping(List<StudySummary> studiesToAdd, final ItemAccessListField<StudySummary> studyAccessListField) {
	    studyService.saveMappedUserStudyNames(user.getId(), convertStudyList(studiesToAdd), null, new EmitAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
            	studyAccessListField.refresh();
            }
        });
    }

	@Override
    public void deleteMapping(List<StudySummary> studiesToDelete, final ItemAccessListField<StudySummary> studyAccessListField) {
		studyService.saveMappedUserStudyNames(user.getId(), null, convertStudyList(studiesToDelete), new EmitAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
               studyAccessListField.refresh();
            }
        });
    }
	
	private List<StudyDefHeader> convertStudyList(List<StudySummary> studyList) {
		List<StudyDefHeader> studies = new ArrayList<StudyDefHeader>();
		for (StudySummary fd : studyList) {
			studies.add(fd.getStudyDefHeader());
		}
		return studies;
	}
}
