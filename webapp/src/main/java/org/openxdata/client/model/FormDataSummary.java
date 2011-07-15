package org.openxdata.client.model;

import java.util.Date;
import java.util.Map;

import org.openxdata.server.admin.model.ExportedDataType;
import org.openxdata.server.admin.model.ExportedFormData;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDef;

import com.extjs.gxt.ui.client.data.BaseModel;

public class FormDataSummary extends BaseModel {

    private static final long serialVersionUID = 3842754006212589283L;

    private FormDef formDef;
    private ExportedFormData exportedFormData;
    
    public FormDataSummary(FormDef formDef, ExportedFormData exportedFormData) {
        this.formDef = formDef;
        this.exportedFormData = exportedFormData;
        convertFormData();
    }
    
    private void convertFormData() {
        FormData formData = exportedFormData.getFormData();
        if (formData != null) {
            setCapturer(formData.getCreator().getName());
            setCaptureDate(formData.getDateCreated());
        }
        setStatus("submitted");
        Map<String, ExportedDataType> data = exportedFormData.getExportedFields();
        for (String binding : data.keySet()) {
            setData(binding, data.get(binding).getValue());
        }
    }
    
    public void setCapturer(String capturer) {
        set("openxdata_user_name", capturer);
    }
    
    public void setCaptureDate(Date date) {
        set("openxdata_date_created", date);
    }
    
    public void setStatus(String status) {
        set("status", status);
    }
    
    public <X> X setData(String name, X value) {
        return set(name, value);
    }
    
    public FormDef getFormDef() {
        return formDef;
    }

    public ExportedFormData getExportedFormData() {
        return exportedFormData;
    }
    
    public FormDataSummary getUpdatedFormDef(){
    	return this;
    }
}