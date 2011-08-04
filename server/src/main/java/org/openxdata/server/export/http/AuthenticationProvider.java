package org.openxdata.server.export.http;

import org.apache.http.impl.client.DefaultHttpClient;
import org.openxdata.server.admin.model.TaskDef;

/**
 * Interface for providing authentication functionality to the
 * {@link HttpPostExportTask}
 * 
 * @author simon@cell-life.org
 */
public interface AuthenticationProvider {

	void authenticate(TaskDef taskDef, DefaultHttpClient client) throws Exception;

}