package org.openxdata.client.views;

import org.openxdata.client.controllers.ItemExportController;
import org.openxdata.server.admin.model.Exportable;
import org.openxdata.server.admin.model.FormDef;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;

public class ItemExportView extends ActionOptionView {

	private RadioFieldSet exportFS;
	
	private TextField<String> studyName;
	private TextField<String> formName;
	private TextField<String> formVersionName;

	private Exportable exportable;
	
	public ItemExportView(Controller controller) {
		super(controller);
	}
	
	protected void initialize(){
		super.initialize();

		formPanel.removeAll();
		exportFS = new RadioFieldSet();
		formPanel.add(exportFS);
		
		studyName = new TextField<String>();
		studyName.setFieldLabel(appMessages.studyName());
		studyName.setAllowBlank(false);
		
		firstRadio = exportFS.addRadio("study", appMessages.exportStudy(), studyName);
		firstRadio.addListener(Events.OnClick, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				studyName.enable();
				
				formName.disable();
				formVersionName.disable();
			}
			
		});
		
		formName = new TextField<String>();
		formName.setFieldLabel(appMessages.formName());
		formName.setAllowBlank(false);
		
		secondRadio = exportFS.addRadio("form", appMessages.exportForm(), formName);
		secondRadio.addListener(Events.OnClick, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				formName.enable();

				studyName.disable();
				formVersionName.disable();
			}
			
		});
		
		formVersionName = new TextField<String>();
		formVersionName.setFieldLabel(appMessages.formVersionName());
		formVersionName.setAllowBlank(false);
		
		thirdRadio = exportFS.addRadio("form", appMessages.exportFormVersion(), formVersionName);
		thirdRadio.addListener(Events.OnClick, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				formVersionName.enable();

				formName.disable();
				studyName.disable();
			}
			
		});
		
		studyName.disable();
		formName.disable();
		formVersionName.disable();
		
	}

	@Override
	protected void handleEvent(AppEvent event) {
        GWT.log("ItemExportView : handleEvent");
        super.handleEvent(event);
        if (event.getType() == ItemExportController.EXPORTITEM) {
        	this.exportable = event.getData("exportable");
        	
        	firstRadio.setBoxLabel(firstRadio.getBoxLabel() +" - "+((FormDef)exportable).getStudy().getName());
			secondRadio.setBoxLabel(secondRadio.getBoxLabel() +" - "+exportable.getName());
			if (((FormDef)exportable).getDefaultVersion() != null) {
				thirdRadio.setBoxLabel(thirdRadio.getBoxLabel() +" - "+((FormDef)exportable).getDefaultVersion().getName());
				thirdRadio.show();
			} else {
				thirdRadio.hide();
			}
        }
	}
	
	@Override
	protected void action() {
		String name = null;
		
		// Handle to passed exportable item.
		Exportable itemToExport = exportable;
		if(firstRadio.getValue()){
			name = studyName.getValue();
			itemToExport = ((FormDef)exportable).getStudy();
		}
		if(secondRadio.getValue()){
			name = formName.getValue();
		}
		if(thirdRadio.getValue()){
			itemToExport = ((FormDef)exportable).getDefaultVersion();
			name = formVersionName.getValue();
		}
		
		name = name.replace(" ", "");
		ItemExportController controller = (ItemExportController) this.getController();
		controller.exportEditable(itemToExport, name);
		
		formPanel.clear();
		
		MessageBox.info(appMessages.export(), itemToExport.getType() + " " + itemToExport.getName() + " " +
				appMessages.successfulExportAs() + " " + name + ".", new Listener<MessageBoxEvent>() {
			@Override
			public void handleEvent(MessageBoxEvent be) {
			}
		});

		itemToExport = exportable;
	}

	@Override
	String getThirdRadioLabel() {
		return appMessages.exportFormVersion();
	}

	@Override
	String getSecondRadioLabel() {
		return appMessages.exportForm();
	}

	@Override
	String getFirstRadioLabel() {
		return appMessages.exportStudy();
	}

	@Override
	String getExecuteButtonLable() {
		return appMessages.exportA();
	}

	@Override
	String getHeading() {
		return appMessages.exportEditable();
	}
}
