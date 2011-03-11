package org.openxdata.server.module.openclinica.ws;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.openxdata.server.module.openclinica.ws.dataimport.ImportDataWs;
import org.openxdata.server.module.openclinica.ws.eventlist.GetEventListWs;
import org.openxdata.server.module.openclinica.ws.studylist.GetStudyListWs;
import org.openxdata.server.module.openclinica.ws.subjectlist.GetSubjectsWs;
import org.openxdata.server.module.openclinica.ws.userlist.GetUsersWs;

/**
 * Connects to the openclinica web services
 * 
 * @author daniel
 *
 */
public class WebServiceClient {

	private GetStudyListWs getStudyListWs = new GetStudyListWs();
	private GetUsersWs getUsersWs = new GetUsersWs();
	private GetSubjectsWs getSubjectsWs  = new GetSubjectsWs();
	private GetEventListWs getEventListWs = new GetEventListWs();
	private ImportDataWs importDataWs = new ImportDataWs();
	
	public WebServiceClient(){
		
	}
	
	
	public void downloadStudyList(String name,String hashedPassword, InputStream is,OutputStream os,String serializerName,String locale) throws IOException {
		getStudyListWs.download(name, hashedPassword, is, os, serializerName, locale);
	}
	
	public void downloadStudyEventsCRFs(String name,String hashedPassword, int studyId,InputStream is,OutputStream os,String serializerName,String locale) throws IOException {
		getEventListWs.download(name, hashedPassword, studyId, is, os, serializerName, locale);
	}
	
	public void downloadStudySubjects(String name,String hashedPassword, int studyId, InputStream is,OutputStream os,String serializerName,String locale) throws IOException {
		getSubjectsWs.download(name, hashedPassword, studyId,is, os, serializerName, locale);
	}
	
	public void downloadUsers(String name,String hashedPassword, InputStream is,OutputStream os,String serializerName) throws IOException {
		getUsersWs.download(name, hashedPassword, is, os, serializerName);
	}
	
	public void uploadStudyCRFs(String name,String hashedPassword, String xml) throws IOException {
		importDataWs.importData(name, hashedPassword, xml);
	}
}
