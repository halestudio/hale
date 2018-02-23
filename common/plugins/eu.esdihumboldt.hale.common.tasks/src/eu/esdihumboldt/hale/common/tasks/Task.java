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
 * A Task is any type of action to be done within the HALE application to
 * describe that action's context and goal.
 * 
 * @param <C> Type of the context object
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface Task<C> extends Comparable<Task<C>> {

	/**
	 * Get the name of the task's type
	 * 
	 * @return the task type name
	 */
	TaskType<C> getTaskType();

	/**
	 * Get the main context element
	 * 
	 * @return the main context element
	 */
	C getMainContext();

	/**
	 * @return the {@link Definition}s that form the context of this
	 *         {@link Task}, i.e. those which are directly modified by it. An
	 *         example would be the Mapping to be clarified.
	 */
	List<? extends C> getContext();

	/**
	 * Clean up the task. This method is called when a task is removed from the
	 * task service
	 */
	void dispose();
}
