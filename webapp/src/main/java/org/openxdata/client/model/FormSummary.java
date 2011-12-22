package org.openxdata.client.model;

import java.util.Date;

import org.openxdata.client.AppMessages;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefHeader;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.StudyDef;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.core.client.GWT;

public class FormSummary extends BaseModel {

    private static final long serialVersionUID = 3037791298938446908L;
    private final AppMessages appMessages = GWT.create(AppMessages.class);
    
    private FormDef formDef;
    private FormDefVersion formVersion;
	private FormDefHeader formDefHeader;

    public FormSummary() {
	}
    
    public FormSummary(FormDef formDef) {
    	setFormDef(formDef);
    }
    
    public FormSummary(FormDefVersion formDefVersion) {
        setFormVersion(formDefVersion);
    }
    
	public FormSummary(FormDefHeader formDefHeader) {
		setFormDefHeader(formDefHeader);
	}
	
    public FormSummary(String id, String form) {
    	setId(id);
    	setForm(form);
    	setName(form);
    }
	
	public FormSummary(String id, String form, String version, String status, String organisation, String creator, Date changed, String responses) {
		setId(id);
		setName(form);
	    setForm(form);
	    setVersion(version);
	    setStatus(status);
	    setOrganisation(organisation);
	    setCreator(creator);
	    setChanged(changed);
	    setResponses(responses);
	}

	public String getId() {
		return get("id");
	}

	public void setId(String id) {
		set("id", id);
	}

	public String getForm() {
		return get("form");
	}

	public void setForm(String form) {
		set("form", form);
	}
	
	public String getName() {
		return get("name");
	}
	
	public String getFormVersionName() {
		return getForm() + " (" + getVersion() + ")";
	}
	
	public void setName(String name) {
		set("name", name);
	}

	public String getStatus() {
		return get("status");
	}

	public void setStatus(String status) {
		set("status", status);
	}

	public String getOrganisation() {
		return get("organisation");
	}

	public void setOrganisation(String organisation) {
		set("organisation", organisation);
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

	public String getResponses() {
		return get("responses");
	}

	public void setResponses(String responses) {
		set("responses", responses);
	}

	public String getVersion() {
		return get("version");
	}

	public void setVersion(String version) {
		set("version", version);
	}
	  
	public void setPublished(Boolean published) {
		set("published", published);
	}

	public Boolean isPublished() {
		return get("published");
	}

	public FormDef getFormDefinition() {
		return formDef;
	}

	public FormDefVersion getFormVersion() {
		return formVersion;
	}

	public FormDefHeader getFormDefHeader() {
		return formDefHeader;
	}

	public void setFormVersion(FormDefVersion formVersion) {
		this.formVersion = formVersion;
		this.formDef = formVersion.getFormDef();
		updateFormVersion(formVersion);
	}

	public void setFormDef(FormDef formDef) {
		this.formDef = formDef;
		updateFormDefinition(formDef);
	}

	public void setFormDefHeader(FormDefHeader formDefHeader) {
		this.formDefHeader = formDefHeader;
		updateFormDefHeader(formDefHeader);
	}

	public void updateFormDefinition(FormDef formDef) {
		setId(String.valueOf("d" + formDef.getId()));
		setForm(formDef.getName());
		setName(formDef.getName());
		// newly created forms may not have any version
		FormDefVersion formVersion = formDef.getDefaultVersion();
		if (formVersion != null) {
			setVersion(formVersion.getName());
			setPublished(true);
		} else {
			setPublished(false);
		}
		StudyDef study = formDef.getStudy();
		setOrganisation(study.getName());
		setCreator(formDef.getCreator().getName());
		if (formDef.getDateChanged() == null) {
			setChanged(formDef.getDateCreated());
		}
	}

	public void updateFormVersion(FormDefVersion formVersion) {
		setId(String.valueOf(formVersion.getId()));
		FormDef formDef = formVersion.getFormDef();
		setForm(formDef.getName());
		setName(formDef.getName());
		setVersion(formVersion.getName());
		setPublished(formVersion.getIsDefault());
		StudyDef study = formDef.getStudy();
		setOrganisation(study.getName());
		setCreator(formDef.getCreator().getName());
		if (formDef.getDateChanged() == null) {
			setChanged(formDef.getDateCreated());
		}
	}
	
	public void updateFormDefHeader(FormDefHeader formDefHeader) {
		setId(String.valueOf(formDefHeader.getId()));
		setForm(formDefHeader.getName());
		if (formDefHeader.isStudyAccess()) {
			setName(formDefHeader.getName() + " ("+appMessages.studyAccess()+")");
			set("disabled", true);
		} else {
			setName(formDefHeader.getName());
			set("disabled", false);
		}
	}
}
