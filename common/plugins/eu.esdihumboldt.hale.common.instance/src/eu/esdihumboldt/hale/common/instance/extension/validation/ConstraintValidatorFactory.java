/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.instance.extension.validation;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.AbstractConfigurationFactory;
import de.cs3d.util.eclipse.extension.ExtensionUtil;

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
	 * @see de.cs3d.util.eclipse.extension.ExtensionObjectFactory#dispose(java.lang.Object)
	 */
	@Override
	public void dispose(ConstraintValidator arg0) {
		// do nothing
	}

	/**
	 * @see de.cs3d.util.eclipse.extension.ExtensionObjectDefinition#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return getIdentifier();
	}

	/**
	 * @see de.cs3d.util.eclipse.extension.ExtensionObjectDefinition#getIdentifier()
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
