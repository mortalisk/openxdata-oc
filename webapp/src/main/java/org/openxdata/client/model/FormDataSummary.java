package org.openxdata.client.model;

import java.util.Map;

import org.openxdata.server.admin.model.ExportedDataType;
import org.openxdata.server.admin.model.ExportedFormData;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDataHeader;
import org.openxdata.server.admin.model.FormDefVersion;

import com.extjs.gxt.ui.client.data.BaseModel;

public class FormDataSummary extends BaseModel {

    private static final long serialVersionUID = 3842754006212589283L;

    private FormDefVersion formDefVersion;
    private ExportedFormData exportedFormData;
    private FormDataHeader formDataHeader;
    
    public FormDataSummary(FormDefVersion formDefVersion, ExportedFormData exportedFormData) {
        this.formDefVersion = formDefVersion;
        this.exportedFormData = exportedFormData;
        convertExportedFormData();
    }
    
	public FormDataSummary(FormDataHeader formDataHeader) {
		this.formDataHeader = formDataHeader;
		convertFormDataHeader();
	}

    private void convertExportedFormData() {
        FormData formData = exportedFormData.getFormData();
        if (formData != null) {
            setCapturer(formData.getCreator().getName());
        }
        setStatus("submitted");
        Map<String, ExportedDataType> data = exportedFormData.getExportedFields();
        for (String binding : data.keySet()) {
            setData(binding, data.get(binding).getValue());
        }
    }
    
	private void convertFormDataHeader() {
		setId(formDataHeader.getId());
		setForm(formDataHeader.getFormName() + "(" + formDataHeader.getVersionName()+")");
		set("userName",formDataHeader.getCreator());
		setDescription(formDataHeader.getDescription());
	}

    public void setCapturer(String capturer) {
        set("openxdata_user_name", capturer);
    }
    
	public void setDescription(String description) {
		set("description", description);
	}

	public void setId(Integer id) {
		set("id", id);
	}

	public void setForm(String formName) {
		set("form", formName);
	}

    public void setStatus(String status) {
        set("status", status);
    }
    
    public <X> X setData(String name, X value) {
        return set(name, value);
    }
    
    public FormDefVersion getFormDefVersion() {
        return formDefVersion;
    }

    public ExportedFormData getExportedFormData() {
        return exportedFormData;
    }
    
    public FormDataHeader getFormDataHeader() {
    	return formDataHeader;
    }
}