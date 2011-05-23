package org.openxdata.server.serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openxdata.server.admin.model.exception.UnexpectedException;

/**
 * Provides the default xform serialization and deserialization from and to the sever.
 * An example of such clients could be mobile devices collecting data in for instance 
 * offline mode, and then send it to the server when connected.
 * 
 * For those who want a different serialization format for xforms,
 * just implement the SerializableData interface and specify the class
 * using the settings {formSerializer}. 
 * The jar containing this class can then be
 * put under the webapps/openxdata/web-inf/lib folder.
 * One of the reasons one could want a different serialization format
 * is for performance by doing a more optimized and compact format. Such an example
 * exists in the EpiHandy compact implementation of xforms.
 * 
 * @author Daniel
 *
 */
public class DefaultXformSerializer implements XformSerializer, StudySerializer, UserSerializer {

	/**
	 * Creates a new xform serailzer.
	 */
	public DefaultXformSerializer(){

	}

	@Override
	public void serializeForms(OutputStream os,List<String> xforms, Integer studyId, String studyName, String studyKey) {
		DataOutputStream dos = new DataOutputStream(os);
		
		try {
			dos.writeByte(xforms.size()); //Write the size such that the party at the other end knows how many times to loop.
			for(String xml : xforms)
				dos.writeUTF(xml);
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void serializeStudies(OutputStream os,Object data){
		DataOutputStream dos = new DataOutputStream(os);
		
		List<Object[]> studies = (List<Object[]>)data;

		try {
			dos.writeByte(studies.size());

			for(Object[] study : studies){
				dos.writeInt((Integer)study[0]);
				dos.writeUTF((String)study[1]);
			}
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void serializeUsers(OutputStream os,Object data){
		DataOutputStream dos = new DataOutputStream(os);
		
		List<Object[]> users = (List<Object[]>)data; 

		try {
			dos.writeByte(users.size());
			for(Object[] user : users){
				dos.writeInt((Integer)user[0]);
				dos.writeUTF((String)user[1]);
				dos.writeUTF((String)user[2]);
				dos.writeUTF((String)user[3]);
			}
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}
	
	@Override
	public List<String> deSerialize(InputStream is, Map<Integer, String> map){
		DataInputStream dis = new DataInputStream(is);
		
		List<String> forms = new ArrayList<String>();

		try {
			int len = dis.readByte();
			for(int i=0; i<len; i++) {
				forms.add(dis.readUTF());
			}
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}

		return forms;
	}

	@Override
	public void serializeAccessDenied(OutputStream os) {		
		// TODO not yet implemented
	}

	@Override
	public void serializeFailure(OutputStream os, Exception ex) {
		// TODO not yet implemented
	}

	@Override
	public void serializeSuccess(OutputStream os) {
		// TODO not yet implemented
	}
}
