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

package eu.esdihumboldt.hale.common.schema.model.validate.factory.extension;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.ExtensionUtil;
import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension.Identifiable;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.model.validate.factory.ValidatorFactory;

/**
 * Descriptor for a {@link ValidatorFactory}.
 * 
 * @author Simon Templer
 */
public class ValidatorFactoryDescriptor implements Identifiable {

	private final String id;
	private final Class<?> validatorType;
	private final ValidatorFactory<?> factory;

	/**
	 * Create a new {@link ValidatorFactory} descriptor based on the definition
	 * in the extension point.
	 * 
	 * @param id the descriptor identifier
	 * @param conf the configuration element defining the descriptor
	 * @throws Exception if an error occurs loading the classes or creating the
	 *             factory
	 */
	public ValidatorFactoryDescriptor(String id, IConfigurationElement conf) throws Exception {
		this.id = id;
		validatorType = ExtensionUtil.loadClass(conf, "type");
		String fcName = conf.getAttribute("factory");
		if (fcName != null && !fcName.isEmpty()) {
			Class<?> factoryClass = ExtensionUtil.loadClass(conf, "factory");
			factory = (ValidatorFactory<?>) factoryClass.newInstance();
		}
		else {
			factory = null;
		}
	}

	@Override
	public String getId() {
		return id;
	}

	/**
	 * @return the validator value factory, <code>null</code> if {@link Value}
	 *         conversion is not possible/allowed
	 */
	public ValidatorFactory<?> getFactory() {
		return factory;
	}

	/**
	 * @return the associated constraint (base) type
	 */
	public Class<?> getValidatorType() {
		return validatorType;
	}

}
