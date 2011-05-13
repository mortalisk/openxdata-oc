package org.openxdata.server.admin.model;

import java.util.Date;

/**
 * Created when an exception is throw during processing of an sms form.
 * This class does not implement the Editable interface because for now
 * we do not support editing of sms error data.
 */
public class FormSmsError extends AbstractEditable {

	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = -3919484259284142514L;

	/** The numeric unique identifier of the form data. */
	private int formSmsErrorId = 0;
	
	/** The phone number of the sender. **/
	private String sender;
	
	/** The sms text. */
	private String data;
	
	/** The error message as reported by the xforms sms parser. */
	private String errorMsg;
	
	
	/**
	 * Constructs a new sms form error object.
	 */
	public FormSmsError(){
		
	}
	
	/**
	 * Constructs a new sms form error object with the following parameters.
	 * 
	 * @param sender the sender's phone number.
	 * @param data the sms text.
	 * @param dateCreated the date when the sms was received by the server.
	 * @param creator the user who sent the sms.
	 * @param errorMsg the error message encountered during the processing or validation of the sms.
	 */
	public FormSmsError(String sender, String data, Date dateCreated, User creator, String errorMsg){
		this.sender =  sender;
		this.data = data;
		this.dateCreated = dateCreated;
		this.creator = creator;
		this.errorMsg = errorMsg;
	}
	
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public int getFormSmsErrorId() {
		return formSmsErrorId;
	}

	@Override
	public int getId() {
		return formSmsErrorId;
	}
	
	public void setFormSmsErrorId(int formSmsErrorId) {
		this.formSmsErrorId = formSmsErrorId;
	}
}
