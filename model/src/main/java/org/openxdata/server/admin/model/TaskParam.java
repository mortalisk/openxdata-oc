package org.openxdata.server.admin.model;

/**
 * This class represents a parameter for a schedulable task.
 */
public class TaskParam  extends AbstractEditable{
	
	private static final long serialVersionUID = 5620957823593973227L;
	
	/** The task definition to which this parameter belongs. */
	private TaskDef taskDef;
	
	/** The name of the parameter. */
	private String name;
	
	/** The value of the parameter. */
	private String value;
	
	public TaskParam(){
		
	}

	/**
	 * Constructs a new task parameter for a given a task definition, and with
	 * a given parameter name and value.
	 * 
	 * @param taskDef the task definition.
	 * @param name the parameter name.
	 * @param value the parameter value.
	 */
	public TaskParam(TaskDef taskDef,String name, String value){
		this.taskDef = taskDef;
		this.name= name;
		this.value= value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TaskDef getTaskDef() {
		return taskDef;
	}

	public void setTaskDef(TaskDef taskDef) {
		this.taskDef = taskDef;
	}

	@Override
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}	
}
