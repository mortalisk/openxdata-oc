package org.openxdata.server.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import org.openxdata.server.admin.model.User;
import org.springframework.security.concurrent.SessionInformation;
import org.springframework.security.concurrent.SessionRegistryImpl;

/**
 * An extension of session registry which contains a list of disable users
 * to facilitate runtime kicking of users out of the system
 * @author kay
 */
public class OpenXDataSessionRegistryImpl extends SessionRegistryImpl implements OpenXDataSessionRegistry {

    //Should this be a fully synchronized hashmap or a faster Concurrent hashmap
    private final Map<String, User> disabledUsers = new ConcurrentHashMap<String, User>();
    private static Logger log = Logger.getLogger(OpenXDataSessionRegistryImpl.class);

    @Override
    public void addDisableUser(User user) {
        if (user == null) {
            return;
        }
        log.debug("Adding Disabled User: " + user.getName());
        synchronized (disabledUsers) {
            disabledUsers.put(user.getName(),cloneUser(user));
        }
    }

    @Override
    public void removeDisabledUser(User user) {
        if (user == null) {
            return;
        }
        removeDisabledUser(user.getName());
    }

    @Override
    public boolean containsDisabledUser(User user) {
        if (user == null) {
            return false;
        }
        return disabledUsers.containsKey(user.getName());
    }

    @Override
    public boolean containsDisabledUserName(String userName) {
        if (userName == null) {
            return false;
        }
        return disabledUsers.containsKey(userName);
    }

    @Override
    public void registerNewSession(String sessionId, Object principal) {
        log.debug("Registering Session: " + sessionId + " Principle: " + principal);
        super.registerNewSession(sessionId, principal);
        if (containsDisabledUserName(principal + "")) {
            removeDisabledUser(principal + "");
        }
    }

    @Override
    public void removeSessionInformation(String sessionId) {
        log.debug("Removing Session ID: "+sessionId);
        SessionInformation sessionInformation = getSessionInformation(sessionId);
        if (sessionInformation == null) {
            return;
        }
        super.removeSessionInformation(sessionId);

        Object principal = sessionInformation.getPrincipal();

        //Check if session was a attached to a disable user/principal
        //and remove the user from the disabled list in case 
        //all his session have expired
        if (!containsDisabledUserName(principal.toString())) {
            return;
        }


        SessionInformation[] allSessions = getAllSessions(principal, true);
        if (allSessions == null || allSessions.length == 0) {
            //All sessions have expired. Remove the user from the disabled list
            removeDisabledUser(principal.toString());
        }
    }

    private void removeDisabledUser(String userName) {
        log.debug("Removing from SessionRegistry Disbaled User: " + userName);
        disabledUsers.remove(userName);
    }
    /**
     * If user is disabled, it adds the user to the disabled list given the user
     * already has active sessions.
     * Otherwise it removes the user from the disabled list 
     * @param user 
     */
    @Override
    public void updateUserEntries(User user) {
        if (!user.isDisabled()) {
            if (containsDisabledUser(user)) {
                removeDisabledUser(user);
            }
        } else {
            Object[] allPrincipals = getAllPrincipals();
            for (Object object : allPrincipals) {
                if (object.equals(user.getName())) {
                    addDisableUser(user);
                }
            }
        }
    }

    private User cloneUser(User user) {
       User clone = new User();
       clone.setId(user.getId());
       clone.setName(user.getName());
       return clone;
    }
}
