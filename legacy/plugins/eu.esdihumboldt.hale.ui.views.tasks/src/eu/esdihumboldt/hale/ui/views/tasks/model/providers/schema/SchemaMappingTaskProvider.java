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

/**
 * Task provider that creates tasks for unmapped types and attributes
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class SchemaMappingTaskProvider extends AbstractSchemaTaskProvider {
	
	private final MapElementTaskFactory mapElement;
	
	private final MapAttributeTaskFactory mapAttribute;

	/**
	 * Create a new schema task provider for the given schema type
	 * 
	 * @param schemaType the schema type
	 */
	public SchemaMappingTaskProvider(SchemaType schemaType) {
		super((schemaType == SchemaType.SOURCE)?("source."):("target."), schemaType); //$NON-NLS-1$ //$NON-NLS-2$
		
		setReactOnCellAddOrUpdate(true);
		
		addFactory(mapElement = new MapElementTaskFactory()); //TODO param?
		addFactory(mapAttribute = new MapAttributeTaskFactory()); //TODO param?
	}

	/**
	 * @see AbstractSchemaTaskProvider#generateAttributeTasks(AttributeDefinition, Collection)
	 */
	@Override
	protected void generateAttributeTasks(AttributeDefinition attribute,
			Collection<Task> taskList) {
		Task attrTask = mapAttribute.createTask(serviceProvider, attribute);
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
		Task task = mapElement.createTask(serviceProvider, element);
		if (task != null) {
			taskList.add(task);
		}
	}

}
