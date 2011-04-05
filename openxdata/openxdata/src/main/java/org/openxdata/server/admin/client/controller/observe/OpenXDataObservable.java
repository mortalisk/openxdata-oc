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
import java.util.Vector;

import org.openxdata.server.admin.model.Locale;
import org.openxdata.server.admin.model.ReportGroup;
import org.openxdata.server.admin.model.SettingGroup;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.admin.model.mapping.UserReportGroupMap;
import org.openxdata.server.admin.model.mapping.UserReportMap;
import org.openxdata.server.admin.model.mapping.UserStudyMap;

/** 
 * This class represents an observable object, or "data"
 * in the model-view paradigm. It can be subclassed to represent an 
 * object that the application wants to have observed. 
 * <p>
 * An observable object can have one or more observers. An observer 
 * may be any object that implements interface <tt>Observer</tt>. After an 
 * observable instance changes, an application calling the 
 * <code>Observable</code>'s <code>notifyObservers</code> method  
 * causes all of its observers to be notified of the change by a call 
 * to their <code>update</code> method. 
 * <p>
 * The order in which notifications will be delivered is unspecified.  
 * The default implementation provided in the Observable class will
 * notify Observers in the order in which they registered interest, but 
 * subclasses may change this order, use no guaranteed order, deliver 
 * notifications on separate threads, or may guarantee that their
 * subclass follows this order, as they choose.
 * <p>
 * Note that this notification mechanism is has nothing to do with threads 
 * and is completely separate from the <tt>wait</tt> and <tt>notify</tt> 
 * mechanism of class <tt>Object</tt>.
 * <p>
 * When an observable object is newly created, its set of observers is 
 * empty. Two observers are considered the same if and only if the 
 * <tt>equals</tt> method returns true for them.<p>
 *
 * </p>
 * @author Angel
 *
 */
public class OpenXDataObservable {
	
    private boolean changed = false;
    
    /** <tt>List</tt> of <tt>Observers.</tt>*/
    private Vector<OpenXDataObserver> observers;
   
    /** Construct an Observable with zero Observers. */
    public OpenXDataObservable() {
    	observers = new Vector<OpenXDataObserver>();
    }

    /**
     * Adds an observer to the set of observers for this object, provided 
     * that it is not the same as some observer already in the set. 
     * The order in which notifications will be delivered to multiple 
     * observers is not specified. See the class comment.
     *
     * @param   o   an observer to be added.
     * @throws NullPointerException   if the parameter o is null.
     */
    public synchronized void addObserver(OpenXDataObserver o) {
        if (o == null)
            throw new NullPointerException();
		if (!observers.contains(o)) {
		    observers.addElement(o);
		}
    }

    /**
     * Deletes an observer from the set of observers of this object. 
     * Passing <CODE>null</CODE> to this method will have no effect.
     * @param   o   the observer to be deleted.
     */
    public synchronized void deleteObserver(OpenXDataObserver o) {
        observers.removeElement(o);
    }

