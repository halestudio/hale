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

package eu.esdihumboldt.hale.common.tasks;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.annotations.messages.Message;
import eu.esdihumboldt.hale.common.align.model.annotations.messages.MessageDescriptor;

/**
 * Task factory for {@link Cell}s
 * 
 * @author Florian Esser
 */
public class CellTaskFactory implements TaskFactory<Cell> {

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

		@Override
		public String getReason(Task<Cell> task) {
			return "Created from cell annotation";
		}

		@Override
		public TaskSeverity getSeverityLevel(Task<Cell> task) {
			return TaskSeverity.WARNING;
		}

		@Override
		public String getTitle(Task<Cell> task) {
			if (task instanceof CellMessageTask) {
				return ((CellMessageTask) task).getMessage().getText();
			}
			else {
				return MessageFormat.format("Unknown cell task of type {0}",
						task.getClass().getCanonicalName());
			}
		}

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
	 * Default constructor
	 */
	public CellTaskFactory() {
		this.taskType = new CellMessageTaskType(this);
	}

	@Override
	public TaskType<Cell> getTaskType() {
		return taskType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Task<Cell>> createTasks(Cell context) {
		List<Task<Cell>> cellTasks = new ArrayList<>();

		List<Message> messageAnnotations = (List<Message>) context
				.getAnnotations(MessageDescriptor.ID);
		messageAnnotations.forEach(msg -> cellTasks.add(createTask(msg, context)));

		return cellTasks;
	}

	private Task<Cell> createTask(Message cellMessage, Cell context) {
		return new CellMessageTask(taskType, Collections.singletonList(context), cellMessage);
	}

}
