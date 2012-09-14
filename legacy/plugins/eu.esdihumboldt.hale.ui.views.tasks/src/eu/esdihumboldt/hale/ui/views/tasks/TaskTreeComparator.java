/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.views.tasks;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.ui.util.tree.AbstractMultiColumnTreeNode;
import eu.esdihumboldt.hale.ui.util.tree.DefaultTreeNode;
import eu.esdihumboldt.hale.ui.views.tasks.model.ResolvedTask;

/**
 * Default task comparator
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class TaskTreeComparator extends ViewerComparator {

	/**
	 * @see ViewerComparator#compare(Viewer, Object, Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof DefaultTreeNode && e2 instanceof DefaultTreeNode) {
			// task nodes
			Object value1 = ((DefaultTreeNode) e1).getFirstValue();
			Object value2 = ((DefaultTreeNode) e2).getFirstValue();
			
			if (value1 instanceof ResolvedTask && value2 instanceof ResolvedTask) {
				return compareTasks((ResolvedTask) value1, (ResolvedTask) value2);
			}
		}
		else if (e1 instanceof AbstractMultiColumnTreeNode && e2 instanceof AbstractMultiColumnTreeNode) {
			// parent nodes
			Object value1 = ((AbstractMultiColumnTreeNode) e1).getFirstValue();
			Object value2 = ((AbstractMultiColumnTreeNode) e2).getFirstValue();
			
			if (value1 instanceof SchemaElement && value2 instanceof SchemaElement) {
				return getComparator().compare( 
						((SchemaElement) value1).getIdentifier(),
						((SchemaElement) value2).getIdentifier());
			}
			else {
				return getComparator().compare(value1, value2);
			}
		}
		
		// fall back
		return super.compare(viewer, e1, e2);
	}

	/**
	 * Compare the given tasks
	 * 
	 * @param task1 the first task
	 * @param task2 the second task
	 * 
	 * @return a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object. 
	 */
	protected int compareTasks(ResolvedTask task1, ResolvedTask task2) {
		//return task1.compareTo(task2);
		
		int result = task1.getTaskStatus().compareTo(task2.getTaskStatus());
		
		if (result == 0) {
			result = task1.getSeverityLevel().compareTo(task2.getSeverityLevel());
		}
			
		if (result == 0) {
			if (task1.getValue() > task2.getValue()) {
				return -1;
			}
			else if (task1.getValue() < task2.getValue()) {
				return 1;
			}
		}
		
		if (result == 0) {
			result = task1.getTask().compareTo(task2.getTask());
		}
		
		return result;
	}

}
