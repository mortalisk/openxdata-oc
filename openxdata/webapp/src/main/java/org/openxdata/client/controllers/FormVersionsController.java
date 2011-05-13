/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.client.controllers;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.google.gwt.core.client.GWT;
import org.openxdata.client.AppMessages;
import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.RefreshablePublisher;
import org.openxdata.client.service.StudyServiceAsync;
import org.openxdata.client.views.FormVersionsView;
import org.openxdata.server.admin.model.FormDef;

/**
 *
 * @author victor
 */
public class FormVersionsController extends Controller {

    AppMessages appMessages = GWT.create(AppMessages.class);
    public final static EventType FORMVERSIONLIST = new EventType();
    private StudyServiceAsync studyService;
    private FormVersionsView formVersionsView;

    public FormVersionsController(StudyServiceAsync aStudyService) {
        super();
        this.studyService = aStudyService;
        registerEventTypes(FORMVERSIONLIST);
    }

    @Override
    protected void initialize() {
        GWT.log("FormVersionsController : initialize");
    }

    @Override
    public void handleEvent(AppEvent event) {
        GWT.log("FormVersionsController : handleEvent");
        EventType type = event.getType();
        if (type == FORMVERSIONLIST) {
            formVersionsView = new FormVersionsView(this);
            forwardToView(formVersionsView, event);
        }
    }

    public void saveFormDefVersion(final FormDef form) {
        GWT.log("FormVersionsController : saveFormDefVersion");
        studyService.saveStudy(form.getStudy(), new EmitAsyncCallback<Void>() {

            @Override
            public void onSuccess(Void result) {
                formVersionsView.closeWindow();
                RefreshablePublisher.get().publish(new RefreshableEvent(
                        RefreshableEvent.Type.UPDATE_STUDY, form.getStudy()));
            }
        });

    }
}
