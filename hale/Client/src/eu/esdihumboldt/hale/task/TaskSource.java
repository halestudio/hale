/**
 * 
 */
package eu.esdihumboldt.hale.task;

/**
 * A {@link TaskSource} describes why a certain task was created and by which
 * part of the system.
 * @author Thorsten Reitz
 */
public interface TaskSource {

	public String getImplementationName();
	
	public String getTaskCreationReason();

	
}
