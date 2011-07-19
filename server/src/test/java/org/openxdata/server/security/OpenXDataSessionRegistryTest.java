package org.openxdata.server.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openxdata.server.admin.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.concurrent.SessionInformation;

/**
 *
 * @author kay
 */
public class OpenXDataSessionRegistryTest {

	private static Logger log = LoggerFactory.getLogger(OpenXDataSessionRegistryTest.class);
	
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

        log.debug("Principals:" + registry.getAllPrincipals());
        printSessions(ADMIN_USERNANE);
        printSessions(DISABLED_USERNAME);
    }

	private void printSessions(String user) {
		if (log.isDebugEnabled()) {
			StringBuilder buf = new StringBuilder();
			SessionInformation[] allSessions = registry.getAllSessions(user,
					true);
			buf.append("Sessions: ");
			buf.append(user);
			buf.append(" : ");
			for (SessionInformation sessionInformation : allSessions) {
				buf.append(sessionInformation.getSessionId());
				buf.append(' ');
			}
			log.debug("Sessions: ");
		}
	}

    @Test
    public void testAddDisableUser() {
        log.debug("addDisableUser");
        registry.addDisableUser(DISABLED_USR_OBJ);
        assertTrue(registry.containsDisabledUserName(DISABLED_USERNAME));
        assertTrue(registry.containsDisabledUser(DISABLED_USR_OBJ));
        assertFalse(registry.containsDisabledUserName("incorrectUserName"));
    }

    @Test
    public void testRemoveDisabledUser() {
        log.debug("removeDisabledUser");
        registry.addDisableUser(ADMIN_USR_OBJ);
        assertTrue(registry.containsDisabledUser(ADMIN_USR_OBJ));
        registry.removeDisabledUser(ADMIN_USR_OBJ);
        assertFalse(registry.containsDisabledUser(ADMIN_USR_OBJ));
    }

    @Test
    public void testContainsDisabledUser_User() {
        log.debug("containsDisabledUser");
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
