package org.openxdata.server.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openxdata.server.Task;
import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.admin.model.exception.UnexpectedException;
import org.openxdata.server.dao.TaskDAO;
import org.openxdata.server.service.SchedulerService;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Default implementation for scheduler service.
 * 
 * @author daniel
 *
 */
public class SchedulerServiceImpl implements SchedulerService {
	
	private Logger log = LoggerFactory.getLogger(SchedulerServiceImpl.class);
	
	/** The quart scheduler. */
	private Scheduler scheduler;
	
	@Autowired
	private TaskDAO taskDAO;
	
	private TransactionTemplate transactionTemplate;
	
	/** Map of tasks keyed by their task identifiers. */
	private Map<Integer, Task> tasks = new HashMap<Integer, Task>();

	@Autowired
	private PlatformTransactionManager transactionManager;

    void setTaskDAO(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    @Override
	public void start(){
    	if(scheduler == null)
			return;
    	
    	/* 
    	 * Use programatic transaction management because
    	 * this method is called by spring via reflection.
    	 * 
    	 * AOP based transaction management does not work in
    	 * conjunction with reflection.
    	 * 
    	 * @see http://www.eclipse.org/aspectj/doc/released/faq.php#q:reflectiveCalls
    	 * 
    	 */
    	this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setReadOnly(true);
    	
    	transactionTemplate.execute(new TransactionCallbackWithoutResult() {
		    @Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
		        try {
		        	List<TaskDef> taskDefs = taskDAO.getTasks();
	
					if (taskDefs != null) {
						for(TaskDef taskDef : taskDefs) {
							scheduleTask(taskDef);
						}
					}
	
					scheduler.start();
					log.info("Started Scheduling Service");
		        } catch (SchedulerException ex) {
		            status.setRollbackOnly();
		            throw new UnexpectedException(ex);
		        }
		    }
		});
	}

	@Override
	public void stop(){
		try{
			if(scheduler != null)
				scheduler.shutdown();
		}
		catch(Exception ex){
			throw new UnexpectedException(ex);
		}
	}

	@Override
	public boolean startTask(TaskDef taskDef){
		return scheduleTask(taskDef);
	}

	@Override
	public boolean stopTask(TaskDef taskDef){
		Task task = getTask(taskDef);
		if(task == null){
			log.warn("Attempted to stop a non registered task");
			return false;
		}

		task.stop();

		try{
			scheduler.unscheduleJob(taskDef.getName(),"DEFAULT");
			return true;
		}
		catch(Exception ex){
			throw new UnexpectedException(ex);
		}
	}
	
	@Override
	public void registerTaskRunningInstance(Task task){
		tasks.put(task.getTaskDef().getId(),task);
	}

	@Override
	public boolean isTaskRunning(TaskDef taskDef){
		Task task = getTask(taskDef);
		if(task == null)
			return false;
		
		return task.isRunning();
	}
	
	/**
	 * Schedules a task as identified by the task definition object.
	 * 
	 * @param taskDef the task definition object.
	 * @return true if the task was scheduled successfully, else false.
	 */
	private boolean scheduleTask(TaskDef taskDef){
		try{
			Class<?> cls = getClass(taskDef);

			JobDetail jobDetail = new JobDetail();
			jobDetail.setJobClass(cls);
			jobDetail.setName(taskDef.getName());

			JobDataMap dataMap = new JobDataMap();
			dataMap.put("taskdef", taskDef);
			jobDetail.setJobDataMap(dataMap);

			Trigger trigger = null;
			String cronExpression = taskDef.getCronExpression();
			if(cronExpression != null && cronExpression.trim().length() > 0 && !taskDef.isStartOnStartup()){
				trigger = new CronTriggerBean();
				((CronTriggerBean)trigger).setJobDetail(jobDetail);
				((CronTriggerBean)trigger).setCronExpression(cronExpression); // /*"1 * * * * ?"*/
			}
			else if(taskDef.isStartOnStartup())
				trigger = TriggerUtils.makeImmediateTrigger(0, 0);
			else
				log.info("Task " + taskDef.getName() + " not scheduled");

			if(trigger != null){
				trigger.setName(taskDef.getName());
				scheduler.scheduleJob(jobDetail,trigger);
				return true;
			}
		} catch (ClassNotFoundException ex) {
			log.error("Service class not found for: " + taskDef.getName());
        } catch(Exception ex){
			log.error("Schedule task failed", ex);
		}
		
		return false;
	}
	
	/**
	 * Gets the Class responsible for running a task identified by the task definition.
	 * 
	 * @param taskDef the task definition.
	 * @return the Class object for the task class.
	 */
	private Class<?> getClass(TaskDef taskDef) throws ClassNotFoundException {
		return Class.forName(taskDef.getTaskClass());
	}

	/**
	 * Gets the task identified by a task definition object.
	 * 
	 * @param taskDef the task definition object.
	 * @return the task object.
	 */
	private Task getTask(TaskDef taskDef){
		return tasks.get(taskDef.getId());
	}


	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
}
