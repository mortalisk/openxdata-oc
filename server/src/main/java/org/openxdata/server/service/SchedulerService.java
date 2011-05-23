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
