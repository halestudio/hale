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

package eu.esdihumboldt.hale.ui.geometry.tester;

import org.eclipse.core.expressions.PropertyTester;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;

/**
 * Tester for properties related to geometries.
 * 
 * @author Simon Templer
 */
public class GeometryTester extends PropertyTester {

	/**
	 * The property namespace for this tester.
	 */
	public static final String NAMESPACE = "eu.esdihumboldt.hale.ui.geometry";

	/**
	 * The property that specifies if setting an entity definition as a default
	 * geometry is allowed.
	 */
	public static final String PROPERTY_ALLOW_SET_DEFAULT = "allow_set_default";

	/**
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object,
	 *      java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver == null) {
			return false;
		}

		if (property.equals(PROPERTY_ALLOW_SET_DEFAULT) && receiver instanceof EntityDefinition) {
			return testAllowSetDefault((EntityDefinition) receiver);
		}

		return false;
	}

	private boolean testAllowSetDefault(EntityDefinition receiver) {
		// TODO test if this is a geometry property or if there are any child
		// geometry properies?
		// XXX this doesn't seem necessary, the user should decide

		// ensure that the entity definition doesn't represent a type
		return !receiver.getPropertyPath().isEmpty();
	}

}
