package org.openxdata.server.export.http;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.export.DataExportUtil;

/**
 * Authentication provider for {@link HttpPostExportTask} that provides
 * HttpClient with credentials required for Basic and Digest authentication.
 * 
 * Requires the following parameters from the TaskDef:
 * 
 * <table border=0 cellspacing=3 cellpadding=0>
 * <tr bgcolor="#ccccff">
 * <th>Parameter name
 * <th>Description
 * <th>Required
 * <th>Default value
 * <tr>
 * <td>postUrl
 * <td>The url that the data is being posted to. This is used to get the host
 * name and port for the authentication scope.
 * <td align="center">X
 * <td>
 * <tr bgcolor="#eeeeff">
 * <td>username
 * <td>Yhe username to authenticate with.
 * <td align="center">X
 * <td>
 * <tr>
 * <td>password
 * <td>The password to authenticate with.
 * <td align="center">X
 * <td>
 * </table>
 * 
 * @author simon@cell-life.org
 */
public class BasicDigestAuthenticationProvider implements AuthenticationProvider {

	public static final String PARAM_USERNAME = "username";
	public static final String PARAM_PASSWORD = "password";
	
	@Override
	public void authenticate(TaskDef taskDef, DefaultHttpClient client) throws Exception {
		String postUrl = DataExportUtil.getParameter(taskDef, HttpPostExportTask.PARAM_POST_URL);
		String host = getHost(postUrl);
		int port = getPort(postUrl);
		
		String username = DataExportUtil.getParameter(taskDef, PARAM_USERNAME);
		String password = DataExportUtil.getParameter(taskDef, PARAM_PASSWORD);
		
		client.getCredentialsProvider().setCredentials(
                new AuthScope(host, port),
                new UsernamePasswordCredentials(username, password));
	}


	String getHost(String postUrl) {
		String[] split = postUrl.split("/", 4);
		String baseurl = split[2];
		int indexOfColon = baseurl.indexOf(':');
		if (indexOfColon > 0){
			baseurl = baseurl.substring(0, indexOfColon);
		}
		return baseurl;
	}


	int getPort(String postUrl) {
		String[] split = postUrl.split("/", 4);
		String protocol = split[0];
		String baseurl = split[2];
		int indexOfColon = baseurl.indexOf(':');
		if (indexOfColon > 0){
			String port = baseurl.substring(indexOfColon+1);
			return Integer.parseInt(port);
		} else if (protocol.equalsIgnoreCase("https:")){
			return 443;
		} 
			
		return 80;
	}
}