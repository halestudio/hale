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

package eu.esdihumboldt.hale.ui.views.tasks.model.providers.schema;

import java.util.Collection;

import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService.SchemaType;
import eu.esdihumboldt.hale.ui.views.tasks.model.Task;
import eu.esdihumboldt.hale.ui.views.tasks.model.TaskFactory;

/**
 * Nil attribute mapping tasks
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class NilAttributeTaskProvider extends AbstractSchemaTaskProvider {
	
	private TaskFactory mapNilAttribute;

	/**
	 * Default constructor
	 */
	public NilAttributeTaskProvider() {
		super(null, SchemaType.TARGET);
		
		setReactOnCellAddOrUpdate(true);
		
		addFactory(mapNilAttribute = new MapNilAttributeTaskFactory());
	}

	/**
	 * @see AbstractSchemaTaskProvider#generateAttributeTasks(AttributeDefinition, Collection)
	 */
	@Override
	protected void generateAttributeTasks(AttributeDefinition attribute,
			Collection<Task> taskList) {
		Task attrTask = mapNilAttribute.createTask(serviceProvider, attribute);
		if (attrTask != null) {
			taskList.add(attrTask);
		}
	}

	/**
	 * @see AbstractSchemaTaskProvider#generateElementTasks(SchemaElement, Collection)
	 */
	@Override
	protected void generateElementTasks(SchemaElement element,
			Collection<Task> taskList) {
		// ignore
	}

}
