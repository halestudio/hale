package eu.esdihumboldt.util.groovy.sandbox;

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
	 * @return the script's return value
	 * @throws GroovyRestrictionException if the Groovy script tries to do
	 *             something which isn't allowed
	 */
	public Object evaluate(Script script);

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
