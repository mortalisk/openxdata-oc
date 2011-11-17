package org.motechproject.proto;

import java.io.Serializable;

/**
 * Stores form data uploaded from the mobile phone which is then saved or updated in the database.
 */
public class UploadData implements Serializable {

	private static final long serialVersionUID = -7849069327015946679L;

	/** formData Identifier (or Session Reference), used for data that is being updated. Is null when new data is being processed */
	Integer dataId;
	/** XML containing the submitted form data */
	String xml;
	
	/**
	 * Creates modified UploadData (data that has been previously submitted)
	 * @param dataId Integer data identifier, can be null for new data
	 * @param xml String
	 */
	public UploadData(Integer dataId, String xml) {
		this.dataId = dataId;
		this.xml = xml;
	}
	
	/**
	 * Creates new UploadData (data that has not been previously submitted)
	 * @param xml String
	 */
	public UploadData(String xml) {
		this.xml = xml;
	}
	
	public Integer getDataId() {
		return dataId;
	}
	
	public void setDataId(Integer formDataId) {
		this.dataId = formDataId;
	}
	
	public String getXml() {
		return xml;
	}
	
	public void setXml(String xml) {
		this.xml = xml;
	}
}
