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

/**
 * Base interface defines the contract for all observing class.
 * <p>
 * A class can implement the <code>OpenXDataObserver</code> 
 * interface when it wants to be notified of changes in <tt>observable objects.</tt>
 * </p>
 * 
 * @author Angel
 *
 */
public interface OpenXDataObserver {
	
    /**
     * This method is called whenever the observed object is changed. 
     * An application calls an <tt>Observable</tt> object's <code>notifyObservers</code> 
     * method to have all the object's observers notified of the change.
     * <p>
     * This <tt>method</tt> should be called in the event that there is no other specific <tt>method</tt> to update the <code>Observer's objects.</code>
     * 
     * @param   observable     the observable object.
     * @param   observedModelObjects   an argument passed to the <code>notifyObservers</code> <tt>method.</tt>
     */
    void update(OpenXDataObservable observable, Object observedModelObjects);

}
