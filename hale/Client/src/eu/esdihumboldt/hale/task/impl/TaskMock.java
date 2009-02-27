package eu.esdihumboldt.hale.task.impl;

import java.util.Set;

import eu.esdihumboldt.hale.task.Task;
import eu.esdihumboldt.hale.task.TaskSource;
import eu.esdihumboldt.modelrepository.abstractfc.SchemaElement;

/**
 * A simple Mock for a Task.
 * @author cjauss
 *
 */
public class TaskMock implements Task{
	
	private SeverityLevel severityLevel;
	private String title;
	
	public TaskMock(SeverityLevel _level, String _title){
		this.severityLevel = _level;
		this.title = _title;
	}

	public SeverityLevel getSeverityLevel() {
		return severityLevel;
	}

	public TaskSource getSource() {
		return new TaskSourceMock();
	}

	public String getTaskTitle() {
		return title;
	}

	public String getTaskType(){
		return "Task Mockup";
	}

	public double getValue() {
		return 1.0;
	}
	
	// not needed in this Mock
	public Set<SchemaElement> getTaskContextElements() {
		return null;
	}
}
