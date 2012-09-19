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
import java.util.List;

import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
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
 * Map type task factory
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class MapElementTaskFactory extends AbstractTaskFactory {
	
	/**
	 * The task 
	 */
	private class MapTypeTask extends AlignmentTask {

		/**
		 * Create a new task
		 *
		 * @param serviceProvider the service provider 
		 * @param element the element to map
		 */
		public MapTypeTask(ServiceProvider serviceProvider,
				SchemaElement element) {
			super(serviceProvider, getTaskTypeName(), Collections.singletonList(element));
		}

		/**
		 * @see AlignmentTask#cellsAdded(Iterable)
		 */
		@Override
		public void cellsAdded(Iterable<ICell> cells) {
			//TODO check given cells instead of calling validateTask
			if (!validateTask((SchemaElement) getMainContext(), alignmentService)) {
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
			super(taskFactory);
		}

		/**
		 * @see TaskType#getReason(Task)
		 */
		@Override
		public String getReason(Task task) {
			return Messages.MapElementTaskFactory_0; //$NON-NLS-1$
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
			return MessageFormat.format(Messages.MapElementTaskFactory_1, ((SchemaElement) task.getMainContext()).getElementName().getLocalPart()); //$NON-NLS-1$
		}

		/**
		 * @see TaskType#getValue(Task)
		 */
		@Override
		public double getValue(Task task) {
			return 0.6;
		}

	}
	
	/**
	 * The type name
	 */
	public static final String BASE_TYPE_NAME = "Schema.mapType"; //$NON-NLS-1$
	
	/**
	 * The task type
	 */
	private final TaskType type;

	/**
	 * Default constructor
	 */
	public MapElementTaskFactory() {
		super(BASE_TYPE_NAME);
		
		type = new MapTypeTaskType(this);
	}

	/**
	 * @see TaskFactory#createTask(ServiceProvider, Definition[])
	 */
	@Override
	public Task createTask(ServiceProvider serviceProvider,
			Definition... definitions) {
		if (definitions == null || definitions.length < 1 || !(definitions[0] instanceof SchemaElement)) {
			return null;
		}
		
		AlignmentService alignmentService = serviceProvider.getService(AlignmentService.class);
		
		SchemaElement element = (SchemaElement) definitions[0];
		if (validateTask(element, alignmentService)) {
			return new MapTypeTask(serviceProvider, element);
		}
		
		return null;
	}

	/**
	 * Determines if the given element is valid for a task
	 * 
	 * @param element the element
	 * @param alignmentService the alignment service
	 * 
	 * @return if the type is valid
	 */
	private static boolean validateTask(SchemaElement element,
			AlignmentService alignmentService) {
		if (element.getType().isFeatureType() && !element.getType().isAbstract()) { //FIXME only on feature types? configurable?
			List<ICell> cells = alignmentService.getCell(element.getEntity());
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
