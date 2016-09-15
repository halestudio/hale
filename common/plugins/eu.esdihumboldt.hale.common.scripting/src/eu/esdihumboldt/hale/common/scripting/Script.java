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
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Interface for the scripting extension.
 * 
 * @author Kai Schwierczek
 */
public interface Script {

	// XXX Allow testing the return type via validate or somehow else?

	/**
	 * Evaluates the given script with the given variables.
	 * 
	 * @param script the script to use
	 * @param variables the variables to use
	 * @param provider the service provider
	 * @return the result of the evaluation
	 * @throws ScriptException if the evaluation fails
	 */
	public Object evaluate(String script, Iterable<PropertyValue> variables,
			ServiceProvider provider) throws ScriptException;

	/**
	 * Validates the given script against the given variables
	 * 
	 * @param script the script to validate
	 * @param variables the variables to use
	 * @param provider the service provider
	 * @return <code>null</code> if the script validates, an error message
	 *         otherwise
	 */
	public String validate(String script, Iterable<PropertyValue> variables,
			ServiceProvider provider);

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

	/**
	 * States if transformation variables should be replaced in the script
	 * string, before it is passed to evaluation.
	 * 
	 * @return <code>true</code> if the script expects transformation variables
	 *         to be already replaced when evaluating it, <code>false</code> if
	 *         transformation variables are handled by the script or not
	 *         supported at all
	 */
	default boolean requiresReplacedTransformationVariables() {
		return false;
	}
}
