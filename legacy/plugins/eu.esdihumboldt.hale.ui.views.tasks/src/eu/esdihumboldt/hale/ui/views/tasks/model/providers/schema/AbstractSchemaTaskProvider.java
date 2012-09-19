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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import eu.esdihumboldt.hale.mapping.helper.EntityHelper;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.service.mapping.AlignmentService;
import eu.esdihumboldt.hale.ui.service.mapping.AlignmentServiceAdapter;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService.SchemaType;
import eu.esdihumboldt.hale.ui.service.schema.SchemaServiceAdapter;
import eu.esdihumboldt.hale.ui.views.tasks.model.Task;
import eu.esdihumboldt.hale.ui.views.tasks.model.impl.AbstractTaskProvider;
import eu.esdihumboldt.hale.ui.views.tasks.service.TaskService;
import eu.esdihumboldt.specification.cst.align.ICell;
import eu.esdihumboldt.specification.cst.align.IEntity;

/**
 * Schema based task provider. Reacts on changes to schema and removed alignment
 * cells.
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractSchemaTaskProvider extends AbstractTaskProvider {
	
	private final SchemaType schemaType;
	
	private SchemaService schemaService;
	
	private SchemaServiceAdapter schemaListener;

	private AlignmentService alignmentService;

	private AlignmentServiceAdapter alignmentListener;
	
	private boolean reactOnCellAddOrUpdate = false;
	
	/**
	 * Create a new schema task provider for the given schema type
	 * 
	 * @param prefix the type name prefix or <code>null</code> 
	 * @param schemaType the schema type
	 */
	public AbstractSchemaTaskProvider(String prefix, SchemaType schemaType) {
		super(prefix);
		
		this.schemaType = schemaType;
	}

	/**
	 * @param reactOnCellAddOrUpdate the reactOnCellAddOrUpdate to set
	 */
	public void setReactOnCellAddOrUpdate(boolean reactOnCellAddOrUpdate) {
		this.reactOnCellAddOrUpdate = reactOnCellAddOrUpdate;
	}

	/**
	 * @return the schemaType
	 */
	public SchemaType getSchemaType() {
		return schemaType;
	}

	/**
	 * @see AbstractTaskProvider#doActivate(TaskService)
	 */
	@Override
	protected void doActivate(final TaskService taskService) {
		schemaService = serviceProvider.getService(SchemaService.class);
		alignmentService = serviceProvider.getService(AlignmentService.class);
		
		// create tasks from the current schema
		generateSchemaTasks(taskService, schemaService.getSchema(schemaType));
		
		// create tasks for new schema types
		schemaService.addListener(schemaListener = new SchemaServiceAdapter() {

			@Override
			public void schemaChanged(SchemaType schema) {
				if (schema.equals(schemaType)) {
					generateSchemaTasks(taskService, schemaService.getSchema(schema));
				}
			}
			
		});
		
		// create tasks when cells have been removed
		alignmentService.addListener(alignmentListener = new AlignmentServiceAdapter() {

			@Override
			public void alignmentCleared() {
				generateSchemaTasks(taskService, schemaService.getSchema(schemaType));
			}

			@Override
			public void cellRemoved(ICell cell) {
				generateSchemaTasks(cell, false); //XXX
			}

			@Override
			public void cellsAdded(Iterable<ICell> cells) {
				if (reactOnCellAddOrUpdate) {
					for (ICell cell : cells) {
						generateSchemaTasks(cell, true); //XXX
					}
				}
			}

			@Override
			public void cellsUpdated(Iterable<ICell> cells) {
				if (reactOnCellAddOrUpdate) {
					for (ICell cell : cells) {
						generateSchemaTasks(cell, true); //XXX
					}
				}
			}
			
		});
	}

	/**
	 * Generate schema tasks based on the given cell
	 * 
	 * @param cell the cell
	 * @param checkSuperTypes if super types shall be checked
	 */
	protected void generateSchemaTasks(ICell cell, boolean checkSuperTypes) {
		// get entity
		IEntity entity;
		switch (schemaType) {
		case SOURCE:
			entity = cell.getEntity1();
			break;
		case TARGET:
			entity = cell.getEntity2();
			break;
		default:
			throw new RuntimeException("Invalid schema type"); //$NON-NLS-1$
		}
		// get definition
		String identifier = EntityHelper.getIdentifier(entity);
		Definition definition = schemaService.getDefinition(identifier, schemaType);
		if (definition != null) {
			if (definition instanceof SchemaElement) {
				// type mapping removed
				if (checkSuperTypes) {
					Collection<SchemaElement> elements = new ArrayList<SchemaElement>();
					TypeDefinition type = ((SchemaElement) definition).getType();
					while (type != null) {
						elements.addAll(type.getDeclaringElements());
						
						type = type.getSuperType();
					}
					generateSchemaTasks(taskService, elements);
				}
				else {
					generateSchemaTasks(taskService, Collections.singleton((SchemaElement) definition));
				}
			}
			else if (definition instanceof AttributeDefinition) {
				Collection<Task> tasks = new ArrayList<Task>();
				AttributeDefinition attribute = (AttributeDefinition) definition;
				generateAttributeTasks(attribute, tasks);
				taskService.addTasks(tasks);
			}
		}
	}

	/**
	 * Generate schema tasks
	 * 
	 * @param taskService the task service
	 * @param schema the schema
	 */
	protected void generateSchemaTasks(TaskService taskService,
			Collection<SchemaElement> schema) {
		Collection<Task> tasks = new ArrayList<Task>();
		for (SchemaElement element : schema) {
			if (element.getType().isFeatureType()) { // restrict to feature types FIXME really? maybe this should be configurable 
				// create type tasks
				generateElementTasks(element, tasks);
				
				// create attribute tasks
				for (AttributeDefinition attribute : element.getType().getDeclaredAttributes()) {
					generateAttributeTasks(attribute, tasks);
				}
			}
		}
		
		taskService.addTasks(tasks);
	}

	/**
	 * Generate tasks based on the given attribute
	 * 
	 * @param attribute the attribute
	 * @param taskList the task list to add created tasks to
	 */
	protected abstract void generateAttributeTasks(AttributeDefinition attribute,
			Collection<Task> taskList);

	/**
	 * Generate tasks based on the given element
	 * 
	 * @param element the element
	 * @param taskList the task list to add created tasks to
	 */
	protected abstract void generateElementTasks(SchemaElement element,
			Collection<Task> taskList);

	/**
	 * @see AbstractTaskProvider#doDeactivate()
	 */
	@Override
	protected void doDeactivate() {
		if (schemaListener != null) {
			schemaService.removeListener(schemaListener);
		}
		if (alignmentListener!= null) {
			alignmentService.removeListener(alignmentListener);
		}
	}

}
