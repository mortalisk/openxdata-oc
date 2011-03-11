package org.openxdata.server.admin.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a schedulable task definition. A task is code which can be run
 * at a schedule to do certain tasks. Examples include: code run every Monday to Friday
 * at midnight to export data from xml to relational tables, a bluetooth or sms service 
 * automatically started every time the server starts up to listen to bluetooth and sms 
 * connections respectively, and more.
 */
public class TaskDef extends AbstractEditable{

	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = -4121837688631289120L;

	/** The database identifier for the task definition. */
	private int taskId = 0;
	
	/** The name. */
	private String name;
	
	/** The description. */
	private String description;
	
	/** The fully qualified name of the class to be instantiated to run the task. */
	private String taskClass;
	
	/** Set to true if the task is to be automatically started every time the server starts. */
	private Boolean startOnStartup;
	
	/** The cron expression used to define the schedule on which the task is to be run.
	 * If startOnStartup is set, the cron expression is ignored.
	 */
	private String cronExpression;
	
	/** A flag to check whether the task has been started by the server or not.
	 * This is not persisted to the database. It is only used at runtime to check
	 * if the task is already started or not and update the UI accordingly.
	 */
	private boolean running = false;
	
	
	/** A list of the task parameters. */
	private List<TaskParam> parameters;
	
	/**
	 * Constructs a new task definition object.
	 */
	public TaskDef(){
		startOnStartup = true;								// as we have a default date populated
	}
	
	/**
	 * Constructs a new task definition object with a given name.
	 * 
	 * @param name the name of the task.
	 */
	public TaskDef(String name){
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getTaskId() {
		return taskId;
	}

	@Override
	public int getId() {
		return taskId;
	}
	
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public String getTaskClass() {
		return taskClass;
	}

	public void setTaskClass(String taskClass) {
		this.taskClass = taskClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean isStartOnStartup() {
		return startOnStartup;
	}

	public void setStartOnStartup(Boolean startOnStartup) {
		this.startOnStartup = startOnStartup;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public List<TaskParam> getParameters() {
		return parameters;
	}

	public void setParameters(List<TaskParam> parameters) {
		this.parameters = parameters;
	}
	
	public TaskParam getParamAt(int index){
		if(parameters == null || index > parameters.size() - 1)
			return null;
		return parameters.get(index);
	}
	
	public void deleteParamAt(int index){
		if(parameters == null || index > parameters.size() - 1)
			return;
		parameters.remove(index);
	}
	
	public void addParam(TaskParam param){
		if(parameters == null)
			parameters = new ArrayList<TaskParam>();
		
		parameters.add(param);
	}
	
	public void deleteParam(TaskParam param){
		parameters.remove(param);
	}
	
	public String getParamValue(String name){
		if(parameters == null)
			return null;
		
		for(TaskParam param : parameters){
			if(param.getName().equalsIgnoreCase(name))
				return param.getValue();
		}
		
		return null;
	}
	
	@Override
	public boolean isNew(){
		return taskId == 0;
	}
}
