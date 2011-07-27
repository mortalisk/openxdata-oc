package org.openxdata.server;

import java.util.List;
import java.util.Map;

import org.openxdata.server.admin.client.service.FormService;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.ExportedFormDataList;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.exception.ExportedDataNotFoundException;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.rpc.OxdPersistentRemoteService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class FormServiceImpl extends OxdPersistentRemoteService implements FormService {

    private static final long serialVersionUID = 2126615349350883086L;
    private org.openxdata.server.service.FormService formService;
	
    @Override
	public List<FormData> getFormData(int formId) {
        return getFormService().getFormData(formId);
    }

    @Override
	public Integer getFormResponseCount(int formId) {
        return getFormService().getFormResponseCount(formId);
    }

    @Override
	public List<FormDef> getForms() {
        return getFormService().getForms();
    }

    @Override
	public FormData saveFormData(FormData formData) {
        return getFormService().saveFormData(formData);
    }
    
    @Override
	public List<FormDef> getFormsForCurrentUser() {
        return getFormService().getFormsForCurrentUser();
    }
    
    @Override
    public Map<Integer, String> getFormNamesForCurrentUser(Integer studyId) {
    	return getFormService().getFormNamesForCurrentUser(studyId);
    }

    @Override
    public List<UserFormMap> getUserMappedForms() {
        return getFormService().getUserMappedForms();
    }

	@Override
    public List<UserFormMap> getUserMappedForms(Integer formId)
            throws OpenXDataSecurityException {
	    return getFormService().getUserMappedForms(formId);
    }

    @Override
    public void saveUserMappedForm(UserFormMap map) {
        getFormService().saveUserMappedForm(map);
    }

    @Override
	public  ExportedFormDataList getFormDataList(String formBinding, String[] questionBindings, int offset, int limit, String sortField, boolean ascending) throws ExportedDataNotFoundException {
        return getFormService().getFormDataList(formBinding, questionBindings, offset, limit, sortField, ascending);
    }

    @Override
    public void deleteUserMappedForm(UserFormMap map) {
        getFormService().deleteUserMappedForm(map);
    }
    @Override
    public FormDef getForm(int formId) {
    	return getFormService().getForm(formId);
    }
    @Override
    public Boolean  hasEditableData(Editable item) {
        return getFormService().hasEditableData(item);
    }
    private org.openxdata.server.service.FormService getFormService() {
        if (formService == null) {
            WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(this.getServletContext());
            formService = (org.openxdata.server.service.FormService) ctx.getBean("formService");
        }
        return formService;
    }
}