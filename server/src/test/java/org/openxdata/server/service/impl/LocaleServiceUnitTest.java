package org.openxdata.server.service.impl;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openxdata.server.admin.model.Locale;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.dao.LocaleDAO;

/**
 * Tests methods in the LocaleService which deal with locales.
 * 
 * @author Jonny Heggheim
 *
 */
public class LocaleServiceUnitTest {

    private LocaleServiceImpl serviceImpl;
    private LocaleDAO daoMock;
    private Locale dummyLocale;

    @Before
    public void createServiceAndMocks() {
        daoMock = mock(LocaleDAO.class);
        serviceImpl = new LocaleServiceImpl(daoMock);
    }

    @Before
    public void createDummyLocale() {
        final String localeName = "LocaleName";
        final String localeKey = "LocaleKey";

        dummyLocale = new Locale();
        dummyLocale.setName(localeName);
        dummyLocale.setKey(localeKey);
        dummyLocale.setCreator(new User("dummyUser"));
        dummyLocale.setDateCreated(new GregorianCalendar(2010, 10, 10).getTime());
    }

    @Test
    public void getLocalesShouldJustForwardTheCallToDao() throws Exception {
        List<Locale> expected = new ArrayList<Locale>();
        expected.add(dummyLocale);

        when(daoMock.getLocales()).thenReturn(expected);
        List<Locale> actual = serviceImpl.getLocales();
        assertSame(expected, actual);

        verify(daoMock).getLocales();
    }

    @Test
    public void saveLocaleShouldJustForwardTheCallToDao() throws Exception {
        List<Locale> locales = new ArrayList<Locale>();
        locales.add(dummyLocale);

        serviceImpl.saveLocale(locales);
        verify(daoMock).saveLocale(locales);
    }

    @Test
    public void deleteLocaleShouldJustForwardTheCallToDao() throws Exception {
        serviceImpl.deleteLocale(dummyLocale);
        verify(daoMock).deleteLocale(dummyLocale);
    }
}
