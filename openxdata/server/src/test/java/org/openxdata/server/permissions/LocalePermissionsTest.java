/*
 *  Licensed to the OpenXdata Foundation (OXDF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The OXDF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, 
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 *  Copyright 2010 http://www.openxdata.org.
 */
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
 * @author Angel
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
