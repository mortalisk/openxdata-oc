package org.openxdata.server.admin.model;

import java.util.Date;

/**
 * This class hold data which has been collected for a form.
 */
public class FormData extends AbstractEditable {

	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = 6183444597939766009L;

	/** The numeric unique identifier of the form data. */
	private int formDataId = 0;

	/**
	 * The numeric unique identifier for the version of the form that this data
	 * belongs to.
	 */
	private Integer formDefVersionId;

	/** The xml data. */
	private String data;

	/**
	 * Description of the form data. This is normally used to tell which form
	 * has which data without having to first open them one by one and check the
	 * data.
	 */
	private String description;

	/** Bitwise flag to indicate if the data has been exported. */
	private Integer exported = 0;

	/**
	 * Constructs a new form data object.
	 */
	public FormData() {

	}

	/**
	 * Creates a new copy of the form data object from another instance.
	 * 
	 * @param formData
	 *            the form data instance to copy from.
	 */
	public FormData(FormData formData) {
		this.formDefVersionId = formData.formDefVersionId;
		this.data = formData.data;
		this.description = formData.description;
		this.dateCreated = formData.dateCreated;
		this.creator = formData.creator;
		this.changedBy = formData.changedBy;
		this.dateChanged = formData.dateChanged;
	}

	public FormData(Integer formDefVersionId, String data, String description, Date dateCreated,
			User creator) {
		this.formDefVersionId = formDefVersionId;
		this.data = data;
		this.description = description;
		this.dateCreated = dateCreated;
		this.creator = creator;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getFormDataId() {
		return formDataId;
	}

	@Override
	public int getId() {
		return formDataId;
	}

	public void setFormDataId(int formDataId) {
		this.formDataId = formDataId;
	}

	public Integer getFormDefVersionId() {
		return formDefVersionId;
	}

	public void setFormDefVersionId(Integer formDefVersionId) {
		this.formDefVersionId = formDefVersionId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Determines if a particular data export task has been executed on this
	 * formData.
	 * 
	 * Use the flagBit to indicate the exporter's bit For the main exporter:
	 * flagBit=1 (000...00000001) Second exporter: flagBit=2 (000...00000010)
	 * Third exporter: flagBit=4 (000...00000100) Fourth exporter: flagBit=16
	 * (000...00001000) nth exporter: flagBit=2^n (two to the power of n)
	 * 
	 * @param flagBit
	 *            Integer
	 * @return Boolean true if the specified exporter has been run
	 */
	public Boolean isExported(Integer flagBit) {
		if ((this.exported & flagBit) == flagBit) {
			// flag is set or turned on
			return true;
		} else {
			// flag is not set or is turned off
			return false;
		}
	}

	/**
	 * Sets that a particular data export task has been executed on this
	 * formData.
	 * 
	 * Use the flagBit to indicate the bit being turned on. For the main
	 * exporter: flagBit=1 (000...00000001) Second exporter: flagBit=2
	 * (000...00000010) Third exporter: flagBit=4 (000...00000100) Fourth
	 * exporter: flagBit=16 (000...00001000) nth exporter: flagBit=2^n (two to
	 * the power of n)
	 * 
	 * @param flagBit
	 *            Integer
	 * @return
	 */
	public void setExportedFlag(Integer flagBit) {
		this.exported = this.exported | flagBit;
	}

	/**
	 * Resets the exported flag, so data can be updated in the exported tables
	 */
	public void resetExportedFlag() {
		this.exported = 0;
	}

	/**
	 * Use isExported to determine if the export has been executed for a
	 * particular exporter flag
	 * 
	 * @return
	 */
	public Integer getExported() {
		return exported;
	}

	/**
	 * Use setExportedFlag to set the exported flag for a particular exporter
	 * 
	 * @param exported
	 */
	public void setExported(Integer exported) {
		this.exported = exported;
	}
}