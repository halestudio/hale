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

import java.util.Collections;
import java.util.List;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;
import eu.esdihumboldt.hale.task.ServiceProvider;
import eu.esdihumboldt.hale.task.Task;
import eu.esdihumboldt.hale.task.TaskFactory;
import eu.esdihumboldt.hale.task.TaskType;
import eu.esdihumboldt.hale.task.impl.AbstractTaskType;
import eu.esdihumboldt.hale.task.impl.AlignmentTask;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class MapTypeTaskFactory implements TaskFactory {
	
	/**
	 * The task 
	 */
	private static class MapTypeTask extends AlignmentTask {

		/**
		 * Create a new task
		 *
		 * @param serviceProvider the service provider 
		 * @param type the type to map
		 */
		public MapTypeTask(ServiceProvider serviceProvider,
				TypeDefinition type) {
			super(serviceProvider, TYPE_NAME, Collections.singletonList(type));
		}

		/**
		 * @see AlignmentTask#cellsAdded(Iterable)
		 */
		@Override
		public void cellsAdded(Iterable<ICell> cells) {
			//TODO check given cells instead of calling validateTask
			if (!validateTask((TypeDefinition) getMainContext(), alignmentService)) {
				invalidate();
			}
		}

	}

	/**
	 * The task type
	 */
	private static class MapTypeTaskType extends AbstractTaskType {
		
		/**
		 * Constructor
		 * 
		 * @param taskFactory the task factory
		 */
		public MapTypeTaskType(TaskFactory taskFactory) {
			super(TYPE_NAME, taskFactory);
		}

		/**
		 * @see TaskType#getReason(Task)
		 */
		@Override
		public String getReason(Task task) {
			return "Type not mapped";
		}

		/**
		 * @see TaskType#getSeverityLevel(Task)
		 */
		@Override
		public SeverityLevel getSeverityLevel(Task task) {
			return SeverityLevel.task;
		}

		/**
		 * @see TaskType#getTitle(Task)
		 */
		@Override
		public String getTitle(Task task) {
			return "Create a mapping for " + ((TypeDefinition) task.getMainContext()).getName().getLocalPart();
		}

	}
	
	/**
	 * The type name
	 */
	public static final String TYPE_NAME = "Schema.mapType";
	
	/**
	 * The task type
	 */
	private TaskType type = new MapTypeTaskType(this);

	/**
	 * @see TaskFactory#createTask(ServiceProvider, Definition[])
	 */
	@Override
	public Task createTask(ServiceProvider serviceProvider,
			Definition... definitions) {
		if (definitions == null || definitions.length < 1 || !(definitions[0] instanceof TypeDefinition)) {
			return null;
		}
		
		AlignmentService alignmentService = serviceProvider.getService(AlignmentService.class);
		
		TypeDefinition type = (TypeDefinition) definitions[0];
		if (validateTask(type, alignmentService)) {
			return new MapTypeTask(serviceProvider, type);
		}
		
		return null;
	}

	/**
	 * Determines if the given type definition is valid for a task
	 * 
	 * @param type the type definition
	 * @param alignmentService the alignment service
	 * 
	 * @return if the type is valid
	 */
	private static boolean validateTask(TypeDefinition type,
			AlignmentService alignmentService) {
		if (type.isFeatureType() && !type.isAbstract()) {
			List<ICell> cells = alignmentService.getCell(type.getEntity());
			if (cells == null || cells.isEmpty()) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * @see TaskFactory#getTaskType()
	 */
	@Override
	public TaskType getTaskType() {
		return type;
	}

}
