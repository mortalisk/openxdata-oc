package org.openxdata.server.permissions;

import org.junit.Test;
import org.openxdata.server.admin.model.Locale;
import org.openxdata.server.admin.model.exception.OpenXDataException;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;

import java.util.ArrayList;
import java.util.List;


/**
 * Tests permissions for accessing locales.
 * 
 * @author daniel
 *
 */
public class LocalePermissionsTest extends PermissionsTest {
        
    @Test(expected=OpenXDataSecurityException.class)
    public void saveLocales_shouldThrowOpenXDataSecurityException() throws OpenXDataException {
    	localeService.saveLocale(mockLocaleList());
    }
    
    @Test(expected=OpenXDataSecurityException.class)
    public void deleteLocales_shouldThrowOpenXDataSecurityException() throws OpenXDataException {
    	localeService.deleteLocale(new Locale());
    }
    
    public List<Locale> mockLocaleList() {
		List<Locale> locales = new ArrayList<Locale>();
		Locale locale = new Locale();
		locales.add(0, locale);
		
		return locales;
    }

}
