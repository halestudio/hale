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

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Definition of an Instance Reader/Writer parameter
 * 
 * @author Yasmina Kammeyer
 */
public class InstanceProviderParameter extends AbstractCommonParameter {

	// private static final ALogger log =
	// ALoggerFactory.getLogger(InstanceProviderParameter.class);

	private final Class<?> binding;
	// private final Validator validator;
	private final List<String> enumeration;
	// true if the parameter is optional
	private final boolean optional;

	/**
	 * Create a InstanceProvider parameter definition
	 * 
	 * @param conf the configuration element
	 */
	public InstanceProviderParameter(IConfigurationElement conf) {
		super(conf.getChildren("parameter")[0]);
		IConfigurationElement[] children = conf.getChildren("parameter");

		this.binding = ParameterUtil.getBinding(children[0]);
		this.enumeration = ParameterUtil.getEnumeration(children[0]);

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

}
