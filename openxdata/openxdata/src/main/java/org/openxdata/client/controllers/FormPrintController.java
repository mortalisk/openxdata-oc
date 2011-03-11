package org.openxdata.client.controllers;

import org.openxdata.client.views.FormPrintView;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.google.gwt.core.client.GWT;

public class FormPrintController extends Controller {

    public final static EventType FORMPRINTVIEW = new EventType();

    private FormPrintView formPrintView;
    
    public FormPrintController() {
        super();
        registerEventTypes(FORMPRINTVIEW);
    }
    
    @Override
    protected void initialize() {
        GWT.log("FormPrintController : initialize");
        formPrintView = new FormPrintView(this);
    }

    @Override
    public void handleEvent(AppEvent event) {
    	GWT.log("SurveyViewController handleEvent");
        EventType type = event.getType();
        if (type == FORMPRINTVIEW) {
            forwardToView(formPrintView, event);
        }
    }
}
