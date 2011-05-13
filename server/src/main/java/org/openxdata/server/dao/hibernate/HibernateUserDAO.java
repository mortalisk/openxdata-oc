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
package org.openxdata.server.dao.hibernate;

import java.util.List;

import org.openxdata.server.admin.model.User;
import org.openxdata.server.dao.UserDAO;
import org.springframework.stereotype.Repository;

/**
 * Provides a hibernate implementation
 * of the <code>UserDAO</code> data access <code> interface.</code>
 * 
 * @author Angel
 * @author Jonny Heggheim
 *
 */
@Repository("userDAO")
public class HibernateUserDAO extends BaseDAOImpl<User> implements UserDAO {

    @Override
	public void deleteUser(User user) {
        remove(user);
	}

	@Override
	public User findUserByEmail(String email) {
        return searchUniqueByPropertyEqual("email", email);
	}
	
	@Override
	public User findUserByPhoneNo(String phoneNo) {
        return searchUniqueByPropertyEqual("phoneNo", phoneNo);
	}
	
	@Override
	public User getUser(String username) {
        return searchUniqueByPropertyEqual("name", username);
	}
	
	@Override
	public List<User> getUsers() {
        return findAll();
	}

	@Override
	public void saveUser(User user) {		
        save(user);
	}
	
	@Override
	public void saveOnlineStatus(User user) {
		save(user);		
	}
}
