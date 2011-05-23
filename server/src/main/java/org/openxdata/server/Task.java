package org.openxdata.server;

import org.openxdata.server.admin.model.TaskDef;


/**
 * This interface should be implemented by all schedulable tasks to enable
 * the scheduler framework manage tasks.
 * 
 * @author daniel
 *
 */
public interface Task {
	
	/**
	 * Stops the task from running.
	 */
	public void stop();
	
	/**
	 * Gets the task definition for the task.
	 * 
	 * @return the task definition.
	 */
	public TaskDef getTaskDef();
	
	/**
	 * Checks if a task is running.
	 * 
	 * @return true if running, else false.
	 */
	public boolean isRunning();
}
