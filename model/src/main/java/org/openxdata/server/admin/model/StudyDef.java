package org.openxdata.server.admin.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * This class encapsulates all form definitions of a particular study.
 */
public class StudyDef extends AbstractEditable implements Exportable {
	
	private static final long serialVersionUID = -8072038229430076563L;

	private String name;
	
	private String description;
	
	private String studyKey;
		
	/** A list of form definitions (FormDef) in the the study. */
	private List<FormDef> forms;
	
	/** A list of the study text for different locales. */
	private List<StudyDefText> text;
	
	/** A list of users who have permission to this study */
	private List<User> users;
		
	public StudyDef() {
	}
	
	/** Copy constructor. */
	public StudyDef(StudyDef studyDef) {
		this(studyDef.getId(), studyDef.getName());
		copyForms(studyDef.getForms());
	}
	
	public StudyDef(int studyId, String name) {
		setId(studyId);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}

	public String getStudyKey() {
		return studyKey;
	}

	public void setStudyKey(String studyKey) {
		this.studyKey = studyKey;
	}

	public void setDescription(String description) {
		this.description = description;
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
	 * @param formId the numeric identifier.
	 * @return the form definition.
	 */
	public FormDef getForm(Integer formId){
		for(byte i=0; i<forms.size(); i++){
			FormDef def = (FormDef)forms.get(i);
			if(def.getId() == formId)
				return def;
		}
		
		return null;
	}
	
	/**
	 * Gets a form definition with the given name
	 * @param formName String name of the form
	 * @return FormDef (or null if not found)
	 */
	public FormDef getForm(String formName) {
		for (byte i=0; i<forms.size(); i++) {
			FormDef def = (FormDef)forms.get(i);
			if (def.getName().equals(formName)) {
				return def;
			}
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
		if(id == 0)
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
    
    public void removeUser(User user) {
        if (this.users != null) {
            this.users.remove(user);
        }
    }

	@Override
	public String getType() {
		return "study";
	}
}
