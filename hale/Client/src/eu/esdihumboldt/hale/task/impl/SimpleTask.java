/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.task.impl;

import java.util.Set;

import eu.esdihumboldt.hale.task.Task;
import eu.esdihumboldt.hale.task.TaskSource;
import eu.esdihumboldt.modelrepository.abstractfc.SchemaElement;

/**
 * A simple {@link Task} implementation.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SimpleTask implements Task {

	private SeverityLevel severityLevel;
	private String title;
	private TaskStatus status;
	private TaskSource source = null;

	public SimpleTask(SeverityLevel _level, String _title) {
		this.severityLevel = _level;
		this.title = _title;
		this.status = TaskStatus.NEW;
	}

	/**
	 * @param task
	 * @param string
	 * @param schemaLoadingTaskProvider
	 */
	public SimpleTask(SeverityLevel task, String title,
			TaskSource taskSource) {
		this(task, title);
		this.source = taskSource;
	}

	public SeverityLevel getSeverityLevel() {
		return severityLevel;
	}

	public TaskSource getSource() {
		return this.source;
	}

	public String getTaskTitle() {
		return title;
	}

	public String getTaskType() {
		return "SimpleTask";
	}

	public double getValue() {
		return 100 * Math.random(); // FIXME
	}

	public Set<SchemaElement> getTaskContextElements() {
		return null; // FIXME
	}

	/**
	 * @see eu.esdihumboldt.hale.task.Task#getTaskStatus()
	 */
	public TaskStatus getTaskStatus() {
		return this.status;
	}
}
