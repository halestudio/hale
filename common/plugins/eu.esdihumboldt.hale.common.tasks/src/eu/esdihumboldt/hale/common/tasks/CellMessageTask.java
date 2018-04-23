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

import java.util.List;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.annotations.messages.Message;
import eu.esdihumboldt.hale.common.tasks.TaskUserData.TaskStatus;

/**
 * Task for {@link Message} annotations of a {@link Cell}.
 * 
 * @author Florian Esser
 */
public class CellMessageTask extends AbstractTask<Cell> implements TaskUserDataAware {

	private final Message message;

	/**
	 * Create the cell message task
	 * 
	 * @param taskType the task type
	 * @param context the Cell this task was generated from
	 * @param message the message annotation
	 */
	public CellMessageTask(TaskType<Cell> taskType, List<Cell> context, Message message) {
		super(taskType, context);

		this.message = message;
	}

	/**
	 * @return the cell's message annotation
	 */
	public Message getMessage() {
		return message;
	}

	@Override
	public int compareTo(Task<Cell> other) {
		if (other == null) {
			return -1;
		}
		else if (this.equals(other)) {
			return 0;
		}

		if (this.getMainContext().getId().equals(other.getMainContext().getId())) {
			if (other instanceof CellMessageTask) {
				CellMessageTask cmt = (CellMessageTask) other;
				return this.getMessage().getText().compareTo(cmt.getMessage().getText());
			}
			else if (other instanceof ResolvedTask<?>) {
				return this.compareTo(((ResolvedTask<Cell>) other).getTask());
			}
		}

		return this.getMainContext().getId().compareTo(other.getMainContext().getId());
	}

	@Override
	public boolean setUserData(TaskUserData data) {
		if (data == null) {
			return false;
		}

		boolean updated = false;

		switch (data.getTaskStatus()) {
		case NEW:
		case ACTIVE:
			if (getMainContext() instanceof MutableCell) {
				if (this.message.isDismissed()) {
					this.message.setDismissed(false);
					updated = true;
				}
			}
			break;
		case IGNORED:
		case COMPLETED:
			if (!this.message.isDismissed()) {
				this.message.setDismissed(true);
				updated = true;
			}
			break;
		}

		// TODO Others

		return updated;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.tasks.TaskUserDataAware#populateUserData(eu.esdihumboldt.hale.common.tasks.TaskUserData)
	 */
	@Override
	public void populateUserData(TaskUserData data) {
		if (this.message.isDismissed()) {
			data.setTaskStatus(TaskStatus.COMPLETED);
		}
		else {
			data.setTaskStatus(TaskStatus.NEW);
		}
	}

	@Override
	public boolean hasMainContext(Object context) {
		if (context == null) {
			return this.getMainContext() == null;
		}

		if (this.getMainContext() == null) {
			return false;
		}

		if (!(context instanceof Cell)) {
			return false;
		}

		// Two cells are considered to be the same task context if they have
		// identical IDs. This allows to replace a cell in the alignment (with
		// the replacing cell having the same ID but not being the same object
		// as the original cell) and retain the task associations.

		Cell otherCell = (Cell) context;
		return this.getMainContext().getId().equals(otherCell.getId());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getMainContext() == null) ? 0 : getMainContext().hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof CellMessageTask)) {
			return false;
		}

		CellMessageTask other = (CellMessageTask) obj;
		return this.getMainContext().equals(other.getMainContext())
				&& this.message.equals(other.message);
	}

}
