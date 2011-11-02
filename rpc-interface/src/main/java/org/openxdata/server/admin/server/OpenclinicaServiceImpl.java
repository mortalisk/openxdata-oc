package org.openxdata.server.admin.server;

import java.util.Set;

import org.openxdata.server.admin.client.service.OpenclinicaService;
import org.openxdata.server.admin.model.OpenclinicaStudy;
import org.openxdata.server.admin.model.exception.UnexpectedException;
import org.openxdata.server.rpc.OxdPersistentRemoteService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class OpenclinicaServiceImpl extends OxdPersistentRemoteService implements OpenclinicaService {

	private static final long serialVersionUID = 4681223938185113228L;
	
	private org.openxdata.server.service.OpenclinicaService openclinicaService;
	
	private org.openxdata.server.service.OpenclinicaService getOpenClinicaService() {
		if (openclinicaService == null) {
			WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(this.getServletContext());
			openclinicaService = (org.openxdata.server.service.OpenclinicaService) ctx.getBean("OpenClinicaService");
		}
		return openclinicaService;
	}

	@Override
	public List<OpenclinicaStudy> getOpenClinicaStudies() throws UnexpectedException {
		return getOpenClinicaService().getOpenClinicaStudies();
	}

	@Override
	public String importOpenClinicaStudy(String identifier) throws UnexpectedException {
		return getOpenClinicaService().importOpenClinicaStudy(identifier);
	}

	@Override
	public Boolean hasStudyData(String studyKey) {
		return getOpenClinicaService().hasStudyData(studyKey);
	}

	@Override
	public void exportOpenclinicaStudyData(String studyKey) {
		getOpenClinicaService().exportOpenClinicaStudyData(studyKey);

	}

}
