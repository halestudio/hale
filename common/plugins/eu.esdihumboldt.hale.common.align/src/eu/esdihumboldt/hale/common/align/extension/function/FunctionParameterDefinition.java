/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.extension.function;

import java.util.List;

import eu.esdihumboldt.hale.common.core.io.extension.ComplexValueDefinition;
import eu.esdihumboldt.hale.common.core.parameter.ParameterValueDescriptor;
import eu.esdihumboldt.hale.common.core.parameter.Validator;

/**
 * Function parameter definition.
 * 
 * @author Simon Templer
 */
public interface FunctionParameterDefinition extends ParameterDefinition {

	/**
	 * Returns the binding class for this function parameter or null if there is
	 * an enumeration present.
	 * 
	 * @return the binding or <code>null</code>
	 */
	public abstract Class<?> getBinding();

	/**
	 * Returns the enumeration of allowed values for this function parameter or
	 * null if there is a binding present.
	 * 
	 * @return the value enumeration or <code>null</code>
	 */
	public abstract List<String> getEnumeration();

	/**
	 * Returns the validator associated with this function parameter or null if
	 * there is none. A validator can only be present if a binding is present.
	 * 
	 * @return the validator or <code>null</code>
	 */
	public abstract Validator getValidator();

	/**
	 * Whether this function parameter may be scripted or not.
	 * 
	 * @return whether this function parameter may be scripted or not
	 */
	public abstract boolean isScriptable();

	/**
	 * @return whether this function parameter is deprecated
	 */
	public abstract boolean isDeprecated();

	/**
	 * @return the complex value definition associated to the parameter, or
	 *         <code>null</code>
	 */
	public abstract ComplexValueDefinition getComplexBinding();

	/**
	 * @return the value descriptor associated to the parameter, or
	 *         <code>null</code>
	 */
	public abstract ParameterValueDescriptor getValueDescriptor();

}