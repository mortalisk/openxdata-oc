package org.openxdata.server.sms;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

public abstract class OpenXDataAbstractJob extends QuartzJobBean {
	
	private static final String APPLICATION_CONTEXT_KEY = "applicationContext";
	
	public OpenXDataAbstractJob() {
		super();
	}

	protected ApplicationContext getApplicationContext(JobExecutionContext context) throws JobExecutionException {
	    ApplicationContext appCtx = null;
	    try {
	        appCtx = (ApplicationContext)context.getScheduler().getContext().get(APPLICATION_CONTEXT_KEY);
	        if (appCtx == null) {
	            throw new JobExecutionException(
	                    "No application context available in scheduler context for key \"" + APPLICATION_CONTEXT_KEY + "\"");
	        }
	        return appCtx;
	    }catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}
	
	protected Object getBean(String beanName, JobExecutionContext context) throws JobExecutionException{
		ApplicationContext applicationContext = getApplicationContext(context);
		return applicationContext.getBean(beanName);
	}

}