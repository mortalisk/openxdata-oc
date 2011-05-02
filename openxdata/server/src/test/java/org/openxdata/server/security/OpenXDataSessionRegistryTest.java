/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.server.security;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openxdata.server.admin.model.User;
import org.springframework.security.concurrent.SessionInformation;

/**
 *
 * @author kay
 */
public class OpenXDataSessionRegistryTest {

    private OpenXDataSessionRegistryImpl registry = new OpenXDataSessionRegistryImpl();
    //-- Constants    ---//
    private String ADMIN_USERNANE = "admin";
    private String DISABLED_USERNAME = "disabled";
    private User ADMIN_USR_OBJ = new User(ADMIN_USERNANE);
    private User DISABLED_USR_OBJ = new User(DISABLED_USERNAME);
    //Sessions
    private String ADMIN_SESSION_ID1 = ADMIN_USERNANE + "1";
    private String ADMIN_SESSION_ID2 = ADMIN_USERNANE + "2";
    private String DISABLED_USERSESSION_ID1 = DISABLED_USERNAME + "1";
    private String DISABLED_USERSESSION_ID2 = DISABLED_USERNAME + "2";

    public OpenXDataSessionRegistryTest() {
    }

    @Before
    public void setUp() {

        registry.registerNewSession(ADMIN_SESSION_ID1, ADMIN_USERNANE);
        registry.registerNewSession(ADMIN_SESSION_ID2, ADMIN_USERNANE);
        registry.registerNewSession(DISABLED_USERSESSION_ID1, DISABLED_USERNAME);
        registry.registerNewSession(DISABLED_USERSESSION_ID2, DISABLED_USERNAME);

        System.out.println("Principals:" + registry.getAllPrincipals());
        printSessions(ADMIN_USERNANE);
        printSessions(DISABLED_USERNAME);
    }

    private void printSessions(String user) {
        System.out.print("Sessions: " + user + " : ");
        SessionInformation[] allSessions = registry.getAllSessions(user, true);
        for (SessionInformation sessionInformation : allSessions) {
            System.out.print(sessionInformation.getSessionId() + " ");
        }
        System.out.println();
    }

    @Test
    public void testAddDisableUser() {
        System.out.println("addDisableUser");
        registry.addDisableUser(DISABLED_USR_OBJ);
        assertTrue(registry.containsDisabledUserName(DISABLED_USERNAME));
        assertTrue(registry.containsDisabledUser(DISABLED_USR_OBJ));
        assertFalse(registry.containsDisabledUserName("incorrectUserName"));
    }

    @Test
    public void testRemoveDisabledUser() {
        System.out.println("removeDisabledUser");
        registry.addDisableUser(ADMIN_USR_OBJ);
        assertTrue(registry.containsDisabledUser(ADMIN_USR_OBJ));
        registry.removeDisabledUser(ADMIN_USR_OBJ);
        assertFalse(registry.containsDisabledUser(ADMIN_USR_OBJ));
    }

    @Test
    public void testContainsDisabledUser_User() {
        System.out.println("containsDisabledUser");
        assertFalse(registry.containsDisabledUser(ADMIN_USR_OBJ));
        registry.addDisableUser(ADMIN_USR_OBJ);
        assertTrue(registry.containsDisabledUser(ADMIN_USR_OBJ));
        assertFalse(registry.containsDisabledUser(null));
    }

    @Test
    public void testRemoveSessionInformation() {
        registry.addDisableUser(DISABLED_USR_OBJ);
        registry.removeSessionInformation(DISABLED_USERSESSION_ID1);
        assertEquals(registry.getAllPrincipals().length, 2);
        assertTrue(registry.containsDisabledUser(DISABLED_USR_OBJ));

        registry.removeSessionInformation(DISABLED_USERSESSION_ID2);
        assertFalse(registry.containsDisabledUser(DISABLED_USR_OBJ));
        assertEquals(registry.getAllPrincipals().length, 1);
    }
}
