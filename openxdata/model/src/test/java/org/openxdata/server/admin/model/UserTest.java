package org.openxdata.server.admin.model;

import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {

    private static final int ROLE_COUNT = 100;
    private User dummyUser;
    private Set<Role> roles;

    @Before
    public void createUser() {
        dummyUser = new User("user", "password");
    }

    @Before
    public void createRoles() {
        roles = new HashSet<Role>(ROLE_COUNT);
        for (int i = 0; i < ROLE_COUNT; i++) {
            roles.add(new Role("Role" + i));
        }
    }

    @Test
    public void testRemoveRole() {
        dummyUser.setRoles(roles);
        assertEquals(ROLE_COUNT, dummyUser.getRoles().size());

        dummyUser.removeRole(new Role("Role50"));
        assertEquals(ROLE_COUNT - 1, dummyUser.getRoles().size());

        dummyUser.removeRole(new Role("Role1"));
        assertEquals(ROLE_COUNT - 2, dummyUser.getRoles().size());

        dummyUser.removeRole(new Role("Role99"));
        assertEquals(ROLE_COUNT - 3, dummyUser.getRoles().size());
    }

    public void testAddRole() {
        assertTrue(dummyUser.getRoles().isEmpty());
        dummyUser.addRole(new Role("Role1"));
        assertEquals(1, dummyUser.getRoles().size());

        dummyUser.addRole(new Role("Role2"));
        assertEquals(2, dummyUser.getRoles().size());

        dummyUser.addRole(new Role("Role2"));
        assertEquals(2, dummyUser.getRoles().size());
    }
}
