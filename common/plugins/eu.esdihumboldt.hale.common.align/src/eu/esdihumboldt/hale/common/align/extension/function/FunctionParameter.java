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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.jcip.annotations.Immutable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import de.fhg.igd.eclipse.util.extension.ExtensionUtil;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Definition of a function parameter.
 * 
 * @author Simon Templer
 */
@Immutable
public final class FunctionParameter extends AbstractParameter {

	private static final ALogger log = ALoggerFactory.getLogger(FunctionParameter.class);

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
		scriptable = scriptableAttr == null ? false : Boolean.valueOf(scriptableAttr);
		IConfigurationElement[] bindingElement = conf.getChildren("parameterBinding");
		IConfigurationElement[] enumerationElement = conf.getChildren("parameterEnumeration");
		if (bindingElement.length > 0) {
			this.enumeration = null;

			// default to String
			String clazz = bindingElement[0].getAttribute("class");
			if (clazz == null)
				this.binding = String.class;
			else
				this.binding = ExtensionUtil.loadClass(bindingElement[0], "class");

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
				this.validator = validator;
			}
			else
				this.validator = null;
		}
		else if (enumerationElement.length > 0) {
			this.binding = null;
			this.validator = null;
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
			this.validator = null;
			this.enumeration = null;
			this.binding = String.class;
		}
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
