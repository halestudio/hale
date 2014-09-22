/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.core.parameter;

import java.util.List;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Definition of a parameter for an I/O provider.
 * 
 * @author Yasmina Kammeyer
 */
public class IOProviderParameter extends AbstractCommonParameter {

	// private static final ALogger log =
	// ALoggerFactory.getLogger(InstanceProviderParameter.class);

	private final Class<?> binding;
	private final Validator validator;
	private final List<String> enumeration;
	// true if the parameter is optional
	private final boolean optional;

	private final ParameterValueDescriptor valueDescriptor;

	/**
	 * Create a InstanceProvider parameter definition
	 * 
	 * @param conf the configuration element
	 */
	public IOProviderParameter(IConfigurationElement conf) {
		super(conf);

		this.binding = ParameterUtil.getBinding(conf);
		// also use validator information
		this.validator = ParameterUtil.getValidator(conf);

		this.enumeration = ParameterUtil.getEnumeration(conf);

		this.valueDescriptor = ParameterUtil.getValueDescriptor(conf);

		boolean optionalElement = Boolean.parseBoolean(conf.getAttribute("optional"));
		this.optional = optionalElement;
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
	 * Returns the validator associated with this function parameter or null if
	 * there is none. A validator can only be present if a binding is present.
	 * 
	 * @return the validator or <code>null</code>
	 */
	public @Nullable
	Validator getValidator() {
		return validator;
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
	 * Returns the optional whether the parameter is optional or not.
	 * 
	 * @return true, if the parameter is optional
	 */
	public boolean isOptional() {
		return optional;
	}

	/**
	 * Returns the ComplexValue or null.
	 * 
	 * @return the ComplexValue
	 */
	public ParameterValueDescriptor getValueDescriptor() {
		return valueDescriptor;
	}

}
