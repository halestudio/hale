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

import net.jcip.annotations.Immutable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

/**
 * Definition of a function parameter.
 * 
 * @author Simon Templer
 */
@Immutable
public final class FunctionParameter extends AbstractParameter {

	private static final ALogger log = ALoggerFactory.getLogger(FunctionParameter.class);

	private final Validator validator;

	/**
	 * Create a function parameter definition.
	 * 
	 * @param conf the configuration element
	 */
	public FunctionParameter(IConfigurationElement conf) {
		super(conf);
		validator = createValidator(conf);
	}

	private static Validator createValidator(IConfigurationElement conf) {
		IConfigurationElement[] validatorElement = conf.getChildren("validator");
		if (validatorElement.length > 0) {
			try {
				Validator validator = (Validator) validatorElement[0]
						.createExecutableExtension("class");
				ListMultimap<String, String> parameters = ArrayListMultimap.create();
				for (IConfigurationElement parameter : validatorElement[0].getChildren("parameter"))
					parameters.put(parameter.getAttribute("name"), parameter.getAttribute("value"));
				validator.setParameters(parameters);
				return validator;
			} catch (CoreException e) {
				log.error("Error creating validator from extension", e);
			}
		}
		return null;
	}

	/**
	 * Returns the validator associated with this function parameter or null if
	 * there is none.
	 * 
	 * @return the validator
	 */
	public Validator getValidator() {
		return validator;
	}
}
