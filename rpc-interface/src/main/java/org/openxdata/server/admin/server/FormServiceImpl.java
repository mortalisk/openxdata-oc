package org.openxdata.server.admin.server;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.ExportedFormData;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefHeader;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.ExportedDataNotFoundException;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;
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
	
	@Override
    public void deleteFormData(FormData formData) throws OpenXDataSecurityException {
	    formService.deleteFormData(formData);
    }
	
	@Override
    public PagingLoadResult<FormDef> getForms(User user, PagingLoadConfig loadConfig) throws OpenXDataSecurityException {
	    return formService.getForms(user, loadConfig);
    }
	
	@Override
    public PagingLoadResult<FormDefVersion> getFormVersions(User user, PagingLoadConfig loadConfig) throws OpenXDataSecurityException {
	    return formService.getFormVersions(user, loadConfig);
    }

    @Override
	public Integer getFormResponseCount(int formDefVersionId) {
		return formService.getFormResponseCount(formDefVersionId);
	}

    @Override
	public PagingLoadResult<ExportedFormData> getFormDataList(String formBinding,
			String[] questionBindings, PagingLoadConfig  pagingLoadConfig) throws ExportedDataNotFoundException {
		
		return formService.getFormDataList(formBinding, questionBindings, pagingLoadConfig);
	}

	@Override
	public FormDef getForm(int formId) {
		return formService.getForm(formId);
	}

    @Override
    public Boolean hasEditableData(Editable item) {
        return formService.hasEditableData(item);
    }

	@Override
    public Map<Integer, String> getFormNamesForCurrentUser(Integer studyId)
            throws OpenXDataSecurityException {
        return formService.getFormNamesForCurrentUser(studyId);
    }

	@Override
    public PagingLoadResult<User> getMappedUsers(Integer formId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException {
	    return formService.getMappedUsers(formId, loadConfig);
    }

	@Override
    public PagingLoadResult<User> getUnmappedUsers(Integer formId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException {
	    return formService.getUnmappedUsers(formId, loadConfig);
    }

	@Override
    public void saveMappedFormUsers(Integer formId, List<User> usersToAdd, List<User> usersToDelete) throws OpenXDataSecurityException {
	    formService.saveMappedFormUsers(formId, usersToAdd, usersToDelete);
    }

	@Override
    public PagingLoadResult<FormDef> getMappedForms(Integer userId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException {
	    return formService.getMappedForms(userId, loadConfig);
    }

	@Override
	public PagingLoadResult<FormDefHeader> getMappedFormNames(Integer userId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException {
		return formService.getMappedFormNames(userId, loadConfig);
	}

	@Override
	public PagingLoadResult<FormDefHeader> getUnmappedFormNames(Integer userId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException {
		return formService.getUnmappedFormNames(userId, loadConfig);
	}

	@Override
	public void saveMappedUserFormNames(Integer userId, List<FormDefHeader> formsToAdd, List<FormDefHeader> formsToDelete) throws OpenXDataSecurityException {
		formService.saveMappedUserFormNames(userId, formsToAdd, formsToDelete);
	}
}
