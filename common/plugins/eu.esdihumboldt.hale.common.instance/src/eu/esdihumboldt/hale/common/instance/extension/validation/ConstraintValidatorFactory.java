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

package eu.esdihumboldt.hale.common.instance.extension.validation;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.ExtensionUtil;

/**
 * Factory for constraint validators.
 * 
 * @author Kai Schwierczek
 */
public class ConstraintValidatorFactory extends AbstractConfigurationFactory<ConstraintValidator> {

	/**
	 * Create a {@link ConstraintValidator} factory based on the given
	 * configuration element.
	 * 
	 * @param conf the configuration element
	 */
	public ConstraintValidatorFactory(IConfigurationElement conf) {
		super(conf, "validator");
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory#dispose(java.lang.Object)
	 */
	@Override
	public void dispose(ConstraintValidator arg0) {
		// do nothing
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return getIdentifier();
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return conf.getAttribute("id");
	}

	/**
	 * Checks whether this factory belongs to a type constraint.
	 * 
	 * @return true, if this factory belongs to a type constraint, false
	 *         otherwise
	 */
	public boolean isTypeConstraintValidator() {
		return conf.getName().equals("typeConstraintValidator");
	}

	/**
	 * Checks whether this factory belongs to a property constraint.
	 * 
	 * @return true, if this factory belongs to a property constraint, false
	 *         otherwise
	 */
	public boolean isPropertyConstraintValidator() {
		return conf.getName().equals("propertyConstraintValidator");
	}

	/**
	 * Checks whether this factory belongs to a group property constraint.
	 * 
	 * @return true, if this factory belongs to a group property constraint,
	 *         false otherwise
	 */
	public boolean isGroupPropertyConstraintValidator() {
		return conf.getName().equals("groupPropertyConstraintValidator");
	}

	/**
	 * Get the class of the associated constraint.
	 * 
	 * @return the class of the associated constraint.
	 */
	public Class<?> getConstraintClass() {
		return ExtensionUtil.loadClass(conf, "constraint");
	}
}
