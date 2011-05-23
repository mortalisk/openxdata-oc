package org.openxdata.server.service;

import java.util.List;
import org.openxdata.server.admin.model.Locale;

/**
 *
 * @author Jonny Heggheim
 */
public interface LocaleService {

    /**
     * Gets a list of locales.
     *
     * @return the locale list.
     */
    List<Locale> getLocales();

    /**
     * Saves a list of locales.
     *
     * @param locales the locale list to save.
     */
    void saveLocale(List<Locale> locales);

    /**
     * Delete a locale
     *
     * @param locale locale to delete.
     */
    void deleteLocale(Locale locale);
}
