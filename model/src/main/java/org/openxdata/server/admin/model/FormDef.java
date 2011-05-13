package org.openxdata.server.admin.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Definition of a form. This has some meta data about the form definition and  
 * a collection of pages together with question branching or skipping rules.
 * A form is sent as defined in one language. For instance, those using
 * Swahili would get forms in that language, etc. We don't support runtime
 * changing of a form language in order to have a more efficient implementation
 * as a trade off for more flexibility which may not be used most of the times.
 */
public class FormDef extends AbstractEditable{

	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = -2422751217356938584L;

	/** The display name of the form. */
	private String name;
	
	/** Description of the form. */
	private String description;
	
	/** The numeric unique identifier of the form definition. */
	private int formId = 0;
	
	/** The study to which the form is attached. */
	private StudyDef study;
	
	/** A list of the form versions. */
	private List<FormDefVersion> versions;
	
	/** A list of users who have permission to work on this form */
	private List<User> users;
	
	private Boolean retired = false;
	private User retiredBy;
	private Date dateRetired;
	private String retiredReason;
	
	/** A list of the study text for different locales. */
	private List<FormDefText> text;
	
	
	/** Constructs a form definition object. */
	public FormDef() {

	}
	
	/**
	 * Creates a new copy of the form definition object from an existing one.
	 * 
	 * @param formDef the form definition to copy.
	 */
	public FormDef(FormDef formDef) {
		setFormId(formDef.getFormId());
		setName(formDef.getName());
	}
	
	/**
	 * Constructs a new form definition object from the parameters.
	 * 
	 * @param formId the database identifier of the form definition.
	 * @param name the name of the form.
	 * @param description the description of the form.
	 * @param study the study to which the form belongs. 
	 */
	public FormDef(Integer formId,String name, String description,StudyDef study) {
		this(formId,name,study);
		setDescription(description);
	}
	
	/**
	 * Constructs a form definition object from these parameters.
	 * 
	 * @param formId the database identifier of the form definition.
	 * @param name the name of the form.
	 * @param study the study to which the form belongs. 
	 */
	public FormDef(int formId, String name,StudyDef study) {
		setFormId(formId);
		setName(name);	
		setStudy(study);
	}
	
	/**
	 * Constructs a new form definition object from these parameters.
	 * 
	 * @param formId the database identifier of the form definition.
	 * @param name the name of the form.
	 * @param versions a list of versions in the form.
	 */
	public FormDef(int formId, String name,List<FormDefVersion> versions) {
		setFormId(formId);
		setName(name);	
		setVersions(versions);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getFormId() {
		return formId;
	}
	
	@Override
	public int getId() {
		return formId;
	}

	public void setFormId(int formId) {
		this.formId = formId;
	}
	
	@Override
	public String toString() {
		return getName();
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

	public StudyDef getStudy() {
		return study;
	}

	public void setStudy(StudyDef study) {
		this.study = study;
	}

	/**
	 * @return the versions
	 */
	public List<FormDefVersion> getVersions() {
		return versions;
	}

	/**
	 * @param versions the versions to set
	 */
	public void setVersions(List<FormDefVersion> versions) {
		this.versions = versions;
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

	public void setRetiredReason(String voidReason) {
		this.retiredReason = voidReason;
	}
	
	public List<FormDefText> getText() {
		return text;
	}

	public void setText(List<FormDefText> text) {
		this.text = text;
	}

	public void addVersion(FormDefVersion formDefVersion){
		if(versions == null)
			versions = new Vector<FormDefVersion>();
		versions.add(formDefVersion);
	}
	
	public void removeVersion(FormDefVersion formDefVersion){
		versions.remove(formDefVersion);
		
		int size = versions.size();
		if(formDefVersion.getIsDefault() &&  size > 0)
			versions.get(size-1).setIsDefault(true); //Atleast one version should be the default
	}
	
	@Override
	public boolean isDirty() {
		if(dirty)
			return true;
		
		if(versions == null)
			return false;
		
		for(FormDefVersion version : versions){
			if(version.isDirty())
				return true;
		}
		
		return false;
	}

	@Override
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
		
		if(versions == null)
			return;
		
		for(FormDefVersion version : versions)
			version.setDirty(dirty);
	}
	
	@Override
	public boolean isNew(){
		if(formId == 0)
			return true;
		
		if(versions == null)
			return false;
		
		for(FormDefVersion version : versions){
			if(version.isNew())
				return true;
		}
		
		return false;
	}
	
	/**
	 * Turns off other default versions of the form, if any, apart from the one given.
	 * 
	 * @param formDefVersion the form version to set as the only default.
	 */
	public void turnOffOtherDefaults(FormDefVersion formDefVersion){	
		if(versions == null)
			return;
		
		for(FormDefVersion version : versions){
			if(version != formDefVersion)
				version.setIsDefault(false);
		}
	}
	
	/**
	 * Returns the version of the form that is marked default
	 * @return FormDefVersion, or null if no versions or no default found
	 */
    public FormDefVersion getDefaultVersion() {
        if (versions != null) {
            for (FormDefVersion fdv : versions) {
                if (fdv.getIsDefault()) {
                    return fdv;
                }
            }
        }
        return null;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
    
    public void addUser(User user) {
        if (this.users == null) {
            this.users = new ArrayList<User>();
        }
        this.users.add(user);
    }
}
