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
 * Schema based task provider (FIXME for now only source schema based)
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SchemaTaskProvider extends AbstractTaskProvider {
	
	private SchemaService schemaService;
	
	private final MapTypeTaskFactory mapType;
	
	private final MapAttributeTaskFactory mapAttribute;

	private SchemaServiceAdapter schemaListener;

	private AlignmentService alignmentService;

	private AlignmentServiceAdapter alignmentListener;
	
	/**
	 * Default constructor
	 */
	public SchemaTaskProvider() {
		super("source.");
		
		addFactory(mapType = new MapTypeTaskFactory());
		addFactory(mapAttribute = new MapAttributeTaskFactory());
	}

	/**
	 * @see AbstractTaskProvider#doActivate(TaskService)
	 */
	@Override
	protected void doActivate(final TaskService taskService) {
		schemaService = serviceProvider.getService(SchemaService.class);
		alignmentService = serviceProvider.getService(AlignmentService.class);
		
		// create tasks from the current schema
		generateSchemaTasks(taskService, schemaService.getSchema(SchemaType.SOURCE));
		
		// create tasks for new schema types
		schemaService.addListener(schemaListener = new SchemaServiceAdapter() {

			@Override
			public void schemaChanged(SchemaType schema) {
				switch (schema) {
				case SOURCE:
					generateSchemaTasks(taskService, schemaService.getSchema(schema));
					break;
				}
			}
			
		});
		
		//TODO create tasks when cells have been removed
		alignmentService.addListener(alignmentListener = new AlignmentServiceAdapter() {

			@Override
			public void alignmentCleared() {
				//XXX works for SOURCE
				generateSchemaTasks(taskService, schemaService.getSchema(SchemaType.SOURCE));
			}

			@Override
			public void cellRemoved(ICell cell) {
				//XXX works for SOURCE
				IEntity entity = cell.getEntity1();
				String identifier = EntityHelper.getIdentifier(entity);
				Definition definition = schemaService.getDefinition(identifier);
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
