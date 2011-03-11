package org.openxdata.server.module.openclinica.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


/**
 * An event as scheduled for a subject.
 * We do not have a subject id in this class because this object is 
 * supposed to be in an events list property of a subject.
 * 
 * @author daniel
 *
 */
public class StudySubjectEvent {

	/** The study event identifier. */
	private int eventId;
	
	/** The location where this event is scheduled for the subject. */
	private String location;
	
	
	public StudySubjectEvent(){
		
	}
	
	
	public int getEventId() {
		return eventId;
	}


	public void setEventId(int eventId) {
		this.eventId = eventId;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeInt(eventId);
		dos.writeUTF(location);
	}
	
	
	public void deserialize(DataInputStream dis) throws IOException {
		eventId = dis.readInt();
		location = dis.readUTF();
	}
}
