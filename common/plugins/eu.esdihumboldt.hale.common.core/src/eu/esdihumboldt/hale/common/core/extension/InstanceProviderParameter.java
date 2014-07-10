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

package eu.esdihumboldt.hale.common.core.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.ExtensionUtil;

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
		IConfigurationElement[] child = conf.getChildren("parameter");
		IConfigurationElement[] bindingElement = child[0].getChildren("parameterBinding");
		IConfigurationElement[] enumerationElement = child[0].getChildren("parameterEnumeration");
		boolean optionalElement = Boolean.parseBoolean(conf.getAttribute("optional"));

		if (bindingElement.length > 0) {
			this.enumeration = null;

			// default to String
			String clazz = bindingElement[0].getAttribute("class");
			if (clazz == null)
				this.binding = String.class;
			else
				this.binding = ExtensionUtil.loadClass(bindingElement[0], "class");
		}
		else if (enumerationElement.length > 0) {
			this.binding = null;
			// this.validator = null;
			// must be present, otherwise xml is invalid
			IConfigurationElement[] enumerationValues = enumerationElement[0]
					.getChildren("enumerationValue");
			List<String> enumeration = new ArrayList<String>(enumerationValues.length);
			for (IConfigurationElement value : enumerationValues)
				enumeration.add(value.getAttribute("value"));
			this.enumeration = Collections.unmodifiableList(enumeration);
		}
		else {
			// default
			// this.validator = null;
			this.enumeration = null;
			this.binding = String.class;
		}
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
