package org.openxdata.server.admin.model;

import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * The form definition version. For each form defined, we can have many versions to support
 * changing of form definition without breaking already collected data.
 */
public class FormDefVersion extends AbstractEditable{

	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = 3882276404608627490L;

	/** The display name of the form version. */
	private String name;
	
	/** Description of the form version. */
	private String description;
	
	/** The numeric unique identifier of the form definition version. */
	private int formDefVersionId = 0;
	
	/** The form definition whose version we represent. */
	private FormDef formDef;
	
	private Boolean retired = false;
	private User retiredBy;
	private Date dateRetired;
	private String retiredReason;
	
	private String xform;
	private String layout;
	private Boolean isDefault = true;
	
	/** A list of the form text for different locales. */
	private List<FormDefVersionText> versionText;
	
	public FormDefVersion() {
	}
	
	public FormDefVersion(int versionId, String name,FormDef formDef){
		this.formDefVersionId = versionId;
		this.name = name;
		this.formDef = formDef;
	}
	
	public FormDefVersion(FormDefVersion formDef) {
		setFormDefVersionId(formDef.getFormDefVersionId());
		setName(formDef.getName());
		setDescription(formDef.getDescription());
	}
	
	public FormDefVersion(int versionId, String name, String description,FormDef formDef) {
		this(versionId,name,formDef);
		setDescription(description);
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return returns the id
	 */
	public int getFormDefVersionId() {
		return formDefVersionId;
	}
	
	@Override
	public int getId() {
		return formDefVersionId;
	}

	/**
	 * @param formDefVersionId the id to set
	 */
	public void setFormDefVersionId(int formDefVersionId) {
		this.formDefVersionId = formDefVersionId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public Date getDateRetired() {
		return dateRetired;
	}

	public void setDateRetired(Date dateRetired) {
		this.dateRetired = dateRetired;
	}

	public Boolean getRetired() {
		return retired;
	}

	public void setRetired(Boolean retired) {
		this.retired = retired;
	}

	public User getRetiredBy() {
		return retiredBy;
	}

	public void setRetiredBy(User retiredBy) {
		this.retiredBy = retiredBy;
	}

	public String getRetiredReason() {
		return retiredReason;
	}

	public void setRetiredReason(String retiredReason) {
		this.retiredReason = retiredReason;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public String getXform() {
		return xform;
	}

	public void setXform(String xform) {
		this.xform = xform;
	}

	public FormDef getFormDef() {
		return formDef;
	}

	public void setFormDef(FormDef formDef) {
		this.formDef = formDef;
	}

	/**
	 * @return the isDefault
	 */
	public boolean getIsDefault() {
		return isDefault;
	}

	/**
	 * @param isDefault the isDefault to set
	 */
	public void setIsDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	
	@Override
	public boolean isNew(){
		
		if(formDefVersionId == 0)
			return true;
		
		if(versionText == null)
			return false;
		
		for(FormDefVersionText text : versionText){
			if(text.isNew())
				return true;
		}
		
		return false;
	}
	
	@Override
	public boolean isDirty() {
		if(super.isDirty())
			return true;
		
		if(versionText == null)
			return false;
		
		for(FormDefVersionText text : versionText){
			if(text.isDirty())
				return true;
		}
		
		return false;
	}
	
	@Override
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
		
		if(versionText == null)
			return;
		
		for(FormDefVersionText text : versionText)
			text.setDirty(dirty);
	}
	
	public List<FormDefVersionText> getVersionText() {
		return versionText;
	}

	public void setVersionText(List<FormDefVersionText> versionText) {
		this.versionText = versionText;
	}
	
	public void addVersionText(FormDefVersionText formDefVersionText){
		if(versionText == null)
			versionText = new Vector<FormDefVersionText>();
		versionText.add(formDefVersionText);
	}
	
	public void removeVersionText(FormDefVersionText formDefVersionText){
		versionText.remove(formDefVersionText);
	}
	
	public FormDefVersionText getFormDefVersionText(String locale){
		if(versionText == null)
			return null;
		
		for(FormDefVersionText text : versionText){
			if(locale.equalsIgnoreCase(text.getLocaleKey()))
				return text;
		}
		
		return null;
	}
}
