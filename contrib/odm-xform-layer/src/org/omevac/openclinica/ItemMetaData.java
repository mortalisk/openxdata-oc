package org.omevac.openclinica;

/**
 * Holds metadata (text,type, etc) of a question item.
 * 
 * @author daniel
 *
 */
public class ItemMetaData {
	
	private String leftItemText;
	private int itemDataTypeId;
	private String optionsText;
	private String optionsValues;
	private int responseTypeId;
	private String regexpErrorMsg;
	private int pageNo;
	private String pageTitle;
	private boolean required;
	private String defaultValue;
	
	public ItemMetaData(){
		
	}
	
	public ItemMetaData(String leftItemText, int itemDataTypeId, String optionsText, String optionsValues, int responseTypeId, String regexpErrorMsg, int pageNo, String pageTitle, boolean required, String defaultValue) {
		super();
		this.leftItemText = leftItemText;
		this.itemDataTypeId = itemDataTypeId;
		this.optionsText = optionsText;
		this.optionsValues = optionsValues;
		this.responseTypeId = responseTypeId;
		this.regexpErrorMsg = regexpErrorMsg;
		this.pageNo = pageNo;
		this.pageTitle = pageTitle;
		this.required = required;
		this.defaultValue = defaultValue;
	}
	
	public int getItemDataTypeId() {
		return itemDataTypeId;
	}
	public void setItemDataTypeId(int itemDataTypeId) {
		this.itemDataTypeId = itemDataTypeId;
	}
	public String getLeftItemText() {
		return leftItemText;
	}
	public void setLeftItemText(String leftItemText) {
		this.leftItemText = leftItemText;
	}
	public String getOptionsText() {
		return optionsText;
	}
	public void setOptionsText(String optionsText) {
		this.optionsText = optionsText;
	}
	public String getOptionsValues() {
		return optionsValues;
	}
	public void setOptionsValues(String optionsValues) {
		this.optionsValues = optionsValues;
	}
	public void setRegExpErrorMsg(String regexpErrorMsg) {
		this.regexpErrorMsg = regexpErrorMsg;
	}
	public String getRegExpErrorMsg() {
		return regexpErrorMsg;
	}
	public int getResponseTypeId() {
		return responseTypeId;
	}
	public void setResponseTypeId(int responseTypeId) {
		this.responseTypeId = responseTypeId;
	}
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}
	public String getPageTitle() {
		return pageTitle;
	}
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}
