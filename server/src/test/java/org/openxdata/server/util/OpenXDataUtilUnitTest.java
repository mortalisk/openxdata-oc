/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.server.util;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.expectPrivate;
import static org.powermock.api.easymock.PowerMock.mockStaticPartial;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 * @author Jonny Heggheim
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({OpenXDataUtil.class, File.class})
public class OpenXDataUtilUnitTest {

    @Mock
    private File fileMock;

    @Before
    public void createMocks() throws Exception {
        String[] methods = {"getHomeFolder", "isUnix", "isWindows", "fileSeparator"};
        mockStaticPartial(OpenXDataUtil.class, methods);
        expectPrivate(OpenXDataUtil.class, "fileSeparator").andReturn("|").anyTimes();
    }

    private void expectOperatingSystem(boolean isUnix, boolean isWindows) {
        expect(OpenXDataUtil.isUnix()).andReturn(isUnix).anyTimes();
        expect(OpenXDataUtil.isWindows()).andReturn(isWindows).anyTimes();
    }

    private void expectHomeFolder(String folder) {
        expect(OpenXDataUtil.getHomeFolder()).andReturn(folder);
    }

    @Test
    public void testGetApplicationDirectoryOnUnix() throws Exception {
        expectOperatingSystem(true, false);
        expectHomeFolder("|home|test");

        String expected = "|home|test|.openxdata|";
        expectNew(File.class, expected).andReturn(fileMock);

        expect(fileMock.exists()).andReturn(Boolean.TRUE);

        PowerMock.replayAll();

        String actual = OpenXDataUtil.getApplicationDataDirectory();

        PowerMock.verifyAll();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetApplicationDirectoryOnWindows() throws Exception {
        expectOperatingSystem(false, true);
        expectHomeFolder("c:|home|test");

        String expected = "c:|home|test|Application Data|openxdata|";
        expectNew(File.class, expected).andReturn(fileMock);

        expect(fileMock.exists()).andReturn(Boolean.TRUE);

        PowerMock.replayAll();

        String actual = OpenXDataUtil.getApplicationDataDirectory();

        PowerMock.verifyAll();
        assertEquals(expected, actual);
    }

    @Test
    public void applicationDirectoryShouldBeCreatedIfMissing() throws Exception {
        expectOperatingSystem(true, false);
        expectHomeFolder("|home|test");

        String expected = "|home|test|.openxdata|";
        expectNew(File.class, expected).andReturn(fileMock);

        expect(fileMock.exists()).andReturn(Boolean.FALSE);
        expect(fileMock.mkdirs()).andReturn(Boolean.TRUE);
        expect(fileMock.getAbsolutePath()).andReturn(expected);
        PowerMock.replayAll();

        String actual = OpenXDataUtil.getApplicationDataDirectory();

        PowerMock.verifyAll();
        assertEquals(expected, actual);
    }

    @Test
    public void applicationDirectoryShouldBeCreatedIfMissingFails() throws Exception {
        expectOperatingSystem(true, false);
        expectHomeFolder("|home|test");

        String expected = "|home|test|.openxdata|";
        expectNew(File.class, expected).andReturn(fileMock);

        expect(fileMock.exists()).andReturn(Boolean.FALSE);
        expect(fileMock.mkdirs()).andReturn(Boolean.FALSE);
        expect(fileMock.getAbsolutePath()).andReturn(expected);
        PowerMock.replayAll();

        String actual = OpenXDataUtil.getApplicationDataDirectory();

        PowerMock.verifyAll();
        assertEquals(expected, actual);
    }
}
