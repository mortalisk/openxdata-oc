package org.openxdata.server.admin.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * This class encapsulates all form definitions of a particular study.
 */
public class StudyDef extends AbstractEditable{
	
	private static final long serialVersionUID = -8072038229430076563L;

	private String name;
	
	private String description;
	
	private String studyKey;
	
	//Assuming the number of studies will not exceed 127.
	/** The numeric identifier of the study. */
	private int studyId = 0;
	
	/** A list of form definitions (FormDef) in the the study. */
	private List<FormDef> forms;
	
	private Boolean retired = false;
	private User retiredBy;
	private Date dateRetired;
	private String retiredReason;
	
	/** A list of the study text for different locales. */
	private List<StudyDefText> text;
	
	/** A list of users who have permission to work on this study */
	private List<User> users;
		
	public StudyDef() {
	}
	
	/** Copy constructor. */
	public StudyDef(StudyDef studyDef) {
		this(studyDef.getStudyId(),studyDef.getName());
		copyForms(studyDef.getForms());
	}
	
	public StudyDef(int studyId, String name) {
		setStudyId(studyId);
		setName(name);
	}
	
	public StudyDef(Integer studyId,String name, String description) {
		this(studyId,name);
		setDescription(description);
	}

	public List<FormDef> getForms() {
		return forms;
	}

	public void setForms(List<FormDef> forms) {
		this.forms = forms;
	}

	public int getStudyId() {
		return studyId;
	}

	@Override
	public int getId() {
		return studyId;
	}
	
	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	public String getStudyKey() {
		return studyKey;
	}

	public void setStudyKey(String studyKey) {
		this.studyKey = studyKey;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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

	public FormDef getFormAt(byte index){
		return forms.get(index);
	}
	
	public List<StudyDefText> getText() {
		return text;
	}

	public void setText(List<StudyDefText> text) {
		this.text = text;
	}

	public void addForm(FormDef formDef){
		if(forms == null)
			forms = new Vector<FormDef>();
		forms.add(formDef);
	}
	
	public void addForms(List<FormDef> formList){
		if(formList != null){
			if(forms == null)
				forms = formList;
			else{
				for(byte i=0; i<formList.size(); i++ )
					forms.add(formList.get(i));
			}
		}
	}
	
	/**
	 * Gets a form definition with a given numeric identifier.
	 * 
	 * @param formId - the numeric identifier.
	 * @return - the form definition.
	 */
	public FormDef getForm(Integer formId){
		for(byte i=0; i<forms.size(); i++){
			FormDef def = (FormDef)forms.get(i);
			if(def.getFormId() == formId)
				return def;
		}
		
		return null;
	}

	@Override
	public String toString() {
		return getName();
	}
	
	private void copyForms(List<FormDef> forms){
		this.forms = new Vector<FormDef>();
		for(byte i=0; i<forms.size(); i++)
			forms.add(new FormDef(forms.get(i)));
	}
	
	public void removeForm(FormDef formDef){
		forms.remove(formDef);
	}
	
	@Override
	public boolean isDirty() {
		if(dirty)
			return true;
		
		if(forms == null)
			return false;
		
		for(FormDef form : forms){
			if(form.isDirty())
				return true;
		}
		
		return false;
	}

	@Override
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
		
		if(forms == null)
			return;
		
		for(FormDef form : forms)
			form.setDirty(dirty);
	}
	
	@Override
	public boolean isNew(){
		if(studyId == 0)
			return true;
		
		if(forms == null)
			return false;
		
		for(FormDef form : forms){
			if(form.isNew())
				return true;
		}
		
		return false;
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
