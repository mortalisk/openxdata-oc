package org.openxdata.server.export.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.export.DataExportUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authentication provider for {@link HttpPostExportTask} that performs
 * authentication by simulating a web form post and storing the resulting
 * authentication cookie.
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
 * <td>authUrl
 * <td>the url to post the authentication data to
 * <td align="center">X
 * <td>
 * <tr bgcolor="#eeeeff">
 * <td>username
 * <td>the username to authenticate with
 * <td align="center">X
 * <td>
 * <tr>
 * <td>usernameFieldName
 * <td>the name of the form field for the username
 * <td align="center">
 * <td>j_username
 * <tr bgcolor="#eeeeff">
 * <td>password
 * <td>the password to authenticate with
 * <td align="center">X
 * <td>
 * <tr>
 * <td>passwordFieldName
 * <td>the name of the form field for the password
 * <td align="center">
 * <td>j_password
 * </table>
 * 
 * @author simon@cell-life.org
 */
public class FormAuthenticationProvider implements AuthenticationProvider {
	
	private static final Logger log = LoggerFactory.getLogger(FormAuthenticationProvider.class);

	public static final String PARAM_PASSWORD_FIELD_NAME = "passwordFieldName";
	public static final String PARAM_USERNAME_FIELD_NAME = "usernameFieldName";
	public static final String PARAM_AUTH_URL = "authUrl";
	public static final String PARAM_USERNAME = "username";
	public static final String PARAM_PASSWORD = "password";
	
	/**
	 * Authenticate via the login page in order to get the session cookie
	 * 
	 * @param client the client to use for authentication
	 * @throws Exception if unable to authenticate
	 */
	@Override
	public void authenticate(TaskDef taskDef, DefaultHttpClient client) throws Exception {
		String url = DataExportUtil.getParameter(taskDef, PARAM_AUTH_URL);
		String username = DataExportUtil.getParameter(taskDef, PARAM_USERNAME);
		String password = DataExportUtil.getParameter(taskDef, PARAM_PASSWORD);
		String usernameFieldName = DataExportUtil.getParameter(taskDef, PARAM_USERNAME_FIELD_NAME, "j_username");
		String passwordFieldName = DataExportUtil.getParameter(taskDef, PARAM_PASSWORD_FIELD_NAME, "j_password");
		
		HttpPost httpost = new HttpPost(url);
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair(usernameFieldName, username));
		nvps.add(new BasicNameValuePair(passwordFieldName, password));

		httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
         
		HttpResponse response = client.execute(httpost);
		log.debug("FormAuthentication response: " + response.getStatusLine());
	}

}