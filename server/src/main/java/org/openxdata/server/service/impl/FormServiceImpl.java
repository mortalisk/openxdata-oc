package org.openxdata.server.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.exception.SQLGrammarException;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.ExportedFormData;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDataHeader;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.ExportedDataNotFoundException;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;
import org.openxdata.server.dao.EditableDAO;
import org.openxdata.server.dao.FormDAO;
import org.openxdata.server.dao.FormDataDAO;
import org.openxdata.server.dao.UserFormMapDAO;
import org.openxdata.server.export.rdbms.task.RdmsDataExportTask;
import org.openxdata.server.service.FormService;
import org.openxdata.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service("formService")
public class FormServiceImpl implements FormService {
	
	private Logger log = LoggerFactory.getLogger(FormServiceImpl.class);

    @Autowired
    private FormDAO formDAO;
    
    @Autowired
    private FormDataDAO formDataDAO;
    
    @Autowired
    private UserFormMapDAO userFormMapDAO;
    
    @Autowired
    private EditableDAO studyDAO;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RdmsDataExportTask exportTask;

    @Autowired
    private EditableDAO editableDAO;
    
    @Override
    @Secured("Perm_View_Forms")
	public Integer getFormResponseCount(int formDefVersionId) {
        return studyDAO.getFormDataCount(formDefVersionId);
    }
    
    @Override
	@Transactional(readOnly=true)
	@Secured("Perm_View_Forms")
    public PagingLoadResult<FormDef> getForms(User user, PagingLoadConfig loadConfig) {
        return formDAO.getForms(user, loadConfig);
    }

    @Override
	@Transactional(readOnly=true)
	@Secured("Perm_View_Forms")
    public PagingLoadResult<FormDef> getForms(PagingLoadConfig loadConfig) {
        return formDAO.getForms(loadConfig);
    }
    
    @Override
    @Transactional(readOnly=true)
	@Secured("Perm_View_Forms")
	public List<FormDef> getStudyForms(User user, Integer studyDefId) {
    	return formDAO.getStudyForms(user, studyDefId);
    }
    
    @Override
	@Transactional(readOnly=true)
	@Secured("Perm_View_Forms")
    public Map<Integer, String> getFormNamesForCurrentUser(Integer studyId) {
	    return userFormMapDAO.getFormNamesForUser(userService.getLoggedInUser(), studyId);
    }

	@Override
	@Secured("Perm_Delete_Forms")
	public void deleteForm(FormDef formDef) {
		formDAO.deleteForm(formDef);
	}
	
	@Override
	@Secured("Perm_Delete_Form_Data")
	public void deleteFormData(FormData formData){
		formDataDAO.saveFormDataVersion(formData, true);
		formDataDAO.deleteFormData(formData.getId());
		exportTask.deleteFormData(formData);
	}
	
	@Override
	@Secured("Perm_Add_Forms")
	public void saveForm(FormDef formDef) {
		formDAO.saveForm(formDef);
	}
	
    @Override
    @Secured("Perm_Add_Form_Data")
	public FormData saveFormData(FormData formData) {
        formData.setExported(0); // make sure the exported flag is reset
        if (formData.getId() != 0) {
        	formDataDAO.saveFormDataVersion(formData);
        }
        
        formDataDAO.saveFormData(formData);
        exportFormDataToRDMS(formData);
        return formData; // lucky this formData id is updated
    }

    @Override
    @Transactional(readOnly = true)
    @Secured("Perm_View_Forms")
    public Boolean hasEditableData(Editable item) {
        return editableDAO.hasEditableData(item);
    }

    private void exportFormDataToRDMS(FormData data) {
        exportTask.exportFormData(data);
    }

    @Override
    @Transactional(readOnly = true)
    @Secured("Perm_View_Form_Data")
	public FormData getFormData(Integer formDataId) {
		return formDataDAO.getFormData(formDataId);
	}
	
    @Override
	@Transactional(readOnly = true)
	@Secured("Perm_View_Form_Data")
    public List<FormData> getFormData(int formDefVersionId) {
        List<FormData> formData = new ArrayList<FormData>();
        List<FormDataHeader> headers = studyDAO.getFormData(formDefVersionId, null, null, null);
        for (FormDataHeader header : headers) {
            formData.add(formDataDAO.getFormData(header.getId()));
            log.debug("Loaded form data with id " + header.getId());
        }
        return formData;
    }
    
	@Override
	@Secured("Perm_View_Forms")
	public FormDef getForm(int formId) {
		return formDAO.getForm(formId);
	}

	@Override
	@Secured({"Perm_View_Forms", "Perm_View_Users"})
    public PagingLoadResult<User> getMappedUsers(Integer formId, PagingLoadConfig loadConfig) {
	    return formDAO.getMappedUsers(formId, loadConfig);
    }

	@Override
	@Secured({"Perm_View_Forms", "Perm_View_Users"})
    public PagingLoadResult<User> getUnmappedUsers(Integer formId, PagingLoadConfig loadConfig) {
	    return formDAO.getUnmappedUsers(formId, loadConfig);
    }

