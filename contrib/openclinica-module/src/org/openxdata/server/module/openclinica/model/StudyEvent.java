package org.openxdata.server.module.openclinica.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * An event in a study together with a list of identifiers for the crfs 
 * registered for the event.
 * 
 * @author daniel
 *
 */
public class StudyEvent {

	private int eventId;
	
	/** The name of the event. */
	private String name;
	
	private String oid;
	
	/** The list of crfs in the event. */
	private List<CrfDef> crfs;
	
	
	public StudyEvent(){
		
	}
	
	public int getEventId() {
		return eventId;
	}


	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public List<CrfDef> getCrfs() {
		return crfs;
	}


	public void setCrfs(List<CrfDef> crfs) {
		this.crfs = crfs;
	}


	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeInt(eventId);
		dos.writeUTF(oid);
		dos.writeUTF(name);
		
		if(crfs == null)
			dos.writeByte(0);
		else{
			int size = crfs.size();
			dos.writeByte(size);
			for(int index = 0; index < size; index++)
				((CrfDef)crfs.get(index)).serialize(dos);
		}
	}
	
	
	public void deserialize(DataInputStream dis) throws IOException {
		eventId = dis.readInt();
		oid = dis.readUTF();
		name = dis.readUTF();
		
		crfs = new ArrayList<CrfDef>();
		int size = dis.readByte();
		for(int index = 0; index < size; index++){
			CrfDef crf = new CrfDef();
			crf.deserialize(dis);
			crfs.add(crf);
		}
	}
}
