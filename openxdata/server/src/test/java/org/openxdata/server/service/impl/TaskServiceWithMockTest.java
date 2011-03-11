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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataException;
import org.openxdata.server.dao.TaskDAO;
import org.openxdata.server.service.SchedulerService;

/**
 * 
 * @author Jonny Heggheim
 *
 */
public class TaskServiceWithMockTest {

    private TaskServiceImpl service;
    private TaskDAO taskDaoMock;
    private SchedulerService schedulerMock;
    private TaskDef dummyTask;
    private List<TaskDef> taskDefs;

    @Before
    public void initalizeTaskServiceAndMock() {
        taskDaoMock = createMock(TaskDAO.class);
        schedulerMock = createMock(SchedulerService.class);
        service = new TaskServiceImpl();
        service.setScheduler(schedulerMock);
        service.setTaskDAO(taskDaoMock);
    }

    @Before
    public void createDummyTaskDef() {
        dummyTask = new TaskDef();
        dummyTask.setName("Dummy");
        dummyTask.setCreator(new User("Dummy"));
        dummyTask.setDateCreated(new Date());
    }

    @Before
    public void createTaskDefs() {
        taskDefs = new LinkedList<TaskDef>();
        taskDefs.add(new TaskDef("Forms Bluetooth"));
        taskDefs.add(new TaskDef("Data Export"));
        taskDefs.add(new TaskDef("Forms SMS"));
    }

    @Test
    public void getTasksShouldReturnAllTasks() throws OpenXDataException {
        expect(taskDaoMock.getTasks()).andReturn(taskDefs);
        expect(schedulerMock.isTaskRunning((TaskDef) anyObject())).andReturn(Boolean.FALSE).times(3);
        replay(taskDaoMock, schedulerMock);
        
        List<TaskDef> tasks = service.getTasks();
        assertEquals(3, tasks.size());
        for(int i = 0; i < tasks.size(); i++){
            assertEquals(taskDefs.get(i).getName(), tasks.get(i).getName());
        }

        verify(taskDaoMock, schedulerMock);
    }

    @Test(expected=NullPointerException.class)
    public void getTaskShouldGiveANullPointerExceptionWhenDaoReturnsNull() throws OpenXDataException {
        expect(taskDaoMock.getTasks()).andReturn(null);
        replay(taskDaoMock);
        service.getTasks();
    }

    @Test
    public void getTaskShouldCheckIfTasksIsRunning() throws OpenXDataException {
        TaskDef runningTask = taskDefs.get(0);
        TaskDef notRunningTask = taskDefs.get(1);
        expect(taskDaoMock.getTasks()).andReturn(taskDefs.subList(0, 2));
        expect(schedulerMock.isTaskRunning(runningTask)).andReturn(Boolean.TRUE);
        expect(schedulerMock.isTaskRunning(notRunningTask)).andReturn(Boolean.FALSE);
        replay(taskDaoMock, schedulerMock);

        List<TaskDef> tasks = service.getTasks();
        runningTask = tasks.get(0);
        notRunningTask = tasks.get(1);

        assertEquals(2, tasks.size());
        assertTrue(runningTask.isRunning());
        assertFalse(notRunningTask.isRunning());
        verify(taskDaoMock, schedulerMock);
    }

    @Test
    public void saveTaskShouldCallSaveTaskOnDAO() throws OpenXDataException {
        taskDaoMock.saveTask(dummyTask);
        replay(taskDaoMock);
        service.saveTask(dummyTask);
        verify(taskDaoMock);
    }

    @Test
    public void deleteTaskShouldCallDeleteOnDAO() throws OpenXDataException {
        taskDaoMock.deleteTask(dummyTask);
        replay(taskDaoMock);
        service.deleteTask(dummyTask);
        verify(taskDaoMock);
    }

    @Test
    public void startTaskShouldForwardToScheduler() throws OpenXDataException {
        expect(schedulerMock.startTask(dummyTask)).andReturn(Boolean.TRUE);
        replay(schedulerMock);
        assertTrue(service.startTask(dummyTask));
        verify(schedulerMock);
    }

    @Test
    public void stopTaskShouldForwatdToScheduler() throws OpenXDataException {
        expect(schedulerMock.stopTask(dummyTask)).andReturn(Boolean.TRUE);
        replay(schedulerMock);
        assertTrue(service.stopTask(dummyTask));
        verify(schedulerMock);
    }
}
