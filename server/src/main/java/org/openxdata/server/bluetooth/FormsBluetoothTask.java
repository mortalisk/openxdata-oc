package org.openxdata.server.bluetooth;

import org.apache.log4j.Logger;
import org.openxdata.server.Task;
import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.service.SchedulerService;
import org.openxdata.server.sms.OpenXDataAbstractJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


/**
 * This class is the task for processing bluetooth connections to OpenXdata.
 * 
 * @author daniel
 *
 */
public class FormsBluetoothTask extends OpenXDataAbstractJob implements Task{

	/** The logger. */
	private Logger log = Logger.getLogger(this.getClass());

	/** The task definition. */
	private TaskDef taskDef;
	
	/** The forms bluetooth server. */
	private FormsBluetoothServer server;
	
	/** Flag to tell whether this task is running. */
	private boolean running = false;
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		SchedulerService schedulerService = (SchedulerService) getBean("schedulerService",context);
		try{
			if(taskDef == null){
				taskDef = (TaskDef)context.getJobDetail().getJobDataMap().get("taskdef");
				schedulerService.registerTaskRunningInstance(this);
			}
			
			String uuid = taskDef.getParamValue("ServerUUID");
			String name = taskDef.getParamValue("ServiceName");
			
			System.out.println("Starting "+ name + " at address: "+uuid);
			
			server = new FormsBluetoothServer(name,uuid);
			server.start();
			
			running = true;
			
			System.out.println("Running "+ name + " at address: "+uuid);
		}
		catch(Exception ex){
			log.error(ex.getMessage(), ex);
		}
	}
	
	/**
	 * @see org.openxdata.server.Task#init()
	 */
	public void init(TaskDef taskDef){
		this.taskDef = taskDef;
	}
	
	/**
	 * @see org.openxdata.server.Task#stop()
	 */
	@Override
	public void stop(){
		
		String uuid = taskDef.getParamValue("ServerUUID");
		String name = taskDef.getParamValue("ServiceName");
		
		System.out.println("Stopping "+ name + " at address: "+uuid);
		
		if(server != null)
			server.stop();
		
		System.out.println("Stopped "+ name + " at address: "+uuid);
		
		taskDef = null;
		server = null;
		
		running  = false;
	}
	
	/**
	 * @see org.openxdata.server.Task#getTaskDef()
	 */
	@Override
	public TaskDef getTaskDef(){
		return taskDef;
	}
	
	/**
	 * @see org.openxdata.server.Task#isRunning()
	 */
	@Override
	public boolean isRunning(){
		return running;
	}
}
