package org.openxdata.server.service;

import java.util.List;

import org.openxdata.server.admin.model.TaskDef;

/**
 * This service is used for 
 * managing <code>Tasks</code>.
 * 
 * @author Angel
 *
 */
public interface TaskService {

    /**
     * Gets a list of task definitions from the database.
     *
     * @return the task list.
     */
    List<TaskDef> getTasks();

    /**
     * Saves a task definition to the database.
     *
     * @param task the task definition to save.
     */
    void saveTask(TaskDef taskDef);

    /**
     * Deletes a task definition from the database.
     *
     * @param task the task definition to delete.
     */
    void deleteTask(TaskDef taskDef);

    /**
     * Starts running a given task.
     *
     * @param taskDef the task definition.
     * @return true if the tasks starts successfully, else false.
     */
    Boolean startTask(TaskDef taskDef);

    /**
     * Stops a task from running.
     *
     * @param taskDef the task definition object.
     * @return true if the tasks starts successfully, else false.
     */
    Boolean stopTask(TaskDef taskDef);
    
    /**
     * Retrieves a <tt>TaskDef</tt> given a name.
     * 
     * @param taskName Name of Task to retrieve.
     * 
     * @return TaskDef if exists.
     */
    TaskDef getTask(String taskName);

}
