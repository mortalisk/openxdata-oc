package org.openxdata.client.controllers;

import java.util.Date;
import java.util.List;

import org.openxdata.client.AppMessages;
import org.openxdata.client.Emit;
import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.RefreshablePublisher;
import org.openxdata.client.service.StudyServiceAsync;
import org.openxdata.client.views.ItemImportView;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.google.gwt.core.client.GWT;

public class ItemImportController extends Controller {

	ItemImportView view;
	Editable editable = null;
	private StudyServiceAsync studyService;
	AppMessages appMessages = GWT.create(AppMessages.class);
	public static final EventType IMPORTITEM = new EventType();
	
	public ItemImportController(StudyServiceAsync studyService){
		super();
		this.studyService = studyService;
		registerEventTypes(IMPORTITEM);
	}

	@Override
	public void handleEvent(AppEvent event) {
		GWT.log("ItemImportController : handleEvent");
        if (event.getType() == ItemImportController.IMPORTITEM) {
        	view = new ItemImportView(this);
        	forwardToView(view, event);
        }
	}
	
	public void setEditable(Editable editable){
		this.editable = editable;
	}
	
	public void createStudy(){
		StudyDef study = (StudyDef)editable;
		study.setCreator((User) Registry.get(Emit.LOGGED_IN_USER_NAME));
		study.setDateCreated(new Date());
		study.setDirty(true);
		
		List<FormDef> forms = study.getForms();
		if(forms != null){
			for(FormDef x : forms){
				createForm(x, study);
			}
		}
		
		// Save Study
		saveImportedStudy(study);
	}
	
	public void addFormToStudy(StudyDef study){
		FormDef form = createForm((FormDef)editable, study);
		study.addForm(form);
		
		// Save Study
		saveImportedStudy(study);
	}
	
	public void addFormVersionToForm(FormDef form){
		FormDefVersion version = createFormVersion((FormDefVersion)editable, form);
		form.addVersion(version);
		
		// Save Form
		saveImportedStudy(form.getStudy());
		
	}
	
	private FormDef createForm(FormDef form, StudyDef study){
		form.setStudy(study);
		form.setCreator((User) Registry.get(Emit.LOGGED_IN_USER_NAME));
		form.setDateCreated(new Date());
		form.setDirty(true);
		
		List<FormDefVersion> versions = form.getVersions();
		if(versions != null){
			for(FormDefVersion x : versions){
				createFormVersion(x, form);
			}
		}
		return form;
	}

	private FormDefVersion createFormVersion(FormDefVersion version, FormDef form) {
		version.setFormDef(form);
		version.setCreator((User) Registry.get(Emit.LOGGED_IN_USER_NAME));
		version.setDateCreated(new Date());
		version.setDirty(true);
		return version;
	}
	
	private void saveImportedStudy(final StudyDef study) {
		
		GWT.log("ItemImportController : saveImportedStudy");
		
		studyService.saveStudy(study, new EmitAsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				RefreshablePublisher.get().publish(new RefreshableEvent(RefreshableEvent.Type.CREATE_STUDY, study));
			}
		});
	}
}
