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
package org.openxdata.server.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openxdata.server.admin.model.Locale;
import org.openxdata.server.service.LocaleService;
import org.openxdata.server.service.UserService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Tests methods in the UtilityService which deal with locales.
 * 
 * @author daniel
 *
 */
public class LocaleServiceTest extends BaseContextSensitiveTest {

	@Autowired
	protected LocaleService localeService;
	
	@Autowired
	protected UserService userService;
	
	@Test
	public void getForms_shouldReturnAllForms() throws Exception {
		List<Locale> locales = localeService.getLocales();
		
		Assert.assertNotNull(locales);
		Assert.assertEquals(1, locales.size());
		Assert.assertEquals("English", locales.get(0).getName());
	}
	
	@Test
	public void saveLocale_shouldSaveLocaleList() throws Exception {
		final String localeName = "LocaleName";
		final String localeKey = "LocaleKey";
		
		List<Locale> locales = localeService.getLocales();
		Assert.assertEquals(1,locales.size());
		Assert.assertNull(getLocale(localeName,locales));
		
		Locale locale = new Locale();
		locale.setName(localeName);
		locale.setKey(localeKey);
		locale.setCreator(userService.getUsers().get(0));
		locale.setDateCreated(new Date());
		
		locales = new ArrayList<Locale>();
		locales.add(locale);
		
		localeService.saveLocale(locales);
		
		locales = localeService.getLocales();
		Assert.assertEquals(2,locales.size());
		Assert.assertNotNull(getLocale(localeName,locales));
	}
	
	@Test
	public void deleteLocale_shouldDeleteGivenLocale() throws Exception {
		final String localeName = "LocaleName";
		final String localeKey = "LocaleKey";
		
		List<Locale> locales = localeService.getLocales();
		Assert.assertEquals(1,locales.size());
		Assert.assertNull(getLocale(localeName,locales));
	
		Locale locale = new Locale();
		locale.setName(localeName);
		locale.setKey(localeKey);
		locale.setCreator(userService.getUsers().get(0));
		locale.setDateCreated(new Date());
		
		locales = new ArrayList<Locale>();
		locales.add(locale);
		
		localeService.saveLocale(locales);
		locales = localeService.getLocales();
		Assert.assertEquals(2,locales.size());
		
		locale = getLocale(localeName,locales);
		Assert.assertNotNull(locale);

		localeService.deleteLocale(locale);
		
		locales = localeService.getLocales();
		Assert.assertEquals(1,locales.size());
		Assert.assertNull(getLocale(localeName,locales));
	}
	
	/**
	 * Gets a locale object for a given name from a list of locale objects.
	 * 
	 * @param name the name of the locale to look for.
	 * @param locales the list of locale objects.
	 * @return the locale object that matches the given name.
	 */
	private Locale getLocale(String name, List<Locale> locales){
		for(Locale locale : locales){
			if(locale.getName().equals(name))
				return locale;
		}
		
		return null;
	}
}
