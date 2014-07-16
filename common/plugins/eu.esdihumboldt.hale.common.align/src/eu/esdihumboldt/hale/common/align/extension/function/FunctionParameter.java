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

import net.jcip.annotations.Immutable;

import org.eclipse.core.runtime.IConfigurationElement;

import eu.esdihumboldt.hale.common.core.parameter.ParameterUtil;
import eu.esdihumboldt.hale.common.core.parameter.Validator;

/**
 * Definition of a function parameter.
 * 
 * @author Simon Templer
 */
@Immutable
public final class FunctionParameter extends AbstractParameter {

	private final Class<?> binding;
	private final Validator validator;
	private final List<String> enumeration;
	private final boolean scriptable;

	/**
	 * Create a function parameter definition.
	 * 
	 * @param conf the configuration element
	 */
	public FunctionParameter(IConfigurationElement conf) {
		super(conf);
		String scriptableAttr = conf.getAttribute("scriptable");
		this.scriptable = scriptableAttr == null ? false : Boolean.valueOf(scriptableAttr);
		this.binding = ParameterUtil.getBinding(conf);
		this.enumeration = ParameterUtil.getEnumeration(conf);
		this.validator = ParameterUtil.getValidator(conf);
	}

	/**
	 * Returns the binding class for this function parameter or null if there is
	 * an enumeration present.
	 * 
	 * @return the binding
	 */
	public Class<?> getBinding() {
		return binding;
	}

	/**
	 * Returns the enumeration of allowed values for this function parameter or
	 * null if there is a binding present.
	 * 
	 * @return the enumeration
	 */
	public List<String> getEnumeration() {
		return enumeration;
	}

	/**
	 * Returns the validator associated with this function parameter or null if
	 * there is none. A validator can only be present if a binding is present.
	 * 
	 * @return the validator
	 */
	public Validator getValidator() {
		return validator;
	}

	/**
	 * Whether this function parameter may be scripted or not.
	 * 
	 * @return whether this function parameter may be scripted or not
	 */
	public boolean isScriptable() {
		return scriptable;
	}
}
