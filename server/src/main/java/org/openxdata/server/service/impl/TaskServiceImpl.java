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
package org.openxdata.server.service.impl;

import java.util.List;

import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.dao.TaskDAO;
import org.openxdata.server.service.SchedulerService;
import org.openxdata.server.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation for <code>Task Service</code>.
 * 
 * @author Angel
 *
 */
@Service("taskService")
@Transactional
public class TaskServiceImpl implements TaskService {

	@Autowired
    private TaskDAO taskDAO;
    
	@Autowired
    private SchedulerService scheduler;

    void setTaskDAO(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    void setScheduler(SchedulerService scheduler) {
		this.scheduler = scheduler;
	}
    
    @Override
    @Secured("Perm_Delete_Tasks")
	public void deleteTask(TaskDef taskDef) {
        taskDAO.deleteTask(taskDef);
    }

    @Override
	@Transactional(readOnly = true)
	@Secured("Perm_View_Tasks")
    public List<TaskDef> getTasks() {
        List<TaskDef> taskDefs = taskDAO.getTasks();
        for (TaskDef taskDef : taskDefs) {
            taskDef.setRunning(scheduler.isTaskRunning(taskDef));
        }
        return taskDefs;
    }

    @Override
    @Secured("Perm_Add_Tasks")
	public void saveTask(TaskDef taskDef) {
        taskDAO.saveTask(taskDef);
    }

    @Override
	@Transactional(readOnly = true)
	@Secured("Perm_Task_Scheduling")
    public Boolean startTask(TaskDef taskDef) {
        return scheduler.startTask(taskDef);
    }

    @Override
	@Transactional(readOnly = true)
	@Secured("Perm_Task_Scheduling")
    public Boolean stopTask(TaskDef taskDef) {
        return scheduler.stopTask(taskDef);
    }

	@Override
	@Secured("Perm_View_Tasks")
	public TaskDef getTask(String taskName) {
		return taskDAO.getTask(taskName);
	}
}
