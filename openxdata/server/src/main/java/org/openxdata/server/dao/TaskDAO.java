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
package org.openxdata.server.dao;

import java.util.List;

import org.openxdata.server.admin.model.TaskDef;

/**
 * Provides data access 
 * services to the <code>Task service</code>.
 * 
 * @author Angel
 *
 */
public interface TaskDAO extends BaseDAO<TaskDef>{

	/**
	 * Gets a list of task definitions from the database.
	 * 
	 * @return the task list.
	 */
	List<TaskDef> getTasks();
	
	/**
	 * Saves a task definition to the database.
	 * 
	 * @param taskDef the task definition to save.
	 */
	void saveTask(TaskDef taskDef);
	
	/**
	 * Deletes a task definition from the database.
	 * 
	 * @param taskDef the task definition to delete.
	 */
	void deleteTask(TaskDef taskDef);
	
	/**
	 * Retrieves a task definition given the identifying name
	 * @param taskName String name of the task
	 * @return TaskDef found, null if no task found
	 */
	TaskDef getTask(String taskName);
	
}
