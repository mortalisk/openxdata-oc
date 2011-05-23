package org.openxdata.server.dao.hibernate;

import java.util.List;

import org.openxdata.server.admin.model.Locale;
import org.openxdata.server.dao.LocaleDAO;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository("localeDAO")
public class HibernateLocaleDAO extends BaseDAOImpl<Locale> implements LocaleDAO {

	@Override
	public void deleteLocale(Locale locale) {
		remove(locale);
	}

	@Override
	public List<Locale> getLocales() {
		return findAll();
	}

	@Override
	public void saveLocale(List<Locale> locales) {
		for(Locale locale: locales) {
			save(locale);
        }
	}
}
