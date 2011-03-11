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
package org.openxdata.server.security;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.User;

/**
 * Extends the Spring Security User in order 
 * to add OpenXData specific user details (such as the <tt>User</tt> specific salt).
 * 
 * An immutable object (i.e. no setters)
 *
 * @author dagmar@cell-life.org.za
 */
public class OpenXDataUserDetails extends User {
    
    private static final long serialVersionUID = 2323963197126198664L;

    private org.openxdata.server.admin.model.User oxdUser;

    public OpenXDataUserDetails(org.openxdata.server.admin.model.User oxdUser,
            boolean enabled, boolean accountNonExpired,
            boolean credentialsNonExpired, boolean accountNonLocked,
            GrantedAuthority[] authorities) throws IllegalArgumentException {
    	
        super(oxdUser.getName(), oxdUser.getPassword(), enabled, accountNonExpired, credentialsNonExpired,
                accountNonLocked, authorities);
        this.oxdUser = oxdUser;
    }

    public String getSalt() {
        return oxdUser.getSalt();
    }
    
    public org.openxdata.server.admin.model.User getOXDUser() {
        return oxdUser;
    }
    
    /**
     * <tt>Overrides super.equals.</tt>
     * <p>
     * We do not equality on instances of <tt>super class</tt> to ignore the identity of the <tt>subclass and the added fields.</tt>
     * <p>Object</tt> we checking for equality.
     * 
     * @param arg <tt>Ob
     * @return the result of invoking super.equals(arg).
     */
    @Override
	public boolean equals(Object arg){
    	return super.equals(arg);
    }
}
