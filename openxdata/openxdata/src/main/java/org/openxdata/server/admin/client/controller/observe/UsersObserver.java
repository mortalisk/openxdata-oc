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
package org.openxdata.server.admin.client.controller.observe;

import java.util.List;

import org.openxdata.server.admin.model.User;

/**
 *
 * @author Angel
 *
 */
public interface UsersObserver extends OpenXDataObserver {
	
    /**
     * This method is called whenever the <code>Users</code> change. 
     * 
     * An application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's observers notified of the change.
     *
     * @param   observable     the observable object.
     * @param   users   the changed <code>User</code> definitions passed to the <code>notifyObservers</code> method.
     */
	void updateUsers(OpenXDataObservable observable, List<User> users);
}
