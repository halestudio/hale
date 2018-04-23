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

/**
 * Represents a certain type of task and allows retrieving information on a task
 * of that type
 * 
 * @param <C> the type of the context object
 *
 * @author Simon Templer, Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface TaskType<C> {

	/**
	 * Task severity level
	 */
	public enum TaskSeverity {
		/**
		 * A logical error in the alignment that makes it impossible to apply.
		 */
		ERROR,

		/**
		 * A warning indicates a possible error in the alignment, as indicated
		 * by instance analysis
		 */
		WARNING,

		/** A normal task. */
		TASK;

		/**
		 * Get the maximum severity level based on the given levels
		 * 
		 * @param one one level
		 * @param theOther the other level
		 * 
		 * @return the maximum severity of both given levels
		 */
		public static TaskSeverity max(TaskSeverity one, TaskSeverity theOther) {
			if (one == null) {
				return theOther;
			}
			if (theOther == null) {
				return one;
			}

			if (one.equals(ERROR) || theOther.equals(ERROR)) {
				return ERROR;
			}
			else if (one.equals(WARNING) || theOther.equals(WARNING)) {
				return WARNING;
			}
			else {
				return TASK;
			}
		}

	}

	/**
	 * Get the type name of the task type
	 * 
	 * @return the task type name
	 */
	public String getName();

	/**
	 * The task factory that provides tasks of this type
	 * 
	 * @return the task factory
	 */
	public TaskFactory<C> getTaskFactory();

	/**
	 * Get the severity level of the given task. The {@link TaskSeverity}
	 * identifies whether the task is required to clear up a logical error in
	 * the mapping or schema, whether it is a logical warning that indicates a
	 * possible mismatch or erroneous modeling, and a normal task indicates a
	 * simple open point that will improve the quality of the schema or mapping.
	 * As an example for a warning, take the case that two classes are declared
	 * equal via an equality relation, but an algorithm finds they share no
	 * substructures like attribute names and types.
	 * 
	 * @param task the task which severity level shall be identified. The task's
	 *            type name must match that of the task type
	 * 
	 * @return the severity level of the task
	 */
	public TaskSeverity getSeverityLevel(Task<C> task);

	/**
	 * Get the creation reason for the given task
	 * 
	 * @param task the task which reason shall be determined. The task's type
	 *            name must match that of the task type
	 * 
	 * @return the task's creation reason
	 */
	public String getReason(Task<C> task);

	/**
	 * Get the title of the given task
	 * 
	 * @param task the task which title shall be determined. The task's type
	 *            name must match that of the task type
	 * 
	 * @return the task title
	 */
	public String getTitle(Task<C> task);

}
