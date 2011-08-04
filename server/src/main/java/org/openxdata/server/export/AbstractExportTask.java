package org.openxdata.server.export;

import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.openxdata.server.Task;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.service.DataExportService;
import org.openxdata.server.service.SchedulerService;
import org.openxdata.server.sms.OpenXDataAbstractJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractExportTask extends OpenXDataAbstractJob implements Task {

	/**
	 * Runnable class to handle executing the Task outside of Quartz
	 * @author dagmar@cell-life.org.za
	 */
	class ExportFormDataThread implements Runnable {
	    FormData formData;
	    FormDefVersion formDefVersion;
	    public ExportFormDataThread(FormData formData, FormDefVersion formDefVersion) {
	        this.formData = formData;
	        this.formDefVersion = formDefVersion;
	    }
        @Override
		public void run() {
            exportFormData(formData, formDefVersion);
        }
	}
	
	private static Logger log = LoggerFactory.getLogger(AbstractExportTask.class);
	
	/** thread pool to manage the direct invocation of the task (without quartz) */
	private static final ThreadPoolExecutor tpe = new ThreadPoolExecutor(5, 10, 3600, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	
	/** cache of form definitions */
	private Hashtable<Integer, FormDefVersion> formDefCache = new Hashtable<Integer, FormDefVersion>();

	@Autowired
	protected DataExportService dataExportService;
	
	private TaskDef taskDef = null;
	
	/** flag to indicate if the task is executing or not */
	private boolean running = false;
	
	public AbstractExportTask() {
		super();
	}
	
	public AbstractExportTask(TaskDef taskDef) {
		super();
		this.taskDef = taskDef;
	}

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
	
		SchedulerService schedulerService = (SchedulerService) getBean("schedulerService", context);
		dataExportService = (DataExportService) getBean("dataExportService", context);
	
		if (taskDef == null) {
			taskDef = (TaskDef) context.getJobDetail().getJobDataMap().get("taskdef");
			schedulerService.registerTaskRunningInstance(this);
			init();
		}
	
		List<FormData> dataList = dataExportService.getFormDataToExport(ExportConstants.EXPORT_BIT_HTTP_POST);
		log.info("Running Data Export Service to export " + dataList.size()	+ " form data items");
		running = true;
	
		for (int index = 0; index < dataList.size(); index++) {
			FormData formData = dataList.get(index);
			exportFormData(formData, false);
		}
	}

	protected void init() {
		// default implementation
	}

	/**
	 * Exports the specified FormData
	 * 
	 * @param formData
	 *            FormData to exports
	 * @param threaded
	 *            boolean true if the export must be run in a thread (only for
	 *            outside of quartz execution)
	 */
	public void exportFormData(FormData formData, boolean threaded) {
		if (formData == null)
			return;

		FormDefVersion formDefVersion = (FormDefVersion) formDefCache.get(new Integer(formData.getFormDefVersionId()));
		if (formDefVersion == null) {
			formDefVersion = dataExportService.getFormDefVersion(formData.getFormDefVersionId());
			formDefCache.put(new Integer(formData.getFormDefVersionId()), formDefVersion);
		}

		if (threaded) {
			tpe.execute(new ExportFormDataThread(formData, formDefVersion));
		} else {
			exportFormData(formData, formDefVersion);
		}
	}

	public abstract void exportFormData(FormData formData, FormDefVersion formDefVersion);

	@Override
	public boolean isRunning() {
		return running;
	}
	
	protected void setRunning(boolean running){
		this.running = running;
	}

	public void setDataExportService(DataExportService dataExportService) {
		this.dataExportService = dataExportService;
	}
	
	@Override
	public TaskDef getTaskDef() {
		return taskDef;
	}
	
	@Override
	public void stop() {
		log.info("Stopping Data Export Service");
		taskDef = null;
		running = false;
	}
}