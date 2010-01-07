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
package eu.esdihumboldt.hale.models.provider;

import java.util.Set;

import eu.esdihumboldt.hale.task.Task;

/**
 * A TaskProvider generates new tasks from a given input.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public interface TaskProvider<T> {
	
	/**
	 * @param baseObject the Object to use as input for generating new 
	 * {@link Task}s
	 * @return a {@link Set} of {@link Task} objects.
	 */
	public Set<Task> createTasks(T baseObject);

	/**
	 * @return a class name as a string identifying the expected input type.
	 */
	public String getSupportedInputType();

}
