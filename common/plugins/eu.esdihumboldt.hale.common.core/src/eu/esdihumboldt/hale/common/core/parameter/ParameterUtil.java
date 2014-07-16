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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import de.fhg.igd.eclipse.util.extension.ExtensionUtil;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.extension.ComplexValueDefinition;
import eu.esdihumboldt.hale.common.core.io.extension.ComplexValueExtension;

/**
 * Parameter definition utilities.
 * 
 * @author Simon Templer
 */
public class ParameterUtil {

	private static final ALogger log = ALoggerFactory.getLogger(ParameterUtil.class);

	/**
	 * Name of the configuration element defining a value enumeration for a
	 * parameter.
	 */
	public static final String CONF_PARAMETER_ENUMERATION = "parameterEnumeration";

	/**
	 * Name of the configuration element defining a class binding for a
	 * parameter.
	 */
	public static final String CONF_PARAMETER_BINDING = "parameterBinding";

	/**
	 * Name of the configuration element defining a complex value binding for a
	 * parameter.
	 */
	public static final String CONF_PARAMETER_COMPLEX_VALUE = "complexValue";

	/**
	 * Determine the binding of a defined parameter.
	 * 
	 * @param parameterConf the configuration element defining the parameter
	 * @return the binding class or <code>null</code>
	 */
	public static @Nullable
	Class<?> getBinding(IConfigurationElement parameterConf) {
		IConfigurationElement[] bindingElement = parameterConf.getChildren(CONF_PARAMETER_BINDING);
		IConfigurationElement[] enumerationElement = parameterConf
				.getChildren(CONF_PARAMETER_ENUMERATION);
		IConfigurationElement[] complexValueElement = parameterConf
				.getChildren(CONF_PARAMETER_COMPLEX_VALUE);
		if (bindingElement.length > 0) {
			// default to String
			String clazz = bindingElement[0].getAttribute("class");
			if (clazz == null)
				return String.class;
			else
				return ExtensionUtil.loadClass(bindingElement[0], "class");
		}
		else if (enumerationElement.length > 0) {
			return String.class;
		}
		else if (complexValueElement.length > 0) {
			String complexValueId = complexValueElement[0].getAttribute("ref");
			if (complexValueId != null) {
				ComplexValueDefinition cv = ComplexValueExtension.getInstance().get(complexValueId);
				if (cv != null) {
					return cv.getValueType();
				}
			}
			return null;
		}
		else {
			// default
			return String.class;
		}
	}

	/**
	 * Get the complex value definition of a defined parameter if applicable.
	 * 
	 * @param parameterConf the configuration element defining the parameter
	 * @return the complex value definition or <code>null</code>
	 */
	public static @Nullable
	ComplexValueDefinition getComplexValueDefinition(IConfigurationElement parameterConf) {
		IConfigurationElement[] complexValueElement = parameterConf
				.getChildren(CONF_PARAMETER_COMPLEX_VALUE);
		if (complexValueElement.length > 0) {
			String complexValueId = complexValueElement[0].getAttribute("ref");
			if (complexValueId != null) {
				return ComplexValueExtension.getInstance().get(complexValueId);
			}
		}

		// default
		return null;
	}

	/**
	 * Determine the value enumeration of a defined parameter.
	 * 
	 * @param parameterConf the configuration element defining the parameter
	 * @return the list of values or <code>null</code>
	 */
	public static @Nullable
	List<String> getEnumeration(IConfigurationElement parameterConf) {
		IConfigurationElement[] enumerationElement = parameterConf
				.getChildren(CONF_PARAMETER_ENUMERATION);
		if (enumerationElement.length > 0) {
			// must be present, otherwise xml is invalid
			IConfigurationElement[] enumerationValues = enumerationElement[0]
					.getChildren("enumerationValue");
			List<String> enumeration = new ArrayList<String>(enumerationValues.length);
			for (IConfigurationElement value : enumerationValues)
				enumeration.add(value.getAttribute("value"));
			return Collections.unmodifiableList(enumeration);
		}
		else {
			// default
			return null;
		}
	}

	/**
	 * Determine the validator for a defined parameter.
	 * 
	 * @param parameterConf the configuration element defining the parameter
	 * @return the validator or <code>null</code>
	 */
	public static @Nullable
	Validator getValidator(IConfigurationElement parameterConf) {
		IConfigurationElement[] bindingElement = parameterConf.getChildren(CONF_PARAMETER_BINDING);
		IConfigurationElement[] enumerationElement = parameterConf
				.getChildren(CONF_PARAMETER_ENUMERATION);
		if (bindingElement.length > 0) {
			IConfigurationElement[] validatorElement = bindingElement[0].getChildren("validator");
			if (validatorElement.length > 0) {
				Validator validator = null;
				try {
					validator = (Validator) validatorElement[0].createExecutableExtension("class");
					ListMultimap<String, String> parameters = ArrayListMultimap.create();
					for (IConfigurationElement parameter : validatorElement[0]
							.getChildren("validatorParameter"))
						parameters.put(parameter.getAttribute("name"),
								parameter.getAttribute("value"));
					validator.setParameters(parameters);
				} catch (CoreException e) {
					log.error("Error creating validator from extension", e);
				}
				return validator;
			}
			else
				return null;
		}
		else if (enumerationElement.length > 0) {
			// XXX instead return a validator for the enumeration?
			return null;
		}
		else {
			// default
			return null;
		}
	}

}
