package org.openxdata.server.service.impl;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.service.TaskService;
import org.openxdata.server.service.UserService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 
 * Tests methods in the TaskService which deal with TaskDef
 * 
 * @author daniel
 *
 */
public class TaskServiceTest  extends BaseContextSensitiveTest {

	@Autowired
	protected TaskService tasksService;
	
	@Autowired
	protected UserService userService;
	
	@Test
	public void getTasks_shouldReturnAllTasks() throws Exception {
		List<TaskDef> tasks = tasksService.getTasks();
		
		Assert.assertNotNull(tasks);
		Assert.assertEquals(3, tasks.size());
		
		for(TaskDef taskDef : tasks){		
			String name = taskDef.getName();		
			Assert.assertTrue(name.equals("Forms Bluetooth") || name.equals("Data Export") || name.equals("Forms SMS"));
		}
	}

	@Test
	public void saveTask_shouldSaveTask() throws Exception {
		final String taskName = "TaskName";
		
		TaskDef task = new TaskDef();
		task.setName(taskName);
		task.setCreator(userService.getUsers().get(0));
		task.setDateCreated(new Date());
		
		tasksService.saveTask(task);
		
		List<TaskDef> tasks = tasksService.getTasks();
		Assert.assertEquals(4,tasks.size());
		Assert.assertNotNull(tasksService.getTask(taskName));
	}

	@Test
	public void deleteTask_shouldDeleteGivenTask() throws Exception {
		
		final String taskName = "Forms Bluetooth";
		
		List<TaskDef> tasks = tasksService.getTasks();
		Assert.assertEquals(3,tasks.size());
		
		TaskDef taskDef = tasksService.getTask(taskName);
		Assert.assertNotNull(taskDef);
	
		tasksService.deleteTask(taskDef);
		
		tasks = tasksService.getTasks();
		Assert.assertEquals(2,tasks.size());
		Assert.assertNull(tasksService.getTask(taskName));
	}
	
	@Test
	public void getTask(){
		assertNotNull(tasksService.getTask("Forms Bluetooth"));
		
	}
}
