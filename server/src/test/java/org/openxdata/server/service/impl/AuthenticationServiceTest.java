package org.openxdata.server.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.openxdata.server.admin.model.exception.OpenXDataException;
import org.openxdata.server.service.AuthenticationService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthenticationServiceTest extends BaseContextSensitiveTest {

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    public void testIsValidUserPassword_AssertTrue() throws OpenXDataException {
        Assert.assertNotNull(authenticationService.authenticate("user", "love"));
    }

    @Test
    public void testIsValidUserPassword_OnExistingUsers_AssertTrue() throws OpenXDataException {
        Assert.assertNotNull(authenticationService.authenticate("guyzb", "daniel123"));
    }

    @Test
    public void testIsValidUserPassword_AssertFalse() throws OpenXDataException {
        Assert.assertNull(authenticationService.authenticate("user", "loved"));
    }

    @Test
    public void testIsValidUserPassword_OnExistingUsers_AssertFalse() throws OpenXDataException {
        Assert.assertNull(authenticationService.authenticate("admin", "admin123"));
    }

    @Test
    public void testIsDisabledUser() throws OpenXDataException {
        String userName = "disabledUser";
        String passwd = "daniel123";
        //First Make sure user is Valid
        Assert.assertNotNull(authenticationService.isValidUserPassword(userName, passwd));
        Assert.assertNull(authenticationService.authenticate(userName, passwd));

    }
}
