/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.server.security;

import org.openxdata.server.admin.model.User;
import org.springframework.security.concurrent.SessionRegistry;

/**
 *
 * @author kay
 */
public interface OpenXDataSessionRegistry extends SessionRegistry{

    void addDisableUser(User user);

    boolean containsDisabledUser(User user);

    boolean containsDisabledUserName(String userName);

    void removeDisabledUser(User user);

    void removeSessionInformation(String sessionId);

    void updateUserEntries(User user);
    
}
