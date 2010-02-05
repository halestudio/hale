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

package eu.esdihumboldt.hale.task.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.IEntity;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.TaskService;
import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.models.alignment.AlignmentServiceAdapter;
import eu.esdihumboldt.hale.models.schema.SchemaServiceAdapter;
import eu.esdihumboldt.hale.rcp.utils.EntityHelper;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;
import eu.esdihumboldt.hale.task.Task;
import eu.esdihumboldt.hale.task.impl.AbstractTaskProvider;

/**
 * Schema based task provider
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SchemaTaskProvider extends AbstractTaskProvider {
	
	private final SchemaType schemaType;
	
	private SchemaService schemaService;
	
	private final MapTypeTaskFactory mapType;
	
	private final MapAttributeTaskFactory mapAttribute;

	private SchemaServiceAdapter schemaListener;

	private AlignmentService alignmentService;

	private AlignmentServiceAdapter alignmentListener;
	
	/**
	 * Create a new schema task provider for the given schema type
	 * 
	 * @param schemaType the schema type
	 */
	public SchemaTaskProvider(SchemaType schemaType) {
		super((schemaType == SchemaType.SOURCE)?("source."):("target."));
		
		this.schemaType = schemaType;
		
		addFactory(mapType = new MapTypeTaskFactory()); //TODO param?
		addFactory(mapAttribute = new MapAttributeTaskFactory()); //TODO param?
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
					throw new RuntimeException("Invalid schema type");
				}
				// get definition
				String identifier = EntityHelper.getIdentifier(entity);
				Definition definition = schemaService.getDefinition(identifier, schemaType);
				if (definition != null) {
					if (definition instanceof TypeDefinition) {
						// type mapping removed
						generateSchemaTasks(taskService, Collections.singleton((TypeDefinition) definition));
					}
					else if (definition instanceof AttributeDefinition) {
						AttributeDefinition attribute = (AttributeDefinition) definition;
						Task attrTask = mapAttribute.createTask(serviceProvider, attribute);
						if (attrTask != null) {
							taskService.addTask(attrTask);
						}
					}
				}
			}
			
		});
	}

	/**
	 * Generate schema tasks
	 * 
	 * @param taskService the task service
	 * @param schema the schema
	 */
	protected void generateSchemaTasks(TaskService taskService,
			Collection<TypeDefinition> schema) {
		Collection<Task> tasks = new ArrayList<Task>();
		for (TypeDefinition type : schema) {
			if (type.isFeatureType()) { // restrict to feature types
				// create map type tasks
				Task task = mapType.createTask(serviceProvider, type);
				if (task != null) {
					tasks.add(task);
				}
				
				// create attribute type tasks
				for (AttributeDefinition attribute : type.getDeclaredAttributes()) {
					Task attrTask = mapAttribute.createTask(serviceProvider, attribute);
					if (attrTask != null) {
						tasks.add(attrTask);
					}
				}
			}
		}
		
		taskService.addTasks(tasks);
	}

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
