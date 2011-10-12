package org.openxdata.client.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.DataField;
import com.extjs.gxt.ui.client.data.DataReader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;

public class CSVReader<D extends ListLoadResult<? extends ModelData>> implements DataReader<D> {

    private static final String CSV_COLUMN_SEPARATOR = ",";
    private static final String CSV_COLUMN_SEPARATOR2 = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
	private static final String CSV_LINE_SEPARATOR = "\n";

	private ModelType modelType;

	public CSVReader(ModelType modelType) {
		this.modelType = modelType;
	}

    @Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public D read(Object loadConfig, Object data) {
		ArrayList<ModelData> records = new ArrayList<ModelData>();

	    String[] csvLines = ((String)data).split(CSV_LINE_SEPARATOR);	    
	    if (csvLines.length <= 0) {
	    	return (D) records;
	    }

	    String[] columns = csvLines[0].split(CSV_COLUMN_SEPARATOR);

	    for (int i=1, j=csvLines.length; i<j; i++) {
	    	ModelData model =  new BaseModelData();
	    	String[] columnData = csvLines[i].split(CSV_COLUMN_SEPARATOR2);
	    	for (int k=0, l=columnData.length; k<l; k++) {
	    		String columnName = columns[k].replace("\"", "");
	    		DataField field = modelType.getField(columnName);
	    		if (field != null) {
	    			String columnValue = columnData[k].replace("\"", "");
	    			Class type = field.getType();
	    	        if (type != null) {
	    	          if (type.equals(Boolean.class)) {
	    	            model.set(columnName, Boolean.parseBoolean(columnValue));
	    	          } else if (type.equals(Integer.class)) {
	    	            model.set(columnName, Integer.parseInt(columnValue));
	    	          } else if (type.equals(Long.class)) {
	    	            model.set(columnName, Long.parseLong(columnValue));
	    	          } else if (type.equals(Float.class)) {
	    	            model.set(columnName, Float.parseFloat(columnValue));
	    	          } else if (type.equals(Double.class)) {
	    	            model.set(columnName, Double.parseDouble(columnValue));
	    	          } else if (type.equals(Date.class)) {
	    	            if ("timestamp".equals(field.getFormat())) {
	    	              Date d = new Date(Long.parseLong(columnValue) * 1000);
	    	              model.set(columnName, d);
	    	            } else {
	    	              DateTimeFormat format = DateTimeFormat.getFormat(field.getFormat());
	    	              Date d = format.parse(columnValue);
	    	              model.set(columnName, d);
	    	            }
	    	          }
	    	        } else {
	    	          model.set(columnName, columnValue);
	    	        }
	    		} else {
	    			GWT.log("Could not find field "+columnName+" in the ModelType.");
	    		}
	    	}
	    	records.add(model);
	    }
	    return (D) createReturnData(loadConfig, records, records.size());
    }

    /**
     * Responsible for the object being returned by the reader.
     * 
     * @param loadConfig the load config
     * @param records the list of models
     * @param totalCount the total count
     * @return the data to be returned by the reader
     */
    @SuppressWarnings("unchecked")
    protected Object createReturnData(Object loadConfig, List<ModelData> records, int totalCount) {
      return (D) records;
    }
}
