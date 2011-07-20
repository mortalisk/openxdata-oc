package org.openxdata.client.views;

import org.openxdata.client.controllers.ItemExportController;
import org.openxdata.server.admin.model.Exportable;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;

public class ItemExportView extends ActionOptionView {

	private TextField<String> fileName;

	private Exportable exportable;
	
	public ItemExportView(Controller controller) {
		super(controller);
	}
	
	protected void initialize(){
		super.initialize();
		
		execButton.setEnabled(false);
		
		firstRadio.setToolTip(appMessages.exportStudyTooltip());
		firstRadio.addListener(Events.OnClick, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				fileName.setValue(getStudyName());
				execButton.setEnabled(true);
			}
			
		});
		
		secondRadio.setToolTip(appMessages.exportFormTooltip());
		secondRadio.addListener(Events.OnClick, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				fileName.setValue(getFormName());
				execButton.setEnabled(true);
			}
			
		});
		
		thirdRadio.setToolTip(appMessages.exportFormVersionTooltip());
		thirdRadio.addListener(Events.OnClick, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				fileName.setValue(getFormVersionName());
				execButton.setEnabled(true);
			}
			
		});
		
		fileName = new TextField<String>();
		fileName.setFieldLabel(appMessages.filename());
		fileName.setAllowBlank(false);
		formPanel.add(fileName);
	}
	
	private String getFormVersionName() {
		FormDefVersion formVersion = (FormDefVersion)exportable;
		FormDef form = formVersion.getFormDef();
		StringBuilder formVersionName = new StringBuilder();
		formVersionName.append(form.getStudy().getName());
		formVersionName.append(" ");
		formVersionName.append(form.getName());
		formVersionName.append(" ");
		formVersionName.append(formVersion.getName());
		return formVersionName.toString();
	}
	
	private String getFormName() {
		FormDefVersion formVersion = (FormDefVersion)exportable;
		FormDef form = formVersion.getFormDef();
		StringBuilder formName = new StringBuilder();
		formName.append(form.getStudy().getName());
		formName.append(" ");
		formName.append(form.getName());
		return formName.toString();
	}
	
	private String getStudyName() {
		FormDefVersion formVersion = (FormDefVersion)exportable;
		FormDef form = formVersion.getFormDef();
		StringBuilder studyName = new StringBuilder();
		studyName.append(form.getStudy().getName());
		return studyName.toString();
	}

	@Override
	protected void handleEvent(AppEvent event) {
        GWT.log("ItemExportView : handleEvent");
        super.handleEvent(event);
        if (event.getType() == ItemExportController.EXPORTITEM) {
        	this.exportable = event.getData("exportable");
        	setFormNameValues((FormDefVersion) exportable);
        }
	}
	
	private void setFormNameValues(FormDefVersion formVersion){
		firstRadio.setBoxLabel(getFirstRadioLabel()+" ("+getStudyName()+")");
		secondRadio.setBoxLabel(getSecondRadioLabel()+"("+getFormName()+")");
		thirdRadio.setBoxLabel(getThirdRadioLabel()+" ("+getFormVersionName()+")");
	}
	
	@Override
	protected void action() {
		String name = fileName.getValue();

		Exportable itemToExport = null;
		if (firstRadio.getValue()) {
			itemToExport = ((FormDefVersion)exportable).getFormDef().getStudy();
		}
		if (secondRadio.getValue()) {
			itemToExport = ((FormDefVersion)exportable).getFormDef();
		}
		if (thirdRadio.getValue()) {
			itemToExport = exportable;
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
	String getExecuteButtonLable() {
		return appMessages.exportA();
	}

	@Override
	String getHeading() {
		return appMessages.exportEditable();
	}
}
