package org.openxdata.server.dao.hibernate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDataHeader;
import org.openxdata.server.admin.model.FormDataVersion;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;
import org.openxdata.server.dao.FormDataDAO;
import org.openxdata.server.export.ExportConstants;
import org.springframework.stereotype.Repository;

import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

/**
 *
 */
@Repository("formDataDAO")
public class HibernateFormDataDAO extends BaseDAOImpl<FormData> implements FormDataDAO {

	@Override
	public FormData getFormData(Integer formDataId) {
		return find(formDataId);
	}

	@Override
	public void saveFormData(FormData formData){
		save(formData);
	}
	
	@Override
	public void saveFormDataVersion(FormData formData) {
		saveFormDataVersion(formData, false);
	}
	
	@Override
	public void saveFormDataVersion(FormData formData, boolean isDelete) {
		// note: must use SQL because we have to avoid using the hibernate cache
		// we want to retrieve the old form data for the backup, not the new one
		Query query = getSession()
			.createSQLQuery("select data from form_data where form_data_id = ?")
			.addScalar("data", Hibernate.TEXT)
			.setInteger(0, formData.getId());
		String oldData = ((String)query.uniqueResult());
		FormDataVersion backup = new FormDataVersion(
				isDelete ? null : formData, oldData, formData.getDateChanged(),
				formData.getChangedBy());
        saveFormDataVersion(backup);
	}
	
	@Override
    public void saveFormDataVersion(FormDataVersion formDataVersion) {
	    _save(formDataVersion);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<FormDataVersion> getFormDataVersion(Integer formDataId) {
		return getSession().createCriteria(FormDataVersion.class)
			.createAlias("formData", "fd").add(Restrictions.eq("fd.id", formDataId)).list();
	}

	@Override
	public List<FormData> getFormDataList(FormDef form) {
		int versionId = form.getDefaultVersion().getId();
		Search search = new Search(FormData.class);
		search.addFilterEqual("formDefVersionId", versionId);
		return search(search);
	}
		
	public Integer getFormDataCount(Integer formDefId) {
		Search dataCountSearch = new Search();
		dataCountSearch.addFilterEqual("formDefVersionId", formDefId);
		dataCountSearch.addFilterEqual("voided", false);
		return count(dataCountSearch);
	}


	@Override
	public PagingLoadResult<FormDataHeader> getUnexportedFormData(PagingLoadConfig loadConfig) {
		Search formSearch = getSearchFromLoadConfig(loadConfig, "id");
		formSearch.addFilterIn("exported", Arrays.asList(ExportConstants.RDMS_EXPORT_BIT_0));
		formSearch.addFilterEqual("voided", false);
		SearchResult<FormData> result = searchAndCount(formSearch);
		List<FormDataHeader> list = new ArrayList<FormDataHeader>();
		Map<Integer, FormDefVersion> formDefVersions = new HashMap<Integer, FormDefVersion>();
		for (FormData fd : result.getResult()) {
			FormDataHeader header = new FormDataHeader(fd.getFormDefVersionId(), fd.getDateCreated(), fd.getCreator().getName());
			header.setId(fd.getId());
			header.setDescription(fd.getDescription());
			FormDefVersion def = formDefVersions.get(fd.getFormDefVersionId()); 
			if (def == null) {
				def = getFormDefVersion(fd.getFormDefVersionId());
				formDefVersions.put(fd.getFormDefVersionId(), def);
			}
			header.setFormName(def.getFormDef().getName());
			header.setVersionName(def.getName());
			list.add(header);
		}
		int totalNum = result.getTotalCount();
		int offset = loadConfig == null ? 0 : loadConfig.getOffset();
		return new PagingLoadResult<FormDataHeader>(list, offset, list.size(), totalNum);
	}
	
	private FormDefVersion getFormDefVersion(Integer formDefId) {
		Search formDefVersionSearch = new Search(FormDefVersion.class);
		formDefVersionSearch.addFilterEqual("id", formDefId);
		FormDefVersion result = (FormDefVersion)_searchUnique(FormDefVersion.class, formDefVersionSearch);
		return result;
	}
	
	@Override
	public Integer getUnprocessedDataCount(Integer formDefVersionId) {
		Search unprocessedDataSearch = new Search();
		unprocessedDataSearch.addFilterIn("exported", Arrays.asList(ExportConstants.RDMS_EXPORT_BIT_0));
		unprocessedDataSearch.addFilterEqual("voided", false);
		unprocessedDataSearch.addFilterEqual("formDefVersionId", formDefVersionId);
		return count(unprocessedDataSearch);
	}
}