    @Override
    @Transactional(readOnly = true)
    @Secured("Perm_View_Form_Data")
	public PagingLoadResult<ExportedFormData> getFormDataList(String formBinding, String[] questionBindings,
			PagingLoadConfig pagingLoadConfig) throws ExportedDataNotFoundException {
        // find out the total size
    	BigInteger count = null;
    	try {
		    count = studyDAO.getNumberOfResponses(formBinding);
		    log.debug("total number of responses "+count+" for form "+formBinding);
    	} catch (SQLGrammarException e) {
    		// if this simple query fails then the exported data does not exist 
    		// (perhaps data exporter is turned off?)
    		log.warn("Could not find exported data in table '"+formBinding+"'", e);
    		throw new ExportedDataNotFoundException(formBinding);
    	}
	    
	    // create sql statement
        List<Object[]> data = studyDAO.getResponseData(formBinding, questionBindings, pagingLoadConfig);
        log.debug("loading exported form data. #items:"+data.size());
        
        // process results
        List<ExportedFormData> exportedFormData = getExportedFormData(questionBindings, data);
        PagingLoadResult<ExportedFormData> result = new PagingLoadResult<ExportedFormData>(exportedFormData, pagingLoadConfig.getOffset(), data.size(), count.intValue());
        return result;
    }
    
    List<ExportedFormData> getExportedFormData(String[] questionBindings, List<Object[]> data) {
		List<ExportedFormData> exportedFormDataList = new ArrayList<ExportedFormData>();
		for (Object[] d : data) {
		    String formDataId = (String)d[0];
		    FormData formData = formDataDAO.getFormData(Integer.parseInt(formDataId));
		    ExportedFormData exportedFormData = new ExportedFormData(formData);
		    exportedFormData.putExportedField("openxdata_form_data_id", formDataId);
		    for (int i=1, j=d.length; i<j; i++) {
		        Object dataElement = d[i];
				String binding = questionBindings[i-1];
				populateFormData(exportedFormData, dataElement, binding);

		    }
		    exportedFormDataList.add(exportedFormData);
		}
		return exportedFormDataList;
	}

	void populateFormData(ExportedFormData exportedFormData, Object data, String binding) {
		if (data == null) {
		    exportedFormData.putExportedField(binding, null);
		} else {
		    if (data instanceof Serializable) {
		        data = adaptUnsupportedDataTypes(data);
		        exportedFormData.putExportedField(binding, (Serializable)data);
		    } else {
		        log.warn("Retrieved value '"+data+"' for question '"+binding+"' is not Serializable, using default toString representation");
		        exportedFormData.putExportedField(binding, data.toString());
		    }
		}
	}

	/**
	 * Adapt data types not supported by GWT. These conversions can result in <a
     * href="http://java.sun.com/docs/books/jls/second_edition/html/conversions.doc.html#25363"><i>narrowing
     * primitive conversion</i></a>.
     * 
	 * @param data Object to be converted
	 * @return the adapted object
	 */
	Object adaptUnsupportedDataTypes(Object data) {
		if (data instanceof BigDecimal) {
			data = ((BigDecimal)data).doubleValue();
		} else if (data instanceof BigInteger) {
			data = ((BigInteger)data).intValue();
		}
		return data;
	}

    public void setDao(EditableDAO dao) {
        this.studyDAO = dao;
    }
    
    public void setFormDataDAO(FormDataDAO formDataDAO) {
		this.formDataDAO = formDataDAO;
	}
    
    public void setFormDAO(FormDAO formDAO) {
		this.formDAO = formDAO;
	}
    
    public void setStudyDAO(EditableDAO studyDAO) {
		this.studyDAO = studyDAO;
	}

	@Override
	@Secured({"Perm_Add_Users", "Perm_Add_Forms"})
    public void saveMappedFormUsers(Integer formId, List<User> usersToAdd, List<User> usersToDelete) {
		if (usersToAdd != null) {
			for (User u : usersToAdd) {
				UserFormMap map = new UserFormMap(u.getId(), formId);
				userFormMapDAO.saveUserMappedForm(map);
			}
		}
		if (usersToDelete != null) {
			for (User u : usersToDelete) {
				UserFormMap map = userFormMapDAO.getUserMappedForm(u.getId(), formId);
				userFormMapDAO.deleteUserMappedForm(map);
			}
		}
    }

	@Override
	@Secured({"Perm_View_Forms", "Perm_View_Users"})
    public PagingLoadResult<FormDef> getMappedForms(Integer userId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException {
	    return formDAO.getMappedForms(userId, loadConfig);
    }

	@Override
	@Secured({"Perm_View_Forms", "Perm_View_Users"})
    public PagingLoadResult<FormDef> getUnmappedForms(Integer userId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException {
	    return formDAO.getUnmappedForms(userId, loadConfig);
    }

	@Override
	@Secured({"Perm_Add_Users", "Perm_Add_Forms"})
    public void saveMappedUserForms(Integer userId, List<FormDef> formsToAdd, List<FormDef> formsToDelete) throws OpenXDataSecurityException {
		if (formsToAdd != null) {
		    for (FormDef fd : formsToAdd) {
		    	UserFormMap map = new UserFormMap(userId, fd.getId());
				userFormMapDAO.saveUserMappedForm(map);
		    }
		}
		if (formsToDelete != null) {
		    for (FormDef fd : formsToDelete) {
		    	UserFormMap map = userFormMapDAO.getUserMappedForm(userId, fd.getId());
				userFormMapDAO.deleteUserMappedForm(map);
		    }
		}
    }
}
