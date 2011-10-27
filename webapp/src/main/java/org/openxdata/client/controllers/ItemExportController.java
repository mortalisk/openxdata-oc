package org.openxdata.client.controllers;

import org.openxdata.client.AppMessages;
import org.openxdata.client.Emit;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.client.views.ItemExportView;
import org.openxdata.server.admin.model.Exportable;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public class ItemExportController extends Controller {

	ItemExportView view;
	AppMessages appMessages = GWT.create(AppMessages.class);
	
	public static final EventType EXPORTITEM = new EventType();

	public ItemExportController() {
		super();
		registerEventTypes(EXPORTITEM);
	}

	@Override
	public void handleEvent(AppEvent event) {
		
		GWT.log("ItemExportController : handleEvent");
        if (event.getType() == ItemExportController.EXPORTITEM) {
        	view = new ItemExportView(this);
        	forwardToView(view, event);
        }
	}
	
	public void exportEditable(final Exportable exportable, final String fileName){

		ProgressIndicator.showProgressBar();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				try{

					String url = "studyexport?";
					url += "type=" + exportable.getType();
					url += "&id=" + exportable.getId();
					url += "&filename=" + fileName;

					Emit.openWindow(url);

					ProgressIndicator.hideProgressBar();	
				}
				catch(Exception ex){
					MessageBox.alert(appMessages.error(), appMessages.exportError(), null);
					ProgressIndicator.hideProgressBar();	
				}	
			}
		});
	}
}
