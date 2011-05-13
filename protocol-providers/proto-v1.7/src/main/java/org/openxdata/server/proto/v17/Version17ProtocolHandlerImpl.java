package org.openxdata.server.proto.v17;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.fcitmuk.epihandy.EpihandyXformSerializer;
import org.openxdata.model.ResponseHeader;
import org.openxdata.proto.ProtocolHandler;
import org.openxdata.proto.SubmissionContext;
import org.openxdata.proto.exception.ProtocolException;


public class Version17ProtocolHandlerImpl implements ProtocolHandler {
	
	public static final byte RESPONSE_ERROR = 0;

	public static final byte RESPONSE_SUCCESS = 1;

	/** Action to save a list of form data. */
	public static final byte ACTION_UPLOAD_DATA = 5;

	/** Action to get a list of form definitions. */
	public static final byte ACTION_DOWNLOAD_FORMS = 3;

	/** Action to get a lsit of studies. */
	public static final byte ACTION_DOWNLOAD_STUDY_LIST = 2;

	/** Action to download a list of patients from the server. */
	public static final byte ACTION_DOWNLOAD_USERS = 7;

	/** Status to download menu text in the selected language. */
	public static final byte ACTION_DOWNLOAD_MENU_TEXT = 16;
	
	/** Action to download a list of languages from the server. */
	public static final byte ACTION_DOWNLOAD_LANGUAGES = 15;

	/** Action to get a list of form definitions. */
	public static final byte ACTION_DOWNLOAD_STUDY_FORMS = 17;

	/** Action to download a list of users and forms from the server. */
	public static final byte ACTION_DOWNLOAD_USERS_AND_FORMS = 11;

	/** Status to download a list of users and studies from the server. */
	public static final byte ACTION_DOWNLOAD_USERS_AND_ALL_FORMS = 12;

	@Override
	public void handleRequest(SubmissionContext ctx) throws ProtocolException {
		DataInputStream in = ctx.getInputStream();
		DataOutputStream out = ctx.getOutputStream();

		byte action = -1;
		try {
			action = in.readByte();
		} catch (IOException e) {
			throw new ProtocolException("failed to read action", e);
		}
		
		EpihandyXformSerializer serializer = new EpihandyXformSerializer();
		
		if(action == ACTION_DOWNLOAD_STUDY_LIST){
			try{
				List<Object[]> studies = ctx.getStudies();
				out.writeByte(ResponseHeader.STATUS_SUCCESS);
				serializer.serializeStudies(out, studies);
			}catch(Exception ex){
				throw new ProtocolException("Failed to serialize studies", ex);
			}
		}
		else if(action == ACTION_DOWNLOAD_FORMS){
			downloadForms(ctx, in, out, serializer);
		}
		else if(action == ACTION_UPLOAD_DATA){
			try{
				Map<Integer, String> xforms = ctx.getXForms();				
				List<String> xmlForms = serializer.deSerialize(in, xforms);
				ctx.setUploadResult(xmlForms);
				
				// Write out response
				serializer.serializeSuccess(out);
				
				// Write out response
				out.writeByte(ResponseHeader.STATUS_SUCCESS);
				out.writeInt(xmlForms.size()); // Report # uploaded
				out.writeInt(0); // Server validation not implemented
				
			}catch(Exception ex){
				serializer.serializeFailure(out, ex);
			}
		}
		else if(action == ACTION_DOWNLOAD_USERS){			
			try {
				List<Object[]> users = ctx.getUsers();
				out.writeByte(ResponseHeader.STATUS_SUCCESS);
				serializer.serializeUsers(out, users);
			} catch (IOException e) {
				try {
					out.writeByte(ResponseHeader.STATUS_ERROR);
				} catch (IOException ioEx) {
					throw new ProtocolException(
							"Failed to serialize users and forms", e);
				}
			}
		}
		else if(action == ACTION_DOWNLOAD_USERS_AND_FORMS){
			downloadUsersAndForms(ctx, in, out, serializer);
		}
		else if(action == ACTION_DOWNLOAD_STUDY_FORMS){
			downloadForms(ctx, in, out, serializer);
		}
		else if(action ==ACTION_DOWNLOAD_USERS_AND_ALL_FORMS){
			downloadUsersAndForms(ctx, in, out, serializer);
		}
	}

	private void downloadUsersAndForms(SubmissionContext ctx,
			DataInputStream in, DataOutputStream out,
			EpihandyXformSerializer serializer) throws ProtocolException {
		int studyId;
		
		try {
			
			out.writeByte(ResponseHeader.STATUS_SUCCESS);
			
			// Serialize Users
			List<Object[]> users = ctx.getUsers();
			serializer.serializeUsers(out, users);
			
			// Serialize Forms
			studyId = in.readInt();
			String studyKey = ctx.getStudyKey(studyId);
			String studyName = ctx.getStudyName(studyId);
			List<String> studyForms = ctx.getStudyForms(studyId);
			
			
			serializer.serializeForms(out, studyForms, studyId, studyName, studyKey);
		} catch (IOException e) {
			try {
				out.writeByte(ResponseHeader.STATUS_ERROR);
			} catch (IOException ioEx) {
				throw new ProtocolException(
						"Failed to serialize Users and Forms", e);
			}
		}
	}

	private void downloadForms(SubmissionContext ctx, DataInputStream in,
			DataOutputStream out, EpihandyXformSerializer serializer)
			throws ProtocolException {
		try {
			
			out.writeByte(ResponseHeader.STATUS_SUCCESS);

			int studyId = in.readInt();
			String studyKey = ctx.getStudyKey(studyId);
			String studyName = ctx.getStudyName(studyId);
			List<String> studyForms = ctx.getStudyForms(studyId);
			
			
			serializer.serializeForms(out, studyForms, studyId, studyName, studyKey);
		} catch (Exception e) {
			try {
				out.writeByte(ResponseHeader.STATUS_ERROR);
			} catch (IOException ioEx) {
				throw new ProtocolException(
						"Failed to serialize users and forms", e);
			}
		}
	}
}
