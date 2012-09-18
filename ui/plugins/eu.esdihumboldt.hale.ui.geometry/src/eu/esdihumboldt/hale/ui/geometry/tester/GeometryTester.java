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
