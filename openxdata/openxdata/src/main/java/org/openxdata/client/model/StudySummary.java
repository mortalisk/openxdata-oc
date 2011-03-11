package org.openxdata.client.model;

import java.util.Date;
import java.util.List;

import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.StudyDef;

import com.extjs.gxt.ui.client.data.BaseModel;

public class StudySummary extends BaseModel {

    private static final long serialVersionUID = 3037791298938446908L;
    
    private StudyDef studyDefinition;

    public StudySummary() {
	}
    
    public StudySummary(String id, String study) {
		setId(id);
	    setStudy(study);
    }
    
    public StudySummary(StudyDef studyDef) {
    	studyDefinition = studyDef;
    	setId(String.valueOf(studyDef.getId()));
    	setStudy(studyDef.getName());
	    setDescription(studyDef.getDescription());
	    setCreator(studyDef.getCreator().getName());
	    if (studyDef.getDateChanged() != null) {
	    	setChanged(studyDef.getDateChanged());
	    } else {
	    	setChanged(studyDef.getDateCreated());
	    }
	    setForms(studyDef.getForms());
    }
	
	public StudySummary(String id, String study, String description, String creator, Date changed, List<FormDef> forms) {
		setId(id);
	    setStudy(study);
	    setDescription(description);
	    setCreator(creator);
	    setChanged(changed);
	    setForms(forms);
	}	

	public String getId() {
		return get("id");
	}

	public void setId(String id) {
		set("id", id);
	}	

	public String getStudy() {
		return get("study");
	}

	public void setStudy(String study) {
	    set("study", study);
	}
	  
	public String getDescription() {
		return get("description");
	}

	public void setDescription(String description) {
		set("description", description);
	}

	public String getStatus() {
		return get("status");
	}

	public void setStatus(String status) {
		set("status", status);
	}

	public String getCreator() {
		return get("creator");
	}

	public void setCreator(String creator) {
		set("creator", creator);
	}

	public Date getChanged() {
		return get("changed");
	}

	public void setChanged(Date changed) {
		set("changed", changed);
	}

    public List<FormDef> getForms() {
    	return get("forms");
    }

    public void setForms(List<FormDef> forms) {
        set("forms", forms);
    }

	public StudyDef getStudyDefinition() {
		return studyDefinition;
	}

	public void setStudyDefinition(StudyDef studyDefinition) {
		this.studyDefinition = studyDefinition;
	}
}