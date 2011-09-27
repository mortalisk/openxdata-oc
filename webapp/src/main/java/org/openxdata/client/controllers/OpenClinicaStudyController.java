package org.openxdata.client.controllers;

import java.util.List;

import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.views.OpenClincaStudyView;
import org.openxdata.server.admin.client.service.StudyServiceAsync;
import org.openxdata.server.admin.model.OpenclinicaStudy;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.google.gwt.core.client.GWT;

public class OpenClinicaStudyController extends Controller {
	
	private OpenClincaStudyView view;
	private List<OpenclinicaStudy> studies;
	private StudyServiceAsync openclinicaService;
	public static final EventType LOADOPECLINICASTUDIES = new EventType();
	
	
	public OpenClinicaStudyController(StudyServiceAsync studyService){
		view = new OpenClincaStudyView (this);
		this.openclinicaService = studyService;
		registerEventTypes(LOADOPECLINICASTUDIES);
	}

	@Override
	public void handleEvent(AppEvent event) {
		GWT.log("OpenClinicaStudyController: HandleEvent");
		getStudies();
		forwardToView(view, event);
	}
	
	void getStudies(){
		openclinicaService.getOpenClinicaStudies(new EmitAsyncCallback<List<OpenclinicaStudy>>() {

			@Override
			public void onSuccess(List<OpenclinicaStudy> result) {
				studies = result;
				view.setStudies(studies);
			}
		});
	}
}
