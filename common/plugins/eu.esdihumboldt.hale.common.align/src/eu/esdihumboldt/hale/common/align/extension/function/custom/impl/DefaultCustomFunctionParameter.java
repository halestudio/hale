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

package eu.esdihumboldt.hale.common.align.extension.function.custom.impl;

import java.util.List;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition;
import eu.esdihumboldt.hale.common.core.io.extension.ComplexValueDefinition;
import eu.esdihumboldt.hale.common.core.parameter.ParameterValueDescriptor;
import eu.esdihumboldt.hale.common.core.parameter.Validator;

/**
 * Default implementation of a custom function parameter.
 * 
 * @author Simon Templer
 */
public class DefaultCustomFunctionParameter extends MinimalParameter implements
		FunctionParameterDefinition {

	private Class<?> bindingClass;

	/**
	 * @param binding the parameter binding class to set
	 */
	public void setBinding(Class<?> binding) {
		this.bindingClass = binding;
	}

	@Override
	public Class<?> getBinding() {
		return bindingClass;
	}

	@Override
	public List<String> getEnumeration() {
		// XXX for now not supported
		return null;
	}

	@Override
	public Validator getValidator() {
		// XXX for now not supported
		return null;
	}

	@Override
	public boolean isScriptable() {
		// XXX for now not supported
		return false;
	}

	@Override
	public boolean isDeprecated() {
		return false;
	}

	@Override
	public ComplexValueDefinition getComplexBinding() {
		// XXX for now not supported
		return null;
	}

	@Override
	public ParameterValueDescriptor getValueDescriptor() {
		// XXX for now not supported
		return null;
	}

}
