package eu.esdihumboldt.util.groovy.sandbox;

import javax.annotation.Nullable;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 * Groovy sandbox service interface.
 * 
 * @author Kai Schwierczek
 */
public interface GroovyService {

	/**
	 * Processes a script result.
	 * 
	 * @param <T> the type of the processed result
	 */
	public interface ResultProcessor<T> {

		/**
		 * Process the script result. It is important that any closures created
		 * in the script are executed within the processor.
		 * 
		 * @param script the script
		 * @param returnValue the script return value
		 * @return the processed result
		 * @throws Exception if an error occurs processing the script result
		 */
		public T process(Script script, Object returnValue) throws Exception;
	}

	/**
	 * Create a customized {@link GroovyShell}.
	 * 
	 * @param binding the binding, may be <code>null</code>
	 * @return a customized {@link GroovyShell}
	 */
	public GroovyShell createShell(Binding binding);

	/**
	 * Parse a script using a customized shell.
	 * 
	 * @param script the script string
	 * @param binding binding the binding, may be <code>null</code>
	 * @return the parsed script
	 */
	public Script parseScript(String script, Binding binding);

	/**
	 * Evaluate a Groovy script.
	 * 
	 * @param script the script
	 * @param processor the script result processor, may be <code>null</code>
	 * @return the script's processed return value or <code>null</code> if no
	 *         processor was given
	 * @throws Exception if the Groovy script tries to do something which isn't
	 *             allowed or the result processing throws an exception
	 */
	public <T> T evaluate(Script script, @Nullable ResultProcessor<T> processor) throws Exception;

	/**
	 * Returns whether the Groovy execution restriction is currently active.
	 * 
	 * @return whether the Groovy execution restriction is currently active
	 */
	public boolean isRestrictionActive();

	/**
	 * Activates/Deactivates the Groovy execution restriction.
	 * 
	 * @param active whether the restriction should be active or not
	 */
	public void setRestrictionActive(boolean active);

	/**
	 * Adds a Groovy service listener
	 * 
	 * @param listener the listener to add
	 */
	public void addListener(GroovyServiceListener listener);

	/**
	 * Removes a Groovy service listener
	 * 
	 * @param listener the listener to remove
	 */
	public void removeListener(GroovyServiceListener listener);
}
