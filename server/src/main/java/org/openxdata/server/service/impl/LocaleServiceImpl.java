package org.openxdata.server.service.impl;

import java.util.List;
import org.openxdata.server.admin.model.Locale;
import org.openxdata.server.dao.LocaleDAO;
import org.openxdata.server.service.LocaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Jonny Heggheim
 */
@Transactional
@Service("localeService")
public class LocaleServiceImpl implements LocaleService {

    private LocaleDAO localeDAO;

    @Autowired
    public LocaleServiceImpl(LocaleDAO localeDAO) {
        this.localeDAO = localeDAO;
    }

    @Override
    @Secured("Perm_Delete_Locales")
	public void deleteLocale(Locale locale) {
        localeDAO.deleteLocale(locale);
    }

    @Override
	@Transactional(readOnly = true)
	// FIXME: should be @Secured("Perm_View_Locales")
    public List<Locale> getLocales() {
        return localeDAO.getLocales();
    }

    @Override
    @Secured("Perm_Add_Locales")
	public void saveLocale(List<Locale> locales) {
        localeDAO.saveLocale(locales);
    }
}
