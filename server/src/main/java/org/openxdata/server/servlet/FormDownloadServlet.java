package org.openxdata.server.servlet;

import static org.openxdata.server.OpenXDataConstants.ACTION_DOWNLOAD_STUDIES;
import static org.openxdata.server.OpenXDataConstants.REQUEST_ACTION_DOWNLOAD_FORMS;
import static org.openxdata.server.OpenXDataConstants.REQUEST_ACTION_DOWNLOAD_USERS;
import static org.openxdata.server.OpenXDataConstants.REQUEST_ACTION_UPLOAD_DATA;
import static org.openxdata.server.OpenXDataConstants.REQUEST_PARAMETER_ACTION;
import static org.openxdata.server.OpenXDataConstants.REQUEST_PARAM_BATCH_ENTRY;
import static org.openxdata.server.OpenXDataConstants.REQUEST_PARAM_FORM_SERIALIZER;
import static org.openxdata.server.OpenXDataConstants.REQUEST_PARAM_LOCALE;
import static org.openxdata.server.OpenXDataConstants.REQUEST_PARAM_PASSWORD;
import static org.openxdata.server.OpenXDataConstants.REQUEST_PARAM_STUDY_SERIALIZER;
import static org.openxdata.server.OpenXDataConstants.REQUEST_PARAM_USERNAME;
import static org.openxdata.server.OpenXDataConstants.REQUEST_PARAM_USER_SERIALIZER;
import static org.openxdata.server.OpenXDataConstants.STATUS_FAILURE;
import static org.openxdata.server.OpenXDataConstants.STATUS_NULL;
import static org.openxdata.server.OpenXDataConstants.STATUS_SUCCESS;
import static org.openxdata.server.OpenXDataConstants.TRUE_TEXT_VALUE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.openxdata.server.FormsServer;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.serializer.XformSerializer;
import org.openxdata.server.service.AuthenticationService;
import org.openxdata.server.service.FormDownloadService;
import org.openxdata.server.service.SerializationService;
import org.openxdata.server.service.UserService;
import org.openxdata.server.validation.OpenxdataValidations;
import org.openxdata.server.validation.PersonIdentificationValidations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Servlet that handles download of studies,forms,users and upload of collected data
 * form mobile devices.
 * 
 * @author daniel
 * @author maimoona kausar
 *
 */
public class FormDownloadServlet extends HttpServlet{

	private FormsServer formsServer;
	private UserService userService;
	private FormDownloadService formDownloadService;
	private SerializationService serializationService;
	private AuthenticationService authenticationService;
	
	private Logger log = LoggerFactory.getLogger(FormDownloadServlet.class);

	public static final long serialVersionUID = 111111111111111L;

    @Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		ServletContext sctx = this.getServletContext();
		WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(sctx);
		
