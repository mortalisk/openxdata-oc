package org.openxdata.server.admin.model;

import java.util.List;
import java.util.Vector;

/**
 * The form definition version. For each form defined, we can have many versions to support
 * changing of form definition without breaking already collected data.
 */
public class FormDefVersion extends AbstractEditable implements Exportable {

	private static final long serialVersionUID = 3882276404608627490L;

	/** The display name of the form version. */
	private String name;
	
	/** Description of the form version. */
	private String description;
		
	/** The form definition whose version we represent. */
	private FormDef formDef;
	
	private String xform;
	private String layout;
	private String javaScriptSrc;

	private Boolean isDefault = true;
	
	/** A list of the form text for different locales. */
	private List<FormDefVersionText> versionText;
	
	public FormDefVersion() {
	}
	
	public FormDefVersion(int versionId, String name,FormDef formDef){
		this.id = versionId;
		this.name = name;
		this.formDef = formDef;
	}
	
	public FormDefVersion(int versionId, String name, String description,FormDef formDef) {
		this(versionId,name,formDef);
		setDescription(description);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	
	public void setJavaScriptSrc(String javaScriptSrc) {
		this.javaScriptSrc = javaScriptSrc;
	}

	public String getJavaScriptSrc() {
		return javaScriptSrc;
	}
	
	@Override
	public boolean isNew(){
		
		if(id == 0)
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

	@Override
	public String getType() {
		return "version";
	}
}
