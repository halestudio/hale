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

package eu.esdihumboldt.hale.common.align.extension.function;

import java.util.List;

import javax.annotation.Nullable;

import net.jcip.annotations.Immutable;

import org.eclipse.core.runtime.IConfigurationElement;

import eu.esdihumboldt.hale.common.core.io.extension.ComplexValueDefinition;
import eu.esdihumboldt.hale.common.core.parameter.ParameterUtil;
import eu.esdihumboldt.hale.common.core.parameter.ParameterValueDescriptor;
import eu.esdihumboldt.hale.common.core.parameter.Validator;

/**
 * Definition of a function parameter.
 * 
 * @author Simon Templer
 */
@Immutable
public final class FunctionParameter extends AbstractParameter implements FunctionParameterDefinition {

	private final Class<?> binding;
	private final Validator validator;
	private final List<String> enumeration;
	private final boolean scriptable;
	private final ComplexValueDefinition complexBinding;
	private final ParameterValueDescriptor valueDescriptor;
	private final boolean deprecated;

	/**
	 * Create a function parameter definition.
	 * 
	 * @param conf the configuration element
	 */
	public FunctionParameter(IConfigurationElement conf) {
		super(conf);
		String scriptableAttr = conf.getAttribute("scriptable");
		this.scriptable = scriptableAttr == null ? false : Boolean.valueOf(scriptableAttr);
		String deprecatedAttr = conf.getAttribute("deprecated");
		this.deprecated = deprecatedAttr == null ? false : Boolean.valueOf(deprecatedAttr);
		this.binding = ParameterUtil.getBinding(conf);
		this.enumeration = ParameterUtil.getEnumeration(conf);
		this.validator = ParameterUtil.getValidator(conf);
		this.complexBinding = ParameterUtil.getComplexValueDefinition(conf);
		this.valueDescriptor = ParameterUtil.getValueDescriptor(conf);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition#getBinding()
	 */
	@Override
	public @Nullable
	Class<?> getBinding() {
		return binding;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition#getEnumeration()
	 */
	@Override
	public @Nullable
	List<String> getEnumeration() {
		return enumeration;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition#getValidator()
	 */
	@Override
	public @Nullable
	Validator getValidator() {
		return validator;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition#isScriptable()
	 */
	@Override
	public boolean isScriptable() {
		return scriptable;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition#isDeprecated()
	 */
	@Override
	public boolean isDeprecated() {
		return deprecated;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition#getComplexBinding()
	 */
	@Override
	public @Nullable
	ComplexValueDefinition getComplexBinding() {
		return complexBinding;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition#getValueDescriptor()
	 */
	@Override
	public @Nullable
	ParameterValueDescriptor getValueDescriptor() {
		return valueDescriptor;
	}
}
