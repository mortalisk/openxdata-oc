package org.openxdata.server.admin.client.service;

import java.util.List;

import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Defines the client side contract for the Task Service.
 */
public interface TaskService extends RemoteService {
	
	/**
	 * Fetches all the <tt>Tasks</tt> in the system.
	 * 
	 * @return <tt>List</tt> of <tt>Tasks.</tt>
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	List<TaskDef> getTasks() throws OpenXDataSecurityException;
	
	/**
	 * Saves a dirty or new <tt>Task.</tt>
	 * 
	 * @param task <tt>Task</tt> to save.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void saveTask(TaskDef task) throws OpenXDataSecurityException;
	
	/**
	 * Deletes a given <tt>Task.</tt>
	 * 
	 * @param task <tt>Task</tt> to save.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void deleteTask(TaskDef task) throws OpenXDataSecurityException;
	
	/**
	 * Starts a given <tt>Task.</tt> After this operation, the status of the <tt>Task</tt> will be started/running.
	 * 
	 * @param task <tt>Task</tt> to start.
	 * @return <tt>True if started else false.</tt>
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	Boolean startTask(TaskDef task) throws OpenXDataSecurityException;
	
	/**
	 * Stops a given <tt>Task.</tt> After this operation, the status of the <tt>Task</tt> will be stopped.
	 * 
	 * @param task <tt>Task</tt> to stop.
	 * @return <tt>True if stopped else false.</tt>
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	Boolean stopTask(TaskDef task) throws OpenXDataSecurityException;
}
