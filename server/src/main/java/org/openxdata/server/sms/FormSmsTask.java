/*
 *  Licensed to the OpenXdata Foundation (OXDF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The OXDF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, 
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 *  Copyright 2010 http://www.openxdata.org.
 */
package org.openxdata.server.sms;

import org.apache.log4j.Logger;
import org.openxdata.server.Task;
import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.service.SchedulerService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * This class is the sms task for processing sms connections to OpenXdata.
 * 
 * @author daniel
 *
 */
public class FormSmsTask extends OpenXDataAbstractJob implements Task{
	
	/** The definition of the task that we represent. */
	private TaskDef taskDef;

	/** The logger for exceptions, warnings, or informational messages. */
	private Logger log = Logger.getLogger(this.getClass());
	
	/** Flag to tell whether this task is running. */
	private boolean running = false;

	private SmsProcessor smsProcessor;
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		
		SchedulerService schedulerService = (SchedulerService) getBean("schedulerService",context);
		smsProcessor = (SmsProcessor) getBean("smsProcessor",context);
		
		try{
			if(taskDef == null){
				taskDef = (TaskDef)context.getJobDetail().getJobDataMap().get("taskdef");
				schedulerService.registerTaskRunningInstance(this);
			}
			
			System.out.println("Runing " + taskDef.getParamValue("gatewayId") + " sms service");
						
			smsProcessor.setGatewayId(taskDef.getParamValue("gatewayId"));
			smsProcessor.setComPort(taskDef.getParamValue("comPort"));
			smsProcessor.setSourcePort(Integer.parseInt(taskDef.getParamValue("srcPort")));
			smsProcessor.setDestinationPort(Integer.parseInt(taskDef.getParamValue("dstPort")));
			smsProcessor.setBaudRate(Integer.parseInt(taskDef.getParamValue("baudRate")));
			smsProcessor.setModemManufacturer(taskDef.getParamValue("modemManufacturer"));
			smsProcessor.setModemModel(taskDef.getParamValue("modemModel"));
			
			String value = taskDef.getParamValue("useInboundSmsPolling");
			smsProcessor.setUseInboundPolling("true".equals(value) ? true : false);
			
			value = taskDef.getParamValue("inboundSmsPollingInterval");
			if(value == null || value.trim().length() == 0)
				value = "30";
			smsProcessor.setInboundPollingInterval(Integer.parseInt(value)*1000);
			
			smsProcessor.start();
			
			running = true;
			
			System.out.println("sms service started");
		}
		catch(Exception ex){
			log.error(ex.getMessage(), ex);
		}
	}

	/**
	 * @see org.openxdata.server.Task#init()
	 */
	public void init(TaskDef taskDef) {
		this.taskDef = taskDef;
	}

	/**
	 * @see org.openxdata.server.Task#stop()
	 */
	@Override
	public void stop() {
		String name = taskDef.getParamValue("gatewayId");
		String port = taskDef.getParamValue("comPort");
		
		System.out.println("Stopping "+ name + " at port: "+port);
		
		/*if(server != null)
			server.stop();*/
		
		if(smsProcessor != null)
			smsProcessor.stop();
		
		System.out.println("Stopped "+ name + " at port: "+port);
		
		//server = null;
		smsProcessor = null;
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
