package org.openxdata.server.dao;

import java.util.List;

import org.openxdata.server.admin.model.Locale;

/**
 * @author Angel
 *
 */
public interface LocaleDAO extends BaseDAO<Locale> {
	
	/**
	 * Gets a list of locales.
	 * 
	 * @return the locale list.
	 */	
	List<Locale> getLocales();
	
	/**
	 * Deletes a locale from the database.
	 * 
	 * @param locale the locale to delete.
	 */
	void deleteLocale(Locale locale);
	
	/**
	 * Saves a list of locales.
	 * 
	 * @param locales the locale list.
	 */
	void saveLocale(List<Locale> locales);

}
