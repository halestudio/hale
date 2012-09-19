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

import java.text.MessageFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.service.mapping.AlignmentService;
import eu.esdihumboldt.hale.ui.views.tasks.internal.Messages;
import eu.esdihumboldt.hale.ui.views.tasks.model.ServiceProvider;
import eu.esdihumboldt.hale.ui.views.tasks.model.Task;
import eu.esdihumboldt.hale.ui.views.tasks.model.TaskFactory;
import eu.esdihumboldt.hale.ui.views.tasks.model.TaskType;
import eu.esdihumboldt.hale.ui.views.tasks.model.impl.AbstractTaskFactory;
import eu.esdihumboldt.hale.ui.views.tasks.model.impl.AbstractTaskType;
import eu.esdihumboldt.hale.ui.views.tasks.model.impl.AlignmentTask;
import eu.esdihumboldt.specification.cst.align.ICell;

/**
 * Map attribute task factory
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class MapAttributeTaskFactory extends AbstractTaskFactory {
	
	/**
	 * The task 
	 */
	private class MapAttributeTask extends AlignmentTask {

		/**
		 * Create a new task
		 *
		 * @param serviceProvider the service provider 
		 * @param type the type to map
		 */
		public MapAttributeTask(ServiceProvider serviceProvider,
				AttributeDefinition type) {
			super(serviceProvider, getTaskTypeName(), Collections.singletonList(type));
		}

		/**
		 * @see AlignmentTask#cellsAdded(Iterable)
		 */
		@Override
		public void cellsAdded(Iterable<ICell> cells) {
			//TODO check given cells instead of calling validateTask
			if (!validateTask((AttributeDefinition) getMainContext(), alignmentService)) {
				invalidate();
			}
		}
		
		/**
		 * @see AlignmentTask#cellRemoved(ICell)
		 */
		@Override
		public void cellRemoved(ICell cell) {
			if (!validateTask((AttributeDefinition) getMainContext(), alignmentService)) {
				invalidate();
			}
		}

	}

	/**
	 * The task type
	 */
	private static class MapAttributeTaskType extends AbstractTaskType {
		
		/**
		 * Constructor
		 * 
		 * @param taskFactory the task factory
		 */
		public MapAttributeTaskType(TaskFactory taskFactory) {
			super(taskFactory);
		}

		/**
		 * @see TaskType#getReason(Task)
		 */
		@Override
		public String getReason(Task task) {
			return Messages.MapAttributeTaskFactory_0; //$NON-NLS-1$
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
			return MessageFormat.format(Messages.MapAttributeTaskFactory_1, ((AttributeDefinition) task.getMainContext()).getName()); //$NON-NLS-1$
		}

		/**
		 * @see TaskType#getValue(Task)
		 */
		@Override
		public double getValue(Task task) {
			return 0.3;
		}

	}
	
	/**
	 * The type name
	 */
	public static final String BASE_TYPE_NAME = "Schema.mapAttribute"; //$NON-NLS-1$
	
	/**
	 * The task type
	 */
	private final TaskType type;

	/**
	 * Default constructor
	 */
	public MapAttributeTaskFactory() {
		super(BASE_TYPE_NAME);
		
		type = new MapAttributeTaskType(this);
	}

	/**
	 * @see TaskFactory#createTask(ServiceProvider, Definition[])
	 */
	@Override
	public Task createTask(ServiceProvider serviceProvider,
			Definition... definitions) {
		if (definitions == null || definitions.length < 1 || !(definitions[0] instanceof AttributeDefinition)) {
			return null;
		}
		
		AlignmentService alignmentService = serviceProvider.getService(AlignmentService.class);
		
		AttributeDefinition type = (AttributeDefinition) definitions[0];
		if (validateTask(type, alignmentService)) {
			return new MapAttributeTask(serviceProvider, type);
		}
		
		return null;
	}

	/**
	 * Determines if the given attribute definition is valid for a task
	 * 
	 * @param attribute the attribute definition
	 * @param alignmentService the alignment service
	 * 
	 * @return if the type is valid
	 */
	private static boolean validateTask(AttributeDefinition attribute,
			AlignmentService alignmentService) {
		// additional condition: declaring type or sub type must be mapped
		boolean typeMapped = false;
		Queue<TypeDefinition> typeQueue = new LinkedList<TypeDefinition>();
		typeQueue.add(attribute.getDeclaringType());
		while (!typeMapped && !typeQueue.isEmpty()) {
			TypeDefinition type = typeQueue.poll();
			
			for (SchemaElement element : type.getDeclaringElements()) {
				List<ICell> elementCells = alignmentService.getCell(element.getEntity());
				if (elementCells != null && !elementCells.isEmpty()) {
					typeMapped = true;
				}
			}
			
			typeQueue.addAll(type.getSubTypes());
		}
		if (!typeMapped) {
			return false;
		}
		
		List<ICell> cells = alignmentService.getCell(attribute.getEntity());
		if (cells == null || cells.isEmpty()) {
			return true;
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
