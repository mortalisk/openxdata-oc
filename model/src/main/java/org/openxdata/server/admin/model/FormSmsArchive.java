package org.openxdata.server.admin.model;

import java.util.Date;

public class FormSmsArchive  extends FormData{

	private static final long serialVersionUID = 3969864109678496787L;
	
	private User archiveCreator;
	private Date archiveDateCreated;
	
	private String sender;
	
	public FormSmsArchive(){
		
	}
	
	public FormSmsArchive(FormData formData){
		super(formData);
	}
		
	public User getArchiveCreator() {
		return archiveCreator;
	}
	
	public void setArchiveCreator(User archiveCreator) {
		this.archiveCreator = archiveCreator;
	}
	
	public Date getArchiveDateCreated() {
		return archiveDateCreated;
	}
	
	public void setArchiveDateCreated(Date archiveDateCreated) {
		this.archiveDateCreated = archiveDateCreated;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}
}
