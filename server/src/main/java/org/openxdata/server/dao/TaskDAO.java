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
