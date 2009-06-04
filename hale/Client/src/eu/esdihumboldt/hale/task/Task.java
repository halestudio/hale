/**
 * 
 */
package eu.esdihumboldt.hale.task;

import java.util.Set;

import eu.esdihumboldt.modelrepository.abstractfc.SchemaElement;

/**
 * A Task is any type of action to be done within the HALE application to 
 * describe that action's context and goal.
 * 
 * @author Thorsten Reitz
 */
public interface Task {
	
	/**
	 * @return the concrete subtype of this task, such as Schema.addClass, as 
	 * a {@link String}.
	 */
	public String getTaskType();
	
	/**
	 * @return the {@link Set} of {@link SchemaElement}s that form the context 
	 * of this {@link Task}, i.e. those which are directly modified by it. An 
	 * example would be the Mapping to be clarified.
	 */
	public Set<SchemaElement> getTaskContextElements();

	/**
	 * @return the {@link SeverityLevel} identifies whether the task is required 
	 * to clear up a logical error in the mapping or schema, whether it is a 
	 * logical warning that indicates a possible mismatch or erroneous modeling, 
	 * and a normal task indicates a simple open point that will improve the 
	 * quality of the schema or mapping. As an example for a warning, take the 
	 * case that two classes are declared equal via an equality relation, but 
	 * an algorithm finds they share no substructures like attribute names and 
	 * types.
	 */
	public SeverityLevel getSeverityLevel();
	
	/**
	 * @return the Source that this Task comes from. It identifies what 
	 * implementation of a task has created that task, which is also responsible 
	 * for describing the task and it's reason in a user-understandable form; 
	 */
	public TaskSource getSource();
	
	/**
	 * @return the value identifies the impact the solving of a task will 
	 * have in terms of the metrics used in the quality model;
	 */
	public double getValue();
	
	/**
	 * @return a Title for the Task that should in a compact form indicate what this 
	 */
	public String getTaskTitle();
	
	/**
	 * @return the status this Task is currently in.
	 */
	public TaskStatus getTaskStatus();
	
	
	public enum SeverityLevel {
		/** A logical error in the alignment that makes it impossible to apply. */
		error, 
		/** A warning indicates a possible error in the alignment, as indicated by instance analysis */
		warning,
		/** A normal task. */
		task
	}
	
	public enum TaskStatus {
		NEW,
		ACTIVE,
		COMPLETE,
		OBSOLETE
	}
	
}
