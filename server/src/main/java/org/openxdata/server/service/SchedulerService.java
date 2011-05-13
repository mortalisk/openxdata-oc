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
package org.openxdata.server.service;

import org.openxdata.server.Task;
import org.openxdata.server.admin.model.TaskDef;

/**
 * Service class to handle scheduling and management of tasks.
 * A task is the actual running instance while a task definition is just the object
 * which has properties for a particular type of task.
 * 
 * @author daniel
 *
 */
public interface SchedulerService {
	
	/**
	 * Starts the scheduler service.
	 */
	public void start();
	
	/**
	 * Stops the scheduler service.
	 */
	public void stop();
	
	/**
	 * Starts or runs a given task.
	 * 
	 * @param taskDef the task definition.
	 * @return true if the task has started, else false.
	 */
	public boolean startTask(TaskDef taskDef);
	
	/**
	 * Stops a given task from running.
	 * 
	 * @param taskDef the task definition.
	 * 
	 * @return true if the task stopped running, else false.
	 */
	public boolean stopTask(TaskDef taskDef);
	
	/**
	 * Registers a running instance of a task.
	 * 
	 * @param task the task to register.
	 */
	public void registerTaskRunningInstance(Task task);
	
	/**
	 * Checks if a task is running.
	 * 
	 * @param taskDef the task definition to check if running.
	 * @return true if the task is running, else false.
	 */
	public boolean isTaskRunning(TaskDef taskDef);
}
