package eu.esdihumboldt.hale.task.impl;

import eu.esdihumboldt.hale.rcp.views.tasks.Tasklist;
import eu.esdihumboldt.hale.task.Task.SeverityLevel;


/**
 * A Mock for a Tasklist to test the TasklistView.
 * @author cjauss
 *
 */
public class TasklistMock extends Tasklist{
	
	public TasklistMock(){
		super();
		initData();
	}
	
	private void initData(){
		TaskMock task1 = new TaskMock(SeverityLevel.error,"Datatype mapping failed");
		TaskMock task2 = new TaskMock(SeverityLevel.warning,"Mapping between ...");
		TaskMock task3 = new TaskMock(SeverityLevel.task,"Attribute X and Y are need to be mapped.");
		addTask(task1);
		addTask(task2);
		addTask(task3);
		}
}