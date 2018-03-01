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
 * Base class for tasks
 *
 * @param <C> the type of the context object
 * 
 * @author Simon Templer
 * @author Florian Esser
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractTask<C> implements Task<C> {

	private final TaskType<C> taskType;

	private final List<? extends C> context;

	/**
	 * Create a new task
	 * 
	 * @param taskType the task type
	 * @param context the task context
	 */
	public AbstractTask(TaskType<C> taskType, List<? extends C> context) {
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
}
