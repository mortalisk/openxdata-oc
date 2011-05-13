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
