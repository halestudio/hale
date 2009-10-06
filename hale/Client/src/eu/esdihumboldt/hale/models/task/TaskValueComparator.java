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
package eu.esdihumboldt.hale.models.task;

import java.util.Comparator;

import eu.esdihumboldt.hale.task.Task;

/**
 * FIXME Add Type description.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class TaskValueComparator 
	implements Comparator<Task> {

	/**
	 * @return -1 if t1.value is lower than t2.value, +1 if t1.value is higher
	 * than t2.value. In case of equal values, the Task titles will be used in 
	 * comparison. Only if these are equal, 0 will be returned.
	 */
	public int compare(Task t1, Task t2) {
		double absolute_result = t1.getValue() - t2.getValue();
		if (absolute_result == 0) {
			return t1.getTaskTitle().hashCode() - t2.getTaskTitle().hashCode();
		}
		return new Double(Math.signum(absolute_result)).intValue();
	}

}
