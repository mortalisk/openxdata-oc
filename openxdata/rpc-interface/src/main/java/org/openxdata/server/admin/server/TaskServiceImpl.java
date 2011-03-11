package org.openxdata.server.admin.server;

import java.util.List;

import javax.servlet.ServletException;

import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.rpc.OxdPersistentRemoteService;
import org.springframework.web.context.WebApplicationContext;

/**
 * Default Implementation for the <code>TaskService Interface.</code>
 */
public class TaskServiceImpl extends OxdPersistentRemoteService implements
org.openxdata.server.admin.client.service.TaskService {

	/**
	 * Generated Serialisation ID.
	 */
	private static final long serialVersionUID = -7724890075405637511L;
	
	private org.openxdata.server.service.TaskService taskService;
	
	public TaskServiceImpl() {}
	
	@Override
	public void init() throws ServletException {
		super.init();
		WebApplicationContext ctx = getApplicationContext();
		taskService = (org.openxdata.server.service.TaskService)ctx.getBean("taskService");
	}
	
	@Override
	public void deleteTask(TaskDef task) {
		taskService.deleteTask(task);
	}

	@Override
	public List<TaskDef> getTasks() {
		return taskService.getTasks();
	}

	@Override
	public void saveTask(TaskDef task) {
		taskService.saveTask(task);
		
	}

	@Override
	public Boolean startTask(TaskDef taskDef) {
		return taskService.startTask(taskDef);
	}

	@Override
	public Boolean stopTask(TaskDef taskDef) {
		return taskService.stopTask(taskDef);
	}

}