		// Manual Injection
		formsServer = (FormsServer) ctx.getBean("formsServer");
		userService = (UserService) ctx.getBean("userService");
		formDownloadService = (FormDownloadService) ctx.getBean("formDownloadService");
		serializationService = (SerializationService) ctx.getBean("serializationService");
		authenticationService = (AuthenticationService) ctx.getBean("authenticationService");
	}
    
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		String serializer = request.getParameter(REQUEST_PARAM_FORM_SERIALIZER);
		
		if(StringUtils.isBlank(serializer)){
			serializer=null;//our application process null serializer
		}else{
			serializer=serializer.trim();
			if(!OpenxdataValidations.validateSerializerParam(serializer)){
				ServletUtils.setBadRequest(response, "serializer parameter was found to be invalid");
				return;
			}
		}
		OutputStream os = response.getOutputStream();
		try{
			String action = request.getParameter(REQUEST_PARAMETER_ACTION);

			if(StringUtils.isBlank(action)){
				formsServer.processConnection(request.getInputStream(), os);
			}else{
				action=action.trim();
				if(!OpenxdataValidations.validateActionParam(action)){
					ServletUtils.setBadRequest(response, "action parameter was found to be invalid");
	                return;
				}
                String username = request.getParameter(REQUEST_PARAM_USERNAME);
                if (!PersonIdentificationValidations.validateUsername(username)) {
                    ServletUtils.setBadRequest(response, "username was found to be invalid");
                    return;
                }

                String password = request.getParameter(REQUEST_PARAM_PASSWORD);
				User user = authenticationService.authenticate(username, password);
				if(user == null){
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					XformSerializer formSerializer = serializationService.getFormSerializer(serializer);
					formSerializer.serializeAccessDenied(os);
				}
				else{
					if(REQUEST_ACTION_UPLOAD_DATA.equalsIgnoreCase(action)){
						uploadData(request,response);
						XformSerializer formSerializer = serializationService.getFormSerializer(serializer);
						formSerializer.serializeSuccess(os);
					}
					else if(REQUEST_ACTION_DOWNLOAD_FORMS.equalsIgnoreCase(action))
						downloadForms(request,response);
					else if(ACTION_DOWNLOAD_STUDIES.equalsIgnoreCase(action))
						downloadStudies(request,response);
					else if(REQUEST_ACTION_DOWNLOAD_USERS.equalsIgnoreCase(action))
						downloadUsers(request,response);
				}
			}
		}
		catch(Exception ex){
			try{
				XformSerializer formSerializer = serializationService.getFormSerializer(serializer);
				formSerializer.serializeFailure(os, ex);
			}catch(Exception e){
				log.error(e.getLocalizedMessage(), e);
			}
		}
	}
	
	private void uploadData(HttpServletRequest request, HttpServletResponse response) throws IOException{
		byte status = STATUS_NULL;

		try{
			if(TRUE_TEXT_VALUE.equalsIgnoreCase(request.getParameter(REQUEST_PARAM_BATCH_ENTRY))){
				try{
					
					String serializerKey = request.getParameter(REQUEST_PARAM_FORM_SERIALIZER);
					XformSerializer formSerializer = serializationService.getFormSerializer(serializerKey);
					List<String> xforms = formSerializer.deSerialize(request.getInputStream(),getXforms());
					if(xforms != null){
						for(String xml : xforms)
							processForm(xml);

						status = STATUS_SUCCESS;
					}
					else{
						status = STATUS_FAILURE;
						formSerializer.serializeFailure(response.getOutputStream(), new Exception("No forms returned from the serializer"));
						log.warn("No forms returned by the serializer");
					}
				}
				catch(Exception ex){
					log.error(ex.getMessage(),ex);
					status = STATUS_FAILURE; 
				}
			}
			else{
				User user = authenticationService.authenticate(request.getParameter("uname"), request.getParameter("pw"));
				if(user == null)
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				else{
					response.setContentType("text/html");
                    String form = getRequestAsString(request);
					processForm(form);
				}
			}
		}
		catch(Exception ex){
			if(status == STATUS_NULL){
				PrintWriter out = response.getWriter();
				out.println("<HTML><HEAD><TITLE>Form Submission Status</TITLE>"+
						"</HEAD><BODY>Problem submitting form! <BR /> " + ex.getMessage() + "</BODY></HTML>");
				out.close();
			}
		}
	}

    /**
     * Reads text data from an http request stream.
     *
     * @param request the http request stream.
     * @return the text data.
     * @throws java.io.IOException
     */
    private String getRequestAsString(HttpServletRequest request) throws IOException {
        InputStream input = request.getInputStream();
        return IOUtils.toString(input, "UTF-8");
    }

	private void downloadForms(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String studyId = request.getParameter("studyId");
		if(StringUtils.isBlank(studyId))
			formDownloadService.downloadForms(response.getOutputStream(),request.getParameter(REQUEST_PARAM_FORM_SERIALIZER),request.getParameter(REQUEST_PARAM_LOCALE));
		else{
			if(!OpenxdataValidations.validateIntegerParam(studyId)){
				ServletUtils.setBadRequest(response, "study id was invalid");
				return;
			}
			formDownloadService.downloadForms(Integer.parseInt(studyId.trim()),response.getOutputStream(),request.getParameter(REQUEST_PARAM_FORM_SERIALIZER),request.getParameter(REQUEST_PARAM_LOCALE));
		}	
	}

	private void downloadStudies(HttpServletRequest request, HttpServletResponse response) throws IOException {
		formDownloadService.downloadStudies(response.getOutputStream(),request.getParameter(REQUEST_PARAM_STUDY_SERIALIZER),request.getParameter(REQUEST_PARAM_LOCALE));
	}

	private void downloadUsers(HttpServletRequest request, HttpServletResponse response) throws IOException {
		formDownloadService.downloadUsers(response.getOutputStream(),request.getParameter(REQUEST_PARAM_USER_SERIALIZER));
	}

	/**
	 * Processes and saves form submitted data, which is the xforms xml model.
	 * 
	 * @param xml the xforms model xml.
	 */
	private void processForm(String xml){
		try{
			formDownloadService.saveFormData(xml, userService.getLoggedInUser(), new Date());
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Gets a map of xforms keyed by the formid
	 * 
	 * @return - the xforms map.
	 */
	private Map<Integer, String> getXforms() {
		return formDownloadService.getFormsVersionXmlMap();
	}
}
