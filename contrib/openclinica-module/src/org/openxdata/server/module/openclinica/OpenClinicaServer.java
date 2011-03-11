package org.openxdata.server.module.openclinica;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.openxdata.server.OpenXDataConstants;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.context.Context;
import org.openxdata.server.module.openclinica.ws.WebServiceClient;
import org.openxdata.server.serializer.SerializerUtil;
import org.openxdata.server.service.FormDownloadService;
import org.openxdata.server.util.OpenXDataUtil;
import org.openxdata.server.security.util.OpenXDataSecurityUtil;
import org.openxdata.server.util.XmlUtil;
import org.w3c.dom.Document;

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZOutputStream;


/**
 * Serves studies,subjects,events, and CRFS
 * 
 * @author daniel
 *
 */
public class OpenClinicaServer {

	/** Value representing a not yet set status. */
	public static final byte STATUS_NULL = -1;

	/** Value representing success of an action. */
	public static final byte STATUS_SUCCESS = 1;

	/** Value representing failure of an action. */
	public static final byte STATUS_FAILURE = 0;

	/** Action to get a lsit of studies. */
	public static final byte ACTION_DOWNLOAD_STUDY_LIST = 2;

	/** Action to get a list of form definitions. */
	public static final byte ACTION_DOWNLOAD_FORMS = 3;

	/** Action to save a list of form data. */
	public static final byte ACTION_UPLOAD_DATA = 5;
	
	/** Action to download a list of subjects from the server. */
	public static final byte ACTION_DOWNLOAD_SUBJECTS = 6;

	/** Action to download a list of patients from the server. */
	public static final byte ACTION_DOWNLOAD_USERS = 7;

	/** Action to download a list of users and forms from the server. */
	public static final byte ACTION_DOWNLOAD_USERS_AND_FORMS = 11;
	
	/** Action to download a list of languages from the server. */
	public static final byte ACTION_DOWNLOAD_LANGUAGES = 15;
	
	/** Status to download menu text in the selected language. */
	public static final byte ACTION_DOWNLOAD_MENU_TEXT = 16;
	
	/** Action to get a list of form definitions. */
	public static final byte ACTION_DOWNLOAD_STUDY_FORMS = 17;
	
	/** Action to get a list of study events. */
	public static final byte ACTION_DOWNLOAD_STUDY_EVENTS = 19;
	
	
	private Logger log = Logger.getLogger(this.getClass());
	
	private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	
	
