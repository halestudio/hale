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

package eu.esdihumboldt.hale.common.tasks;

import java.util.List;

/**
 * Base task implementation
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class BaseTask<C> implements Task<C> {

	private final TaskType<C> taskType;

	private final List<? extends C> context;

	/**
	 * Create a new task
	 * 
	 * @param typeName the name of the task type
	 * @param context the task context
	 */
	public BaseTask(TaskType<C> taskType, List<? extends C> context) {
		super();
		this.taskType = taskType;
		this.context = context;
	}

	/**
	 * Remove the task
	 */
	protected void invalidate() {
		// do nothing
	}

	/**
	 * @see Task#getContext()
	 */
	@Override
	public List<? extends C> getContext() {
		return context;
	}

	/**
	 * @see Task#getMainContext()
	 */
	@Override
	public C getMainContext() {
		if (context.size() > 0) {
			return context.get(0);
		}
		else {
			throw new IllegalStateException("No valid task context defined"); //$NON-NLS-1$
		}
	}

	/**
	 * @see Task#getTypeName()
	 */
	@Override
	public TaskType<C> getTaskType() {
		return taskType;
	}

	/**
	 * @see Task#dispose()
	 */
	@Override
	public void dispose() {
		// do nothing
	}

	/**
	 * @see Object#hashCode()
	 */
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((context == null) ? 0 : context.hashCode());
//		result = prime * result + ((taskType == null) ? 0 : taskType.hashCode());
//		return result;
//	}

	/**
	 * @see Object#equals(Object)
	 */
//	@Override
//	public boolean equals(Object obj) {
//		// test if the other is a task with equal context and type name
//
//		if (this == obj) {
//			return true;
//		}
//		if (obj == null) {
//			return false;
//		}
//		if (!(obj instanceof Task)) {
//			return false;
//		}
//
//		Task<?> other = (Task<?>) obj;
//		if (context == null) {
//			if (other.getContext() != null) {
//				return false;
//			}
//		}
//		else if (!context.equals(other.getContext())) {
//			return false;
//		}
//
//		return true;
//	}

	/**
	 * @see Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(Task<C> other) {
		if (other == null) {
			return -1;
		}
		else if (this.equals(other)) {
			return 0;
		}

		return 1;
//		int result = getMainContext().getIdentifier()
//				.compareTo(other.getMainContext().getIdentifier());
//		if (result == 0) {
//			return getTypeName().compareTo(other.getTypeName());
//		}
//		else {
//			return result;
//		}
	}

}
