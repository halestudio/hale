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

package eu.esdihumboldt.hale.ui.views.tasks.model.providers.schema.geometry;

import java.text.MessageFormat;

import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.views.tasks.internal.Messages;
import eu.esdihumboldt.hale.ui.views.tasks.model.ServiceProvider;
import eu.esdihumboldt.hale.ui.views.tasks.model.Task;
import eu.esdihumboldt.hale.ui.views.tasks.model.TaskFactory;
import eu.esdihumboldt.hale.ui.views.tasks.model.TaskType;
import eu.esdihumboldt.hale.ui.views.tasks.model.impl.AbstractTaskFactory;
import eu.esdihumboldt.hale.ui.views.tasks.model.impl.AbstractTaskType;
import eu.esdihumboldt.hale.ui.views.tasks.model.impl.SchemaTask;

/**
 * Map type task factory
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class NoGeometryTaskFactory extends AbstractTaskFactory {

	/**
	 * The task type
	 */
	private static class NoGeometryDescriptorTaskType extends AbstractTaskType {
		
		/**
		 * Constructor
		 * 
		 * @param taskFactory the task factory
		 */
		public NoGeometryDescriptorTaskType(TaskFactory taskFactory) {
			super(taskFactory);
		}

		/**
		 * @see TaskType#getReason(Task)
		 */
		@Override
		public String getReason(Task task) {
			return Messages.NoGeometryTaskFactory_0; //$NON-NLS-1$
		}

		/**
		 * @see TaskType#getSeverityLevel(Task)
		 */
		@Override
		public SeverityLevel getSeverityLevel(Task task) {
			return SeverityLevel.warning;
		}

		/**
		 * @see TaskType#getTitle(Task)
		 */
		@Override
		public String getTitle(Task task) {
			return MessageFormat.format(Messages.NoGeometryTaskFactory_1, 
					((SchemaElement) task.getMainContext()).getElementName().getLocalPart()); //$NON-NLS-1$
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
	public static final String BASE_TYPE_NAME = "Schema.noGeometryDescriptor"; //$NON-NLS-1$
	
	/**
	 * The task type
	 */
	private final TaskType type;

	/**
	 * Default constructor
	 */
	public NoGeometryTaskFactory() {
		super(BASE_TYPE_NAME);
		
		type = new NoGeometryDescriptorTaskType(this);
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
		
		SchemaElement element = (SchemaElement) definitions[0];
		if (validateTask(element)) {
			return new SchemaTask(serviceProvider, getTaskTypeName(), element);
		}
		
		return null;
	}

	/**
	 * Determines if the given element is valid for a task
	 * 
	 * @param element the element
	 * 
	 * @return if the type is valid
	 */
	private static boolean validateTask(SchemaElement element) {
		TypeDefinition type = element.getType();
		if (type.isFeatureType() && !type.isAbstract()) {
			// check if a geometry descriptor is available
			if (type.getFeatureType().getGeometryDescriptor() == null) {
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