	/**
	 * Called when a new connection has been received. Failures are not handled
	 * in this class as different servers (BT,SMS, etc) may want to handle them
	 * differently.
	 * 
	 * @param dis - the stream to read from.
	 * @param dos - the stream to write to.
	 */
	public void processConnection(InputStream disParam, OutputStream dosParam){
		
		Context.openSession();
		
		WebServiceClient wsClient = new WebServiceClient();

		ZOutputStream gzip = new ZOutputStream(dosParam,JZlib.Z_BEST_COMPRESSION);
		DataOutputStream dos = new DataOutputStream(gzip);

		byte responseStatus = ResponseStatus.STATUS_ERROR;

		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataInputStream dis = new DataInputStream(disParam);

			String name = dis.readUTF();
			String password = dis.readUTF();
			String serializer = dis.readUTF();
			String locale = dis.readUTF();
			
			byte action = dis.readByte();			

			User user = Context.authenticate(name,password);
			if(user == null)
				responseStatus = ResponseStatus.STATUS_ACCESS_DENIED;
			else{
				DataOutputStream dosTemp = new DataOutputStream(baos);

				String hashedPassword = OpenXDataSecurityUtil.encodeString2(password);
				
				if (action == ACTION_UPLOAD_DATA)
					submitCrfs(wsClient,name,hashedPassword, dis, dosTemp,serializer);
				else if (action == ACTION_DOWNLOAD_USERS)
					wsClient.downloadUsers(name,hashedPassword,dis,dosTemp,serializer);
				else if (action == ACTION_DOWNLOAD_USERS_AND_FORMS)
					downloadUsersAndForms(dis.readUTF(),dis,dosTemp,serializer,locale);
				else if (action == ACTION_DOWNLOAD_STUDY_LIST)
					wsClient.downloadStudyList(name,hashedPassword,dis, dosTemp,serializer,locale);
				else if(action == ACTION_DOWNLOAD_LANGUAGES)
					;//formDownloadService.downloadLocales(dis, dosTemp,serializer);
				else if(action == ACTION_DOWNLOAD_MENU_TEXT)
					;//formDownloadService.downloadMenuText(dis, dosTemp,serializer,locale);
				else if(action == ACTION_DOWNLOAD_SUBJECTS)
					wsClient.downloadStudySubjects(name, hashedPassword,dis.readInt(), dis, dosTemp, serializer, locale);
				else if(action == ACTION_DOWNLOAD_STUDY_EVENTS)
					wsClient.downloadStudyEventsCRFs(name,hashedPassword,dis.readInt(),dis,dosTemp,serializer,locale);
				
				responseStatus = ResponseStatus.STATUS_SUCCESS;
			}

			dos.writeByte(responseStatus);

			if(responseStatus == ResponseStatus.STATUS_SUCCESS)
				dos.write(baos.toByteArray());

			dos.flush();
			gzip.finish();
		}
		catch(Exception ex){
			//TODO Need to return status code back to the caller.
			log.error(ex.getMessage(),ex);
			try{
				dos.writeByte(responseStatus);
				dos.flush();
				gzip.finish();
			}
			catch(Exception e){
				log.error(e.getMessage(), e);
			}
		}
		finally{
			Context.closeSession();
		}
	}
	
	/**
	 * Downloads a list of users and xforms.
	 * 
	 * @param dos - the stream to write to.
	 * @throws Exception
	 */
	private void downloadUsersAndForms(String studyId, DataInputStream dis,DataOutputStream dos, String serializer,String locale) throws Exception {
		FormDownloadService formDownloadService = Context.getFormDownloadService();
		formDownloadService.downloadUsers(dis,dos,serializer);
		formDownloadService.downloadForms(formDownloadService.getStudyIdWithKey(studyId),dis,dos,serializer,locale);
	}
	
	
	private void submitCrfs(WebServiceClient wsClient,String name,String hashedPassword, InputStream is, OutputStream os,String serializerName) throws Exception{
		
		FormDownloadService formDownloadService = Context.getFormDownloadService();
		
		//When submitting data, we need all the form versions and not just the default ones
		//because the user can change the default form version which already has data on
		//mobile devices. So getting all versions shields us from such problems.
		List<String> xforms = (List<String>)SerializerUtil.invokeDeserializationMethod(OpenXDataConstants.DESERIALIZER_METHOD_NAME_FORMS, is, OpenXDataConstants.SETTING_NAME_FORM_SERIALIZER,OpenXDataUtil.getFormSerializer(serializerName),formDownloadService.getFormsVersionXmlMap());

		if(xforms == null || xforms.size() == 0)
			throw new Exception("Problem encountered while deserializing data.");

		DocumentBuilder db = dbf.newDocumentBuilder();
		for(String xml : xforms){
			Document doc = db.parse(IOUtils.toInputStream(xml,"UTF-8"));
			Integer formId = Integer.valueOf(doc.getDocumentElement().getAttribute(OpenXDataConstants.ATTRIBUTE_NAME_FORMID));
			String descTemplate = doc.getDocumentElement().getAttribute(OpenXDataConstants.ATTRIBUTE_NAME_DESCRIPTION_TEMPLATE);

			//TODO Need to have some error handling in case the submission fails
			wsClient.uploadStudyCRFs(name,hashedPassword,xml);
			formDownloadService.saveFormData(new FormData(formId,xml,XmlUtil.getDescriptionTemplate(doc.getDocumentElement(),descTemplate),new Date(),Context.getAuthenticatedUser()));
		}
		
		((DataOutputStream)os).writeByte(STATUS_SUCCESS);
	}

}
