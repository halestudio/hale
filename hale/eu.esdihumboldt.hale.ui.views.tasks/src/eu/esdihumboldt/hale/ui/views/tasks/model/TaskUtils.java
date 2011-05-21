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

package eu.esdihumboldt.hale.ui.views.tasks.model;

import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;

/**
 * Task utiltiy methods
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class TaskUtils {
	
	/**
	 * Get the type definition representing the given task's group
	 * 
	 * @param task the task
	 * 
	 * @return the group type definition
	 */
	public static SchemaElement getGroup(Task task) {
		Definition definition = task.getMainContext();
		if (definition instanceof SchemaElement) {
			return (SchemaElement) definition;
		}
		else if (definition instanceof AttributeDefinition) {
			// get first declaring element
			return ((AttributeDefinition) definition).getDeclaringType().getDeclaringElements().iterator().next();
		}
		else {
			throw new IllegalArgumentException("Unknown definition type"); //$NON-NLS-1$
		}
	}

}
