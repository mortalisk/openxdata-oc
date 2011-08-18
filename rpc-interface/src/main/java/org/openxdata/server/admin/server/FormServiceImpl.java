package org.openxdata.server.admin.server;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import org.openxdata.server.admin.model.Editable;

import org.openxdata.server.admin.model.ExportedFormDataList;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.exception.ExportedDataNotFoundException;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.rpc.OxdPersistentRemoteService;
import org.springframework.web.context.WebApplicationContext;

/**
 * Default Implementation for the <code>PermissionService Interface.</code>
 */
public class FormServiceImpl extends OxdPersistentRemoteService implements
	org.openxdata.server.admin.client.service.FormService {

	/**
	 * Generated serialization ID
	 */
	private static final long serialVersionUID = -5786495516328035348L;
	
	private org.openxdata.server.service.FormService formService;

	@Override
	public void init() throws ServletException {
		super.init();
		WebApplicationContext ctx = getApplicationContext();
		formService = (org.openxdata.server.service.FormService)ctx.getBean("formService");
	}

	public FormData saveFormData(FormData formData) {
		return formService.saveFormData(formData);
	}

	public List<FormDef> getForms() {
		return formService.getForms();
	}

	public List<FormDef> getFormsForCurrentUser() {
		return formService.getFormsForCurrentUser();
	}

    @Override
    public List<UserFormMap> getUserMappedForms() {
        return formService.getUserMappedForms();
    }

	@Override
	public void saveUserMappedForm(UserFormMap map) {
		formService.saveUserMappedForm(map);
	}
        @Override
        public void deleteUserMappedForm(UserFormMap map) {
            formService.deleteUserMappedForm(map);
        }
    @Override
	public Integer getFormResponseCount(int formDefVersionId) {
		return formService.getFormResponseCount(formDefVersionId);
	}

    @Override
	public List<FormData> getFormData(int formDefVersionId) {
		return formService.getFormData(formDefVersionId);
	}

    @Override
	public ExportedFormDataList getFormDataList(String formBinding,
			String[] questionBindings, int offset, int limit, String sortField,
			boolean ascending) throws ExportedDataNotFoundException {
		
		return formService.getFormDataList(formBinding, questionBindings, offset, limit, sortField, ascending);
	}

	@Override
	public FormDef getForm(int formId) {
		return formService.getForm(formId);
	}

    @Override
    public Boolean  hasEditableData(Editable item) {
        return formService.hasEditableData(item);
    }

	@Override
    public Map<Integer, String> getFormNamesForCurrentUser(Integer studyId)
            throws OpenXDataSecurityException {
        return formService.getFormNamesForCurrentUser(studyId);
    }

	@Override
	public List<UserFormMap> getUserMappedForms(Integer formId)
	        throws OpenXDataSecurityException {
	    return formService.getUserMappedForms(formId);
	}

}
