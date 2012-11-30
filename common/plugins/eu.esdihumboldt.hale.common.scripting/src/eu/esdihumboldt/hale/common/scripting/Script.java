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

package eu.esdihumboldt.hale.common.scripting;

import javax.script.ScriptException;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;

/**
 * Interface for the scripting extension.
 * 
 * @author Kai Schwierczek
 */
public interface Script {

	// XXX Further allow some configuration settings for the script execution?
	// For example class loader for Groovy.
	// -> ClassLoader for groovy should be the BundleActivator's ClassLoader?
	// -> should be custom classloader that goes through all bundles?
	// XXX Allow testing the return type via validate or somehow else?

	/**
	 * Evaluates the given script with the given variables.
	 * 
	 * @param script the script to use
	 * @param variables the variables to use
	 * @return the result of the evaluation
	 * @throws ScriptException if the evaluation fails
	 */
	public Object evaluate(String script, Iterable<PropertyValue> variables) throws ScriptException;

	/**
	 * Validates the given script against the given variables
	 * 
	 * @param script the script to validate
	 * @param variables the variables to use
	 * @return <code>null</code> if the script validates, an error message
	 *         otherwise
	 */
	public String validate(String script, Iterable<PropertyValue> variables);

	/**
	 * Returns the string representation of the given entity definition this
	 * script is using.
	 * 
	 * @param entityDefinition the entity definition
	 * @return the string representation of the entity definition
	 */
	public String getVariableName(PropertyEntityDefinition entityDefinition);

	/**
	 * Returns the script id.
	 * 
	 * @return the script id
	 */
	public String getId();
}
