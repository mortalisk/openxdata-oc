package org.openxdata.server.servlet;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.openxdata.server.OpenXDataConstants;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.service.FormService;
import org.openxdata.server.util.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.w3c.dom.Document;

/**
 * Handles multimedia (Picture, Audio and Video) requests 
 * for the form designer and runner.
 * 
 */
public class MultimediaServlet extends HttpServlet {

	private FormService formService;
	
	private Logger log = LoggerFactory.getLogger(MultimediaServlet.class);
	private static final long serialVersionUID = 1239820102030344L;
	
	private final String KEY_MULTIMEDIA_POST_DATA = "MultidemiaPostData";
	private final String KEY_MULTIMEDIA_POST_CONTENT_TYPE = "MultidemiaPostContentType";
	
    @Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		ServletContext sctx = this.getServletContext();
		WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(sctx);
		formService = (FormService) ctx.getBean("formService");
	}
    
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//Setting header from hear ensures that user is not given a blank page
		//if there is not data
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", -1);
		response.setHeader("Cache-Control", "no-store");

		String sFormId = request.getParameter("formId");
		String xpath = request.getParameter("xpath");
		String contentType = request.getParameter("contentType");
		String name = request.getParameter("name");
		
		if("recentbinary".equals(request.getParameter("action"))){
			byte[] postData = (byte[])getSessionData(request,sFormId,KEY_MULTIMEDIA_POST_DATA+getFieldKey(sFormId,xpath));
			if(postData != null){				
				response.setContentType((String)getSessionData(request,sFormId,KEY_MULTIMEDIA_POST_CONTENT_TYPE+getFieldKey(sFormId,xpath)));
				response.getOutputStream().write(postData);
				
				setSessionData(request,sFormId,KEY_MULTIMEDIA_POST_CONTENT_TYPE+getFieldKey(sFormId,xpath),null);
				setSessionData(request,sFormId,KEY_MULTIMEDIA_POST_DATA+getFieldKey(sFormId,xpath),null);
			}
			return;
		}

		try{
			if(name == null || name.trim().length() == 0)
				name = "multimedia.3gp";

			if(sFormId == null || sFormId.trim().length() == 0)
				return;

			if(xpath == null || xpath.trim().length() == 0)
				return;

			byte[] bytes = (byte[])getSessionData(request,sFormId,KEY_MULTIMEDIA_POST_DATA+getFieldKey(sFormId,xpath)); 

			String value = null;
			
			if(bytes == null){
				int id = Integer.parseInt(sFormId);
				FormData formData = formService.getFormData(id);
				if(formData == null)
					return;

				String xml = formData.getData();
				if(xml == null || xml.trim().length() == 0)
					return;

				Document doc = XmlUtil.fromString2Doc(xml);
				if(doc == null)
					return;
				
				value = XmlUtil.getNodeValue(doc, xpath);
			}
			if(bytes != null || (value != null && value.trim().length() > 0)){
				if(bytes == null)
					bytes = Base64.decodeBase64(value.getBytes());
				
				if(bytes != null){
					if(contentType != null && contentType.trim().length() > 0){
						response.setContentType(contentType);

						//Send it as an attachement such that atleast firefox can also detect it
						if(contentType.contains("video") || contentType.contains("audio"))
							response.setHeader(OpenXDataConstants.HTTP_HEADER_CONTENT_DISPOSITION, OpenXDataConstants.HTTP_HEADER_CONTENT_DISPOSITION_VALUE + name + "\"");
					}

					response.getOutputStream().write(bytes);
				}
			}//This elese if is to prevent a blank page if there is no data.
			else if(contentType != null && (contentType.contains("video") || contentType.contains("audio")))
				response.setHeader(OpenXDataConstants.HTTP_HEADER_CONTENT_DISPOSITION, OpenXDataConstants.HTTP_HEADER_CONTENT_DISPOSITION_VALUE + name + "\"");
		}
		catch(Exception ex){
			log.error(ex.getLocalizedMessage(), ex);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String formId = request.getParameter("formId");
		String xpath = request.getParameter("xpath");

		CommonsMultipartResolver multipartResover = new CommonsMultipartResolver();
		if(multipartResover.isMultipart(request)){
			MultipartHttpServletRequest multipartRequest = multipartResover.resolveMultipart(request);
			MultipartFile uploadedFile = multipartRequest.getFile("filecontents");
			if (uploadedFile != null && !uploadedFile.isEmpty()) {
				byte[] postData = uploadedFile.getBytes();
				response.getOutputStream().print(new String(Base64.encodeBase64(postData)));

				setSessionData(request,formId,KEY_MULTIMEDIA_POST_CONTENT_TYPE+getFieldKey(formId,xpath),uploadedFile.getContentType());
				setSessionData(request,formId,KEY_MULTIMEDIA_POST_DATA+getFieldKey(formId,xpath),postData);
			}
		}
	}

	private static String getFieldKey(String formId, String xpath){
		return formId + xpath;
	}
	
	private static String getFormKey(String formId){
		return "MultidemiaData"+formId;
	}
	
	@SuppressWarnings("unchecked")
	private void setSessionData(HttpServletRequest request,String formId, String key, Object data){
		HttpSession session = request.getSession();
		String formKey = getFormKey(formId);
		HashMap<String,Object> dataMap = (HashMap<String,Object>)session.getAttribute(formKey);
		
		if(dataMap == null){
			dataMap = new HashMap<String,Object>();
			session.setAttribute(formKey, dataMap);
		}
		
		dataMap.put(key, data);
	}
	
	@SuppressWarnings("unchecked")
	private Object getSessionData(HttpServletRequest request,String formId, String key){
		HttpSession session = request.getSession();
		String formKey = getFormKey(formId);
		HashMap<String,Object> dataMap = (HashMap<String,Object>)session.getAttribute(formKey);
		
		if(dataMap != null)
			return dataMap.get(key);

		return null;
	}
	
	public static void clearFormSessionData(HttpServletRequest request,String formId){
		HttpSession session = request.getSession();
		session.setAttribute(getFormKey(formId), null);
	}
}
