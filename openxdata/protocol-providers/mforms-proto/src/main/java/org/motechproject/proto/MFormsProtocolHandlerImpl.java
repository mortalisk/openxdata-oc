package org.motechproject.proto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.fcitmuk.epihandy.EpihandyXformSerializer;
import org.fcitmuk.epihandy.ResponseHeader;
import org.openxdata.proto.ProtocolHandler;
import org.openxdata.proto.SubmissionContext;
import org.openxdata.proto.exception.ProtocolException;

/**
 * The protocol handler for the MoTeCH mForms protocol.
 * 
 * @author batkinson
 */
public class MFormsProtocolHandlerImpl implements ProtocolHandler {
	
	private Logger log = Logger.getLogger(this.getClass().getName());

	public static final byte ACTION_DOWNLOAD_USERS = 7;
	public static final byte ACTION_DOWNLOAD_USERS_AND_FORMS = 11;
	public static final byte ACTION_DOWNLOAD_STUDY_LIST = 2;
	public static final byte ACTION_UPLOAD_DATA = 5;

	public static final byte RESPONSE_ERROR = 0;
	public static final byte RESPONSE_SUCCESS = 1;

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
		UploadDataProcessor uploadProcessor = new UploadDataProcessor();
		serializer.addDeserializationListener(uploadProcessor);

		// This shows that an api library is desirable
		if (action == ACTION_DOWNLOAD_STUDY_LIST) {
			try {
				List<Object[]> studies = ctx.getStudies();
				out.writeByte(ResponseHeader.STATUS_SUCCESS);
				serializer.serializeStudies(out, studies);
			} catch (Exception e) {
				try {
					out.writeByte(ResponseHeader.STATUS_ERROR);
				} catch (IOException e1) {
					throw new ProtocolException(
							"failed to write error response", e1);
				}
				throw new ProtocolException("failed to serialize studies", e);
			}
		} else if (action == ACTION_DOWNLOAD_USERS_AND_FORMS) {
			try {
				String userName = in.readUTF();
				int studyId = in.readInt();
				out.writeByte(ResponseHeader.STATUS_SUCCESS);
				List<Object[]> user = getUser(ctx.getUsers(), userName); // no longer downloading ALL users
				serializer.serializeUsers(ctx.getOutputStream(), user);
				String studyName = ctx.getStudyName(studyId);
				List<String> studyForms = ctx.getStudyForms(studyId);
				serializer.serializeForms(out, studyForms, studyId, studyName);
			} catch (Exception e) {
				try {
					out.writeByte(ResponseHeader.STATUS_ERROR);
				} catch (IOException e1) {
					throw new ProtocolException(
							"failed to write error response", e1);
				}
				throw new ProtocolException(
						"failed to serialize users and forms", e);
			}
		} else if (action == ACTION_DOWNLOAD_USERS) {
			try {
				String userName = in.readUTF();
				out.writeByte(ResponseHeader.STATUS_SUCCESS);
				List<Object[]> user = getUser(ctx.getUsers(), userName); // no longer downloading ALL users
				serializer.serializeUsers(ctx.getOutputStream(), user);
			} catch (Exception e) {
				try {
					out.writeByte(ResponseHeader.STATUS_ERROR);
				} catch (IOException e1) {
					throw new ProtocolException(
							"failed to write error response", e1);
				}
				throw new ProtocolException(
						"failed to serialize users", e);
			}
		} else if (action == ACTION_UPLOAD_DATA) {
			try {
				Map<Integer, String> xforms = ctx.getXForms();
				serializer.deserializeStudiesWithEvents(in, xforms);
                String[][] studyForms = uploadProcessor.getConvertedStudies();
                String[][] sessionReferences = new String[studyForms.length][];
                String[][] errorMessages = new String[studyForms.length][];
	            int numForms = uploadProcessor.getFormsProcessed();
	            log.debug("upload contains: studies=" + studyForms.length + ", forms=" + numForms);
	            // Starting processing here, only process until we run out of time
	            int processedForms = 0;
	            int errorForms = 0;
	            if (studyForms != null && numForms > 0) {
	            	for (int i = 0, n = studyForms.length; i<n; i++) {
	            		sessionReferences[i] = new String[studyForms[i].length];
	            		errorMessages[i] = new String[studyForms[i].length];
	            		for (int j = 0, m = studyForms[i].length; j<m; j++, processedForms++) {
	            			//if (maxProcessingTime > 0 && System.currentTimeMillis() - startTime > maxProcessingTime)
	            			//	break formprocessing;
	            			try {
	            				sessionReferences[i][j] = ctx.setUploadResult(studyForms[i][j]);
	            				log.debug("submitted data session reference="+sessionReferences[i][j]);
	            			} catch (Exception ex) {
	            				log.error("processing form failed", ex);
	            				errorMessages[i][j] = ex.getMessage();
	            				errorForms++;
	            			}
	            		}
	            	}
	            }

            	// Write out usual upload response
				out.writeByte(ResponseHeader.STATUS_SUCCESS);
	            out.writeInt(processedForms);
	            out.writeInt(errorForms);
	            for (int s = 0; s < errorMessages.length; s++) {
	            	// error uploads
	            	for (int f = 0; f < errorMessages[s].length; f++) {
	            		if (errorMessages[s][f] != null) {
	            			out.writeByte((byte) s);
	            			out.writeShort((short) f);
	            			out.writeUTF(errorMessages[s][f]);
	            		}
	            	}
	            }
	            for (int s = 0; s < sessionReferences.length; s++) {
	            	// session references
	            	for (int f = 0; f < sessionReferences[s].length; f++) {
	            		if (sessionReferences[s][f] != null) {
	            			out.writeByte((byte) s);
	            			out.writeShort((short) f);
	            			out.writeUTF(sessionReferences[s][f]);
	            		}
	            	}
	            }
			} catch (Exception e) {
				try {
					out.writeByte(ResponseHeader.STATUS_ERROR);
				} catch (IOException e1) {
					throw new ProtocolException(
							"failed to write error response", e1);
				}
				throw new ProtocolException(
						"failed to deserialize uploaded form data", e);
			}
		}
	}
	
	private List<Object[]> getUser(List<Object[]> users, String userName) {
		List<Object[]> filteredUser = new ArrayList<Object[]>();
		for (Object[] user : users) {
			if (userName.equalsIgnoreCase((String)user[1])) {
				filteredUser.add(user);
			}
		}
		return filteredUser;
	}
}
