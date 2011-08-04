package org.openxdata.server.export;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.admin.model.TaskParam;

public class DataExportUtil {

	public static String getParameter(TaskDef taskDef, String name) {
		return getParameter(taskDef, name, null);
	}

	public static String getParameter(TaskDef taskDef, String name, String defaultValue) {
		String value = taskDef.getParamValue(name);
		if (value == null || value.isEmpty()){
			if (defaultValue == null) {
				throw new IllegalArgumentException("Empty value for parameter: " + name);
			} 
			return defaultValue;
		} 
		return value;
	}

	public static List<String> getMultiParamValues(TaskDef taskDef, String name) {
		List<TaskParam> allParameters = taskDef.getParameters();
		List<String> parameters = new ArrayList<String>();
		for (TaskParam taskParam : allParameters) {
			if (taskParam.getName().equalsIgnoreCase(name)){
				parameters.add(taskParam.getValue());
			}
		}
		return parameters;
	}

}