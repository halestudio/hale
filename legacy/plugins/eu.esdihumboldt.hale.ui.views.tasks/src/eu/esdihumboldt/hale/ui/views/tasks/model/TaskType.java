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

package eu.esdihumboldt.hale.ui.views.tasks.model;

/**
 * Represents a certain type of task and allows retrieving information on a
 * task of that type
 *
 * @author Simon Templer, Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface TaskType {
	
	/**
	 * Task severity level
	 */
	public enum SeverityLevel {
		/** A logical error in the alignment that makes it impossible to apply. */
		error, 
		/** A warning indicates a possible error in the alignment, as indicated by instance analysis */
		warning,
		/** A normal task. */
		task;

		/**
		 * Get the maximum severity level based on the given levels
		 * 
		 * @param one one level
		 * @param theOther the other level
		 * 
		 * @return the maximum severity of both given levels
		 */
		public static SeverityLevel max(SeverityLevel one, SeverityLevel theOther) {
			if (one == null) {
				return theOther;
			}
			if (theOther == null) {
				return one;
			}
			
			if (one.equals(error) || theOther.equals(error)) {
				return error;
			}
			else if (one.equals(warning) || theOther.equals(warning)) {
				return warning;
			}
			else {
				return task;
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
	public TaskFactory getTaskFactory();
	
	/**
	 * Get the severity level of the given task.
	 * The {@link SeverityLevel} identifies whether the task is required 
	 * to clear up a logical error in the mapping or schema, whether it is a 
	 * logical warning that indicates a possible mismatch or erroneous modeling, 
	 * and a normal task indicates a simple open point that will improve the 
	 * quality of the schema or mapping. As an example for a warning, take the 
	 * case that two classes are declared equal via an equality relation, but 
	 * an algorithm finds they share no substructures like attribute names and 
	 * types.
	 * 
	 * @param task the task which severity level shall be identified. The task's
	 *   type name must match that of the task type
	 * 
	 * @return the severity level of the task 
	 */
	public SeverityLevel getSeverityLevel(Task task);
	
	/**
	 * Get the value of the given task
	 * 
	 * @param task the task
	 * 
	 * @return the value identifies the impact the solving of a task will 
	 * have in terms of the metrics used in the quality model;
	 */
	public double getValue(Task task);
	
	/**
	 * Get the creation reason for the given task
	 * 
	 * @param task the task which reason shall be determined. The task's
	 *   type name must match that of the task type
	 *   
	 * @return the task's creation reason
	 */
	public String getReason(Task task);
	
	/**
	 * Get the title of the given task
	 * 
	 * @param task the task which title shall be determined. The task's
	 *   type name must match that of the task type
	 *   
	 * @return the task title
	 */
	public String getTitle(Task task);

}