    /**
     * If this object has changed, as indicated by the 
     * <code>hasChanged</code> method, then notify all of its observers 
     * and then call the <code>clearChanged</code> method to 
     * indicate that this object has no longer changed. 
     * <p>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and <code>null</code>. In other 
     * words, this method is equivalent to:
     * <blockquote><tt>
     * notifyObservers(null)</tt></blockquote>
     *
     * @param type the <code>Model Object type</code> being notified on.
     * 
     * @see     java.util.Observable#clearChanged()
     * @see     java.util.Observable#hasChanged()
     * @see     java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void notifyObservers(Class<?> typeClass) {
    	notifyObservers(null, typeClass);
    }

    /**
     * If this object has changed, as indicated by the 
     * <code>hasChanged</code> method, then notify all of its observers 
     * and then call the <code>clearChanged</code> method to indicate 
     * that this object has no longer changed. 
     * <p>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and the <code>arg</code> argument.
     *
     * @param   arg   any object.
     * @param typeClass the class of the object that is being monitored.
     * @see     java.util.Observable#clearChanged()
     * @see     java.util.Observable#hasChanged()
     * @see     java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
	public void notifyObservers(Object arg, Class<?> typeClass) {
	    synchronized (this) {
	    	
	    /* We don't want the Observer doing callbacks into
	     * arbitrary code while holding its own Monitor.
	     * The code where we extract each Observable from 
	     * the Vector and store the state of the Observer
	     * needs synchronization, but notifying observers
	     * does not (should not).  The worst result of any 
	     * potential race-condition here is that:
	     * 1) a newly-added Observer will miss a
	     *   notification in progress
	     * 2) a recently unregistered Observer will be
	     *   wrongly notified when it doesn't care
	     */
	    if (!changed)
                return;
            clearChanged();
        }	
	
	    //Notify Observers
		for(OpenXDataObserver xObserver : observers){
			
			//Notifying StudiesViewController observers
			if(xObserver instanceof StudiesObserver ){				
				updateStudiesObserverObjects(arg, typeClass, xObserver);				
			}
			
			//Notifying ReportsViewController observers
			if(xObserver instanceof ReportsObserver){
				updateReportsObserverObjects(arg, typeClass, xObserver);					
			}

			
			//Notify observer.
			else{
				xObserver.update(this, arg);
			}
		}
    }



	/**
	 * Updates all Observers listening for notification on the <code>ReportsViewController.</code>
	 * 
	 * @param notifiedObjects The model objects that have changed.
	 * @param typeClass The class of the model object.
	 * @param xObserver <code>Observer</code> that was registered to listen for notifications.
	 */
	@SuppressWarnings("unchecked")
	private synchronized void updateReportsObserverObjects(Object notifiedObjects, Class<?> typeClass, OpenXDataObserver xObserver) {
		if(typeClass.equals(UserReportMap.class)){
			((ReportsObserver)xObserver).updateUserMappedReports(this, (List<UserReportMap>) notifiedObjects);
		}
			
		else 
			if(typeClass.equals(UserReportGroupMap.class)){
			((ReportsObserver)xObserver).updateUserMappedReportGroups(this, (List<UserReportGroupMap>) notifiedObjects);
		}
		
		else 
			if(typeClass.equals(ReportGroup.class)){
				((ReportsObserver)xObserver).updateReportGroups(this, (List<ReportGroup>) notifiedObjects);
			}
	}

	/**
	 * Updates all Observers listening for notification on the <code>StudiesViewController.</code>
	 * 
	 * @param notifiedObjects The model objects that have changed.
	 * @param typeClass The class of the model object.
	 * @param xObserver <code>Observer</code> that was registered to listen for notifications.
	 */
	@SuppressWarnings("unchecked")
	private synchronized void updateStudiesObserverObjects(Object notifiedObjects, Class<?> typeClass, OpenXDataObserver xObserver) {
		if(typeClass.equals(StudyDef.class)){
			((StudiesObserver)xObserver).updateStudies(this, (List<StudyDef>) notifiedObjects);
		}
		else if(typeClass.equals(UserFormMap.class)){
			((StudiesObserver)xObserver).updateUserMappedForms(this, (List<UserFormMap>) notifiedObjects);
		}
		else if(typeClass.equals(UserStudyMap.class)){
			((StudiesObserver)xObserver).updateUserMappedStudies(this, (List<UserStudyMap>) notifiedObjects);
		}
	}

    /**
     * Clears the observer list so that this object no longer has any observers.
     */
    public synchronized void deleteObservers() {
    	observers.removeAllElements();
    }

    /**
     * Marks this <tt>Observable</tt> object as having been changed; the 
     * <tt>hasChanged</tt> method will now return <tt>true</tt>.
     */
    protected synchronized void setChanged() {
    	changed = true;
    }

    /**
     * Indicates that this object has no longer changed, or that it has 
     * already notified all of its observers of its most recent change, 
     * so that the <tt>hasChanged</tt> method will now return <tt>false</tt>. 
     * This method is called automatically by the 
     * <code>notifyObservers</code> methods. 
     *
     * @see     java.util.Observable#notifyObservers()
     * @see     java.util.Observable#notifyObservers(java.lang.Object)
     */
    protected synchronized void clearChanged() {
    	changed = false;
    }

    /**
     * Tests if this object has changed. 
     *
     * @return  <code>true</code> if and only if the <code>setChanged</code> 
     *          method has been called more recently than the 
     *          <code>clearChanged</code> method on this object; 
     *          <code>false</code> otherwise.
     * @see     java.util.Observable#clearChanged()
     * @see     java.util.Observable#setChanged()
     */
    public synchronized boolean hasChanged() {
    	return changed;
    }

    /**
     * Returns the number of observers of this <tt>Observable</tt> object.
     *
     * @return  the number of observers of this object.
     */
    public synchronized int countObservers() {
    	return observers.size();
    }
}

