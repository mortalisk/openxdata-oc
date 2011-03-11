package org.openxdata.server.module.openclinica.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author daniel
 *
 */
public class Study {

	/** The numeric study identifier. */
	private int studyId;
	
	/** The study oid identifier. */
	private String oid;
	
	/** The study name. */
	private String name;
	
	/** The list of events in the study. */
	private List<StudyEvent> events;

	
	public Study(){

	}
	
	
	public void serialize(DataOutputStream dos) throws Exception {
		dos.writeInt(studyId);
		dos.writeUTF(oid);
		dos.writeUTF(name);
		
		if(events == null)
			dos.writeInt(0);
		else{
			dos.writeInt(events.size());
			for(int index = 0; index < events.size(); index++)
				events.get(index).serialize(dos);
		}
	}
	
	
	public void deserialize(DataInputStream dis) throws Exception {
		studyId = dis.readInt();
		oid = dis.readUTF();
		name = dis.readUTF();
		
		events = new ArrayList<StudyEvent>();
		int size = dis.readInt();
		for(int index = 0; index < size; index++){
			StudyEvent event = new StudyEvent();
			event.deserialize(dis);
			events.add(event);
		}
	}
}
