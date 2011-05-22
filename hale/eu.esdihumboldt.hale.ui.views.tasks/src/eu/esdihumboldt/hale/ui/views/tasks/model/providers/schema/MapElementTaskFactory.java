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
