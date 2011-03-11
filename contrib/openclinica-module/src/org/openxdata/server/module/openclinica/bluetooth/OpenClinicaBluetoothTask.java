package org.openxdata.server.module.openclinica.bluetooth;

import org.apache.log4j.Logger;
import org.openxdata.server.Task;
import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.context.Context;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;


/**
 * 
 * @author daniel
 *
 */
public class OpenClinicaBluetoothTask extends QuartzJobBean implements Task{

	/** The logger. */
	//private Log log = LogFactory.getLog(FormsBluetoothTask.class );
	private Logger log = Logger.getLogger(this.getClass());

	/** The task definition. */
	private TaskDef taskDef;
	
	/** The forms bluetooth server. */
	private OpenClinicaBluetoothServer server;
	
	/** Flag to tell whether this task is running. */
	private boolean running = false;
	
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		
		try{
			if(taskDef == null){
				taskDef = (TaskDef)context.getJobDetail().getJobDataMap().get("taskdef");
				Context.getSchedulerService().registerTaskRunningInstance(this);
			}
			
			String uuid = taskDef.getParamValue("ServerUUID");
			String name = taskDef.getParamValue("ServiceName");
			
			System.out.println("Starting "+ name + " at address: "+uuid);
			
			server = new OpenClinicaBluetoothServer(name,uuid);
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
	public TaskDef getTaskDef(){
		return taskDef;
	}
	
	/**
	 * @see org.openxdata.server.Task#isRunning()
	 */
	public boolean isRunning(){
		return running;
	}
}
