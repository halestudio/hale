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

package eu.esdihumboldt.hale.common.schema.model.constraint.factory.extension;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.ExtensionUtil;
import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension.Identifiable;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ValueConstraintFactory;

/**
 * Descriptor for a {@link ValueConstraintFactory}.
 * 
 * @author Simon Templer
 */
public class ValueConstraintFactoryDescriptor implements Identifiable {

	private final String id;
	private final Class<?> constraintType;
	private final ValueConstraintFactory<?> factory;

	/**
	 * Create a new {@link ValueConstraintFactory} descriptor based on the
	 * definition in the extension point.
	 * 
	 * @param id the descriptor identifier
	 * @param conf the configuration element defining the descriptor
	 * @throws Exception if an error occurs loading the classes or creating the
	 *             factory
	 */
	public ValueConstraintFactoryDescriptor(String id, IConfigurationElement conf) throws Exception {
		this.id = id;
		constraintType = ExtensionUtil.loadClass(conf, "type");
		String fcName = conf.getAttribute("factory");
		if (fcName != null && !fcName.isEmpty()) {
			Class<?> factoryClass = ExtensionUtil.loadClass(conf, "factory");
			factory = (ValueConstraintFactory<?>) factoryClass.newInstance();
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
	 * @return the value constraint factory, <code>null</code> if {@link Value}
	 *         conversion is not possible/allowed
	 */
	public ValueConstraintFactory<?> getFactory() {
		return factory;
	}

	/**
	 * @return the associated constraint (base) type
	 */
	public Class<?> getConstraintType() {
		return constraintType;
	}

}
