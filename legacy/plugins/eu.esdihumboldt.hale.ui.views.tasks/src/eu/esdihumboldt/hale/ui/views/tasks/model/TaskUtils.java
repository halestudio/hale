/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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
