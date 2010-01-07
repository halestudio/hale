package eu.esdihumboldt.hale.task.impl;

import eu.esdihumboldt.hale.task.TaskSource;

/**
 * A simple Mock for a TaskSource.
 * @author cjauss
 *
 */
public class TaskSourceMock implements TaskSource{

	public String getImplementationName(){
		return "TaskSourceMock";
	}

	public String getTaskCreationReason(){
		return "create to test the TasklistView";
	}
}
