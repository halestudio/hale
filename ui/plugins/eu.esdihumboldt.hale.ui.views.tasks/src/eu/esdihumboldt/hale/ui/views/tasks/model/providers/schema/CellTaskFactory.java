/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.ui.views.tasks.model.providers.schema;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.annotations.messages.Message;
import eu.esdihumboldt.hale.common.align.model.annotations.messages.MessageDescriptor;
import eu.esdihumboldt.hale.common.tasks.AbstractTaskFactory;
import eu.esdihumboldt.hale.common.tasks.AbstractTaskType;
import eu.esdihumboldt.hale.common.tasks.CellMessageTask;
import eu.esdihumboldt.hale.common.tasks.Task;
import eu.esdihumboldt.hale.common.tasks.TaskFactory;
import eu.esdihumboldt.hale.common.tasks.TaskType;

/**
 * TODO Type description
 * 
 * @author Florian Esser
 */
public class CellTaskFactory extends AbstractTaskFactory<Cell> {

	/**
	 * The task type
	 */
	private static class CellMessageTaskType extends AbstractTaskType<Cell> {

		/**
		 * Constructor
		 * 
		 * @param taskFactory the task factory
		 */
		public CellMessageTaskType(TaskFactory<Cell> taskFactory) {
			super(taskFactory);
		}

		/**
		 * @see TaskType#getReason(Task)
		 */
		@Override
		public String getReason(Task task) {
//			return Messages.MapNilAttributeTaskFactory_0; // $NON-NLS-1$
			return "reason.Message";
		}

		/**
		 * @see TaskType#getSeverityLevel(Task)
		 */
		@Override
		public SeverityLevel getSeverityLevel(Task<Cell> task) {
			return SeverityLevel.WARNING;
		}

		/**
		 * @see TaskType#getTitle(Task)
		 */
		@Override
		public String getTitle(Task<Cell> task) {
//			return MessageFormat.format(Messages.MapNilAttributeTaskFactory_1,
//					((PropertyDefinition) task.getMainContext()).getName()); // $NON-NLS-1$
			if (task instanceof CellMessageTask) {
				return ((CellMessageTask) task).getMessage().getText();
			}
			else {
				return MessageFormat.format("Unknown cell task of type {0}",
						task.getClass().getCanonicalName());
			}
		}

		/**
		 * @see eu.esdihumboldt.hale.ui.views.tasks.model.TaskType#getName()
		 */
		@Override
		public String getName() {
			return "CellMessageTaskType";
		}
	}

	/**
	 * The task type
	 */
	private final CellMessageTaskType taskType;

	/**
	 * @param baseTypeName
	 */
	public CellTaskFactory(String baseTypeName) {
		super(baseTypeName);

		this.taskType = new CellMessageTaskType(this);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.views.tasks.model.TaskFactory#getTaskType()
	 */
	@Override
	public TaskType<Cell> getTaskType() {
		return taskType;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.views.tasks.model.TaskFactory#createTask(java.lang.Object)
	 */
	@Override
	public Collection<Task<Cell>> createTasks(Cell context) {
		List<Task<Cell>> cellTasks = new ArrayList<>();

		List<Message> messageAnnotations = (List<Message>) context
				.getAnnotations(MessageDescriptor.ID);
		messageAnnotations.forEach(msg -> cellTasks.add(createTask(msg, context)));

		return cellTasks;
	}

	private Task<Cell> createTask(Message cellMessage, Cell context) {
		// TODO Use cellMessage
//		return new DefaultTask<Cell>(taskType, Arrays.asList(context));
		return new CellMessageTask(taskType, Collections.singletonList(context), cellMessage);
	}

}
