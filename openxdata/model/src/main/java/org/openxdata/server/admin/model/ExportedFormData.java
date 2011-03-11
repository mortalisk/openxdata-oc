package org.openxdata.server.admin.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.sf.gilead.pojo.gwt.LightEntity;

/**
 * Class to represent exported form data - form data that has been
 * archived to a rdms.
 */
public class ExportedFormData extends LightEntity {

    private static final long serialVersionUID = -3170346869483517114L;

    /** original form data */
    private FormData formData;
    /** array of exported form data in original format. question binding is used as the key*/
    private Map<String, ExportedDataType> exportedFields = new HashMap<String, ExportedDataType>();

    public ExportedFormData() {
        super();
    }
    
    public ExportedFormData(FormData formData) {
        super();
        this.formData = formData;
    }

    public ExportedFormData(FormData formData, Map<String, ExportedDataType> exportedFields) {
        super();
        this.formData = formData;
        this.exportedFields = exportedFields;
    }

    public FormData getFormData() {
        return formData;
    }

    public void setFormData(FormData formData) {
        this.formData = formData;
    }

    public Map<String, ExportedDataType> getExportedFields() {
        return exportedFields;
    }
    
    public ExportedDataType getExportedField(String binding) {
        return exportedFields.get(binding);
    }

    public void setExportedFields(Map<String, ExportedDataType> exportedFields) {
        this.exportedFields = exportedFields;
    }
    
    /**
     * Converts the Serializable object for the field (from the database) 
     * to an ExportedDataType (which can be used in the client) 
     * and stores the value in a map using the key
     * 
     * @param key String key identifying the field
     * @param fieldObject Serializable object representing the field value
     */
    public void putExportedField(String key, Serializable fieldObject) {
        if (fieldObject != null) {
            ExportedDataType dataType = ExportedDataType.getDataType(fieldObject);
        	if (dataType == null)
        		throw new IllegalArgumentException("Unsupported type of fieldObject: '" + fieldObject.getClass().getName() + "' for field: '" + key + "'");
        	this.exportedFields.put(key, dataType);
        }
    }
}
