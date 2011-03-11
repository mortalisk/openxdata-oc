package org.openxdata.server.admin.server;

import java.util.List;

import javax.servlet.ServletException;

import org.openxdata.server.admin.client.service.UtilityService;
import org.openxdata.server.admin.model.Locale;
import org.openxdata.server.rpc.OxdPersistentRemoteService;
import org.openxdata.server.service.LocaleService;
import org.springframework.web.context.WebApplicationContext;

/**
 * Default Implementation for the <code>UtilityService Interface.</code> 
 */
public class UtilityServiceImpl extends OxdPersistentRemoteService implements UtilityService {

	private static final long serialVersionUID = 865681095382002202L;

	private org.openxdata.server.service.UtilityService utilityService;

	private org.openxdata.server.service.AuthenticationService authenticationService;
    private LocaleService localeService;
	
	public UtilityServiceImpl() {
	}

	@Override
	public void init() throws ServletException {
		super.init();
		WebApplicationContext ctx = getApplicationContext();

		utilityService = (org.openxdata.server.service.UtilityService) ctx.getBean("utilityService");
		
		authenticationService = (org.openxdata.server.service.AuthenticationService) ctx.getBean("authenticationService");

		localeService = (org.openxdata.server.service.LocaleService) ctx.getBean("localeService");
    }

	@Override
	public void deleteLocale(Locale locale) {
		localeService.deleteLocale(locale);
	}

	@Override
	public List<Locale> getLocales() {
		return localeService.getLocales();
	}

	@Override
	public Boolean installMobileApp(List<String> phonenos, String url, String modemComPort,
			int modemBaudRate, String promptText) {
		return utilityService.installMobileApp(phonenos, url, modemComPort, modemBaudRate,
				promptText);
	}

	@Override
	public void saveLocale(List<Locale> locales) {
		localeService.saveLocale(locales);
	}

	@Override
	public Boolean checkIfPasswordsMatchOnAdministrator(String username, String password) {
		return authenticationService.isValidUserPassword(username, password);
	}

}
