package org.openxdata.client.views;

import org.openxdata.client.controllers.ItemExportController;
import org.openxdata.server.admin.model.Exportable;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;

public class ItemExportView extends ActionOptionView {

	private TextField<String> fileName;
	
	public ItemExportView(Controller controller) {
		super(controller);
	}
	
	@Override
	protected void initialize(){
		super.initialize();
		
		firstRadio.setToolTip(appMessages.exportStudyTooltip());
		secondRadio.setToolTip(appMessages.exportFormTooltip());
		thirdRadio.setToolTip(appMessages.exportFormVersionTooltip());
		
		fileName = new TextField<String>();
		fileName.setFieldLabel(appMessages.filename());
		fileName.setAllowBlank(false);
		formPanel.add(fileName);
	}
	
	@Override
	protected void handleEvent(AppEvent event) {
		super.handleEvent(event);
		GWT.log("ItemExportView : handleEvent");
		if (event.getType() == ItemExportController.EXPORTITEM) {
        	formVersion = event.getData("formVersion");
        	form = event.getData("formDef");
        	if (form == null && formVersion != null) {
        		form = formVersion.getFormDef();
        	}
        }
		updateRadioButtons();
	}
	
	@Override
	protected void action() {
		String name = fileName.getValue();

		Exportable itemToExport = null;
		if (firstRadio.getValue()) {
			itemToExport = form.getStudy();
		}
		if (secondRadio.getValue()) {
			itemToExport = form;
		}
		if (thirdRadio.getValue()) {
			itemToExport = formVersion;
		}
		
		if (name != null && itemToExport != null) {
			name = name.replace(" ", "");
			ItemExportController controller = (ItemExportController) this.getController();
			controller.exportEditable(itemToExport, name);
			
			formPanel.clear();
			closeWindow();
		}
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
	String getExecuteButtonLabel() {
		return appMessages.exportA();
	}

	@Override
	String getHeading() {
		return appMessages.exportEditable();
	}

	@Override
    protected void onFirstRadioSelected() {
	    super.onFirstRadioSelected();
	    fileName.setValue(getStudyName());
    }

	@Override
    protected void onSecondRadioSelected() {
	    super.onSecondRadioSelected();
	    fileName.setValue(getFormName());
    }

	@Override
    protected void onThirdRadioSelected() {
	    super.onThirdRadioSelected();
	    fileName.setValue(getFormVersionName());
    }
}
