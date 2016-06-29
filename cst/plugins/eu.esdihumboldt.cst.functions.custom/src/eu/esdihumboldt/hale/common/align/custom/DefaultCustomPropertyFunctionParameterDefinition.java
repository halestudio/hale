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

package eu.esdihumboldt.hale.common.align.custom;

import java.util.ArrayList;
import java.util.List;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition;
import eu.esdihumboldt.hale.common.core.io.extension.ComplexValueDefinition;
import eu.esdihumboldt.hale.common.core.parameter.ParameterValueDescriptor;
import eu.esdihumboldt.hale.common.core.parameter.Validator;

/**
 * Wrapper for default implementation of a custom function parameter that
 * exposes a {@link FunctionParameterDefinition} interface.
 * 
 * @author Simon Templer
 */
public class DefaultCustomPropertyFunctionParameterDefinition
		implements FunctionParameterDefinition {

	private final DefaultCustomPropertyFunctionParameter parameter;

	/**
	 * Constructor.
	 * 
	 * @param parameter the parameter to wrap
	 */
	public DefaultCustomPropertyFunctionParameterDefinition(
			DefaultCustomPropertyFunctionParameter parameter) {
		super();
		this.parameter = parameter;
	}

	@Override
	public int getMinOccurrence() {
		return parameter.getMinOccurrence();
	}

	@Override
	public int getMaxOccurrence() {
		return parameter.getMaxOccurrence();
	}

	@Override
	public String getName() {
		return parameter.getName();
	}

	@Override
	public String getDisplayName() {
		return parameter.getDisplayName();
	}

	@Override
	public String getDescription() {
		return parameter.getDescription();
	}

	@Override
	public Class<?> getBinding() {
		return parameter.getBindingClass();
	}

	@Override
	public List<String> getEnumeration() {
		return new ArrayList<>(parameter.getEnumeration());
	}

	@Override
	public Validator getValidator() {
		// TODO not supported yet
		return null;
	}

	@Override
	public boolean isScriptable() {
		// TODO not supported yet
		return false;
	}

	@Override
	public boolean isDeprecated() {
		// TODO not supported yet
		return false;
	}

	@Override
	public ComplexValueDefinition getComplexBinding() {
		// TODO not supported yet
		return null;
	}

	@Override
	public ParameterValueDescriptor getValueDescriptor() {
		// TODO not supported yet
		return null;
	}

}
