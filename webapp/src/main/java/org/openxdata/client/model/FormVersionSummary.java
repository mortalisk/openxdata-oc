package org.openxdata.client.model;

import com.extjs.gxt.ui.client.data.BaseModel;
import java.util.Date;
import org.openxdata.server.admin.model.FormDefVersion;

/**
 *
 * @author victor
 */
public class FormVersionSummary extends BaseModel {

	private static final long serialVersionUID = 8891120171475054905L;
	
	private FormDefVersion formDefVersion;

    public FormVersionSummary() {
    }

    public FormVersionSummary(FormDefVersion formDefVersion) {
        setFormVersionDefinition(formDefVersion);
    }

    public FormVersionSummary(String id, String formVersion) {
        setId(id);
        setFormVersion(formVersion);
    }

    public FormVersionSummary(String id, String formVersion, String description, String form, String creator, Date changed) {
        setId(id);
        setFormVersion(formVersion);
        setDescription(description);
        setForm(form);
        setCreator(creator);
        setChanged(changed);
    }

    public String getId() {
        return get("id");
    }

    public void setId(String id) {
        set("id", id);
    }

    public String getFormVersion() {
        return get("version");
    }

    public void setFormVersion(String formVersion) {
        set("version", formVersion);
    }

    public String getDescription() {
        return get("description");
    }

    public void setDescription(String description) {
        set("description", description);
    }

    public String getForm() {
        return get("form");
    }

    public void setForm(String form) {
        set("form", form);
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

    public FormDefVersion getFormVersionDef() {
        return formDefVersion;
    }

    public void setFormVersionDefinition(FormDefVersion version) {
        formDefVersion = version;
        updateFormVersion(version);
    }

    public void updateFormVersion(FormDefVersion version) {
        setId(String.valueOf(version.getId()));
        setForm(version.getFormDef().getName());
        setCreator(version.getCreator().getName());
        if (version.getDateChanged() == null) {
            setChanged(version.getDateCreated());
        } else {
            setChanged(version.getDateChanged());
        }
    }
}
