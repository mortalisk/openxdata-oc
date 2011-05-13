/*
 *  Licensed to the OpenXdata Foundation (OXDF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The OXDF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, 
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 *  Copyright 2010 http://www.openxdata.org.
 */
package org.openxdata.client.controllers;

import org.openxdata.client.AppMessages;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.RefreshablePublisher;
import org.openxdata.client.views.DeleteStudyFormView;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.StudyDef;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.google.gwt.core.client.GWT;
import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.service.StudyServiceAsync;
import org.openxdata.server.admin.client.service.FormServiceAsync;
import org.openxdata.server.admin.model.Editable;

/**
 * @author Angel
 *
 */
public class DeleteStudyFormController extends Controller {
	
	AppMessages appMessages = GWT.create(AppMessages.class);
	
	private DeleteStudyFormView deleteStudyFormView;
        private StudyServiceAsync studyService;
        private FormServiceAsync formService;
	
	public static final EventType DELETESTUDYFORM = new EventType();
	
	public DeleteStudyFormController(StudyServiceAsync studyService,FormServiceAsync formService){
		super();
		this.studyService = studyService;
                this.formService = formService;
		registerEventTypes(DELETESTUDYFORM);
	}

	@Override
	public void handleEvent(AppEvent event) {
    	GWT.log("DeleteStudyFormController : handleEvent");
        EventType type = event.getType();
        if (type == DELETESTUDYFORM) {
        	deleteStudyFormView = new DeleteStudyFormView(this);
            forwardToView(deleteStudyFormView, event);
        }
		
	}
	
	public void delete(final StudyDef study) {
    	       GWT.log("DeleteStudyFormController : delete study");
            // TODO: implement delete
            // FIXME: on success
            studyService.deleteStudy(study, new EmitAsyncCallback<Void>() {

                @Override
                public void onSuccess(Void result) {
                    RefreshablePublisher.get().publish(new RefreshableEvent(RefreshableEvent.Type.CREATE_STUDY, study));
                    deleteStudyFormView.closeWindow();
                }
            });
	}
	
	public void delete(final FormDef form) {
    	GWT.log("DeleteStudyFormController : delete form");
		// TODO: implement delete
		// FIXME: on success
            StudyDef study = form.getStudy();
            study.removeForm(form);

            studyService.saveStudy(study, new EmitAsyncCallback<Void>() {

                @Override
                public void onSuccess(Void result) {
                    RefreshablePublisher.get().publish(new RefreshableEvent(RefreshableEvent.Type.CREATE_STUDY, form));
                    deleteStudyFormView.closeWindow();
                }
            });
	}
	
	public void delete(final FormDefVersion formVersion) {
    	    GWT.log("DeleteStudyFormController : delete form version");
            // TODO: implement delete
            // FIXME: on success
            FormDef form = formVersion.getFormDef();
            form.removeVersion(formVersion);

            studyService.saveStudy(form.getStudy(), new EmitAsyncCallback<Void>() {

                @Override
                public void onSuccess(Void result) {
                    deleteStudyFormView.closeWindow();
                    RefreshablePublisher.get().publish(new RefreshableEvent(RefreshableEvent.Type.CREATE_STUDY, formVersion));
                }
            });
	}
        public void itemHasData(Editable item){
            GWT.log("EditStudyFormController : formHasData");
            formService.hasEditableData(item, new EmitAsyncCallback<Boolean>() {

                @Override
                public void onSuccess(Boolean result) {
                    deleteStudyFormView.onItemDataCheckComplete(result);
                }
            });
        }
}
