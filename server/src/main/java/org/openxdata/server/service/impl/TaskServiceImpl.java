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
