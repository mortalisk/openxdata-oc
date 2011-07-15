package org.openxdata.server.admin.model;

import java.util.ArrayList;
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
public class FormDef extends AbstractEditable implements Exportable {

	private static final long serialVersionUID = -2422751217356938584L;

	/** The display name of the form. */
	private String name;
	
	/** Description of the form. */
	private String description;
		
	/** The study to which the form is attached. */
	private StudyDef study;
	
	/** A list of the form versions. */
	private List<FormDefVersion> versions;
	
	/** A list of users who have permission to work on this form */
	private List<User> users;
	
	/** A list of the study text for different locales. */
	private List<FormDefText> text;
	
	public FormDef() {

	}
	
	/**
	 * Creates a new copy of the form definition object from an existing one.
	 * 
	 * @param formDef the form definition to copy.
	 */
	public FormDef(FormDef formDef) {
		setId(formDef.getId());
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
		setId(formId);
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
		setId(formId);
		setName(name);	
		setVersions(versions);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public StudyDef getStudy() {
		return study;
	}

	public void setStudy(StudyDef study) {
		this.study = study;
	}

	public List<FormDefVersion> getVersions() {
		return versions;
	}

	public void setVersions(List<FormDefVersion> versions) {
		this.versions = versions;
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
		if(id == 0)
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

	@Override
	public String getType() {
		return "form";
	}
}
