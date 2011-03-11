package org.openxdata.server.serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.kxml2.kdom.Document;
import org.openxdata.model.FormData;
import org.openxdata.model.FormDef;
import org.openxdata.model.StudyData;
import org.openxdata.model.StudyDataList;
import org.openxdata.model.StudyDef;
import org.openxdata.model.StudyDefList;
import org.openxdata.model.User;
import org.openxdata.server.admin.model.exception.UnexpectedException;


/**
 * Provides custom serialization of xforms for epihandy. We do binary serialization
 * to reduce the number of bytes sent over. It is very important that this class
 * does not swallow any exceptions but instead propagate them to the caller such
 * that any user data that is for instance is being submitted does not get lost
 * by this class absorbing exceptions and caller assumes everything went fine.
 * 
 * @author Daniel
 *
 */
public class KxmlXformSerializer implements XformSerializer, UserSerializer, StudySerializer{
	
	@SuppressWarnings("unchecked")
	@Override
	public void serializeStudies(OutputStream os,Object data) {
		StudyDefList studyList = new StudyDefList();
		
		List<Object[]> studies = (List<Object[]>)data;
		for(Object[] study : studies) {
			StudyDef studyDef = new StudyDef(((Integer)study[0]).intValue(),
					(String)study[1],
					(String)study[1]);
			if (study.length > 2) {
				List<String> xforms = (List<String>) study[2];
				setForms(xforms, studyDef);	
			}
			studyList.addStudy(studyDef);
		}
		
		try {
			studyList.write(new DataOutputStream(os));
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}
	
	@Override
	public void serializeForms(OutputStream os,List<String> xforms, Integer studyId, String studyName, String studyKey){
		
		StudyDef studyDef = new StudyDef();
		studyDef.setForms(new Vector<FormDef>()); //Just to temporarily work on a possible bug of handling null forms.
		setForms(xforms, studyDef);
		studyDef.setName(studyName);
		studyDef.setId(studyId.intValue());
		studyDef.setVariableName(studyKey);
		
		try {
			studyDef.write(new DataOutputStream(os));
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	private void setForms(List<String> xforms, StudyDef studyDef) {
		for(String xml : xforms){
			//Wrapped in a try catch block such that when a form fails, we carry on with the rest
			try{
				FormDef formDef = KxmlSerializerUtil.fromXform2FormDef(new StringReader(xml));
				studyDef.addForm(formDef);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void serializeUsers(OutputStream os,Object data) {
		List<Object[]> users = (List<Object[]>)data; 

		DataOutputStream dos = new DataOutputStream(os);
		
		try {
			dos.writeByte(users.size());
			for(Object[] user : users) {
				serializeUser(new User((Integer)user[0],(String)user[1],(String)user[2],(String)user[3]),dos);
			}
		} catch (Exception e) {
			throw new UnexpectedException(e);
		}
	}
	
	/**
	 * Serializes a user to the stream.
	 * 
	 * @param user - the user to serialize.
	 * @param dos  - the stream to write to.
	 */
	private void serializeUser(User user, OutputStream os) throws Exception{
		DataOutputStream dos = new DataOutputStream(os);
		
		dos.writeInt(user.getUserId());
		dos.writeUTF(user.getName());
		dos.writeUTF(user.getPassword());
		dos.writeUTF(user.getSalt());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> deSerialize(InputStream is, Map<Integer, String> xformMap) {
		DataInputStream dis = new DataInputStream(is);
		
		List<String> xmlforms = new ArrayList<String>();

		try {
			StudyDataList studyDataList = new StudyDataList();
			studyDataList.read(dis);
			Vector<StudyData> studies = studyDataList.getStudies();
			for(StudyData studyData: studies) {
				deSerialize(studyData,xmlforms,xformMap);
			}
		} catch (Exception e) {
			throw new UnexpectedException(e);
		}
		
		return xmlforms;
	}
	
	@SuppressWarnings("unchecked")
	private void deSerialize(StudyData studyData,List<String> xmlforms,Map<Integer, String> xformMap) {
		Vector<FormData> forms = studyData.getForms();
		for(FormData formData : forms){
			String xml = xformMap.get(formData.getDefId());
			
			//Form could be deleted on server when mobile has reference to it
			if(xml == null)
				throw new UnexpectedException("Cannot find form with id = "+formData.getDefId());
			
			Document doc = KxmlSerializerUtil.getDocument(new StringReader(xml));
			formData.setDef(KxmlSerializerUtil.getFormDef(doc));
			xml = KxmlSerializerUtil.updateXformModel(doc,formData);
			xmlforms.add(xml);
		}
	}

	@Override
	public void serializeAccessDenied(OutputStream dos) {
		// TODO not yet implemented		
	}

	@Override
	public void serializeFailure(OutputStream dos, Exception ex) {
		// TODO not yet implemented
	}

	@Override
	public void serializeSuccess(OutputStream os) {
		// TODO not yet implemented
	}
}
