package org.openxdata.server.module.openclinica.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openxdata.server.module.openclinica.serialization.SerializationUtils;

/**
 * 
 * @author daniel
 *
 */
public class Subject {

	private Integer subjectId;
	private String studySubjectId;
	private String personId;
	private String secondaryId;
	private String oid;
	private String gender;
	private Date birthDate;
	private boolean isNew = false;
	
	private List<StudySubjectEvent> events;
	
	
	public Subject(){
		
	}

	public Integer getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Integer subjectId) {
		this.subjectId = subjectId;
	}


	public List<StudySubjectEvent> getEvents() {
		return events;
	}


	public void setEvents(List<StudySubjectEvent> events) {
		this.events = events;
	}


	public String getStudySubjectId() {
		return studySubjectId;
	}


	public void setStudySubjectId(String studySubjectId) {
		this.studySubjectId = studySubjectId;
	}


	public String getPersonId() {
		return personId;
	}


	public void setPersonId(String personId) {
		this.personId = personId;
	}


	public String getSecondaryId() {
		return secondaryId;
	}


	public void setSecondaryId(String secondaryId) {
		this.secondaryId = secondaryId;
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}


	public Date getBirthDate() {
		return birthDate;
	}


	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	
	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public void serialize(DataOutputStream dos) throws IOException {
		SerializationUtils.writeInteger(dos, subjectId);
		
		SerializationUtils.writeUTF(dos,studySubjectId);
		SerializationUtils.writeUTF(dos,personId);
		SerializationUtils.writeUTF(dos,secondaryId);
		SerializationUtils.writeUTF(dos,oid);
		SerializationUtils.writeUTF(dos,gender);
		SerializationUtils.writeDate(dos,birthDate);
		
		dos.writeBoolean(isNew);
		
		if(events == null)
			dos.writeByte(0);
		else{
			int size = events.size();
			dos.writeByte(size);
			for(int index = 0; index < size; index++)
				((StudySubjectEvent)events.get(index)).serialize(dos);
		}
	}
	
	
	public void deserialize(DataInputStream dis) throws Exception {
		
		subjectId = SerializationUtils.readInteger(dis);
		
		studySubjectId = SerializationUtils.readUTF(dis);
		personId = SerializationUtils.readUTF(dis);
		secondaryId = SerializationUtils.readUTF(dis);
		oid = SerializationUtils.readUTF(dis);
		gender = SerializationUtils.readUTF(dis);
		birthDate = SerializationUtils.readDate(dis);
		
		isNew = dis.readBoolean();
		
		events = new ArrayList<StudySubjectEvent>();
		int size = dis.readByte();
		for(int index = 0; index < size; index++){
			StudySubjectEvent event = new StudySubjectEvent();
			event.deserialize(dis);
			events.add(event);
		}
	}
}
