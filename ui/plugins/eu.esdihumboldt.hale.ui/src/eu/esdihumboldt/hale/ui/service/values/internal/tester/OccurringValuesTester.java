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

package eu.esdihumboldt.hale.ui.service.values.internal.tester;

import org.eclipse.core.expressions.IPropertyTester;
import org.eclipse.core.expressions.PropertyTester;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.ui.service.values.OccurringValuesService;
import eu.esdihumboldt.hale.ui.service.values.OccurringValuesUtil;

/**
 * Tester for properties related to {@link OccurringValuesService}.
 * 
 * @author Simon Templer
 */
public class OccurringValuesTester extends PropertyTester {

	/**
	 * The property namespace for this tester.
	 */
	public static final String NAMESPACE = "eu.esdihumboldt.hale.ui.service.values";

	/**
	 * The property that specifies if updating the occurring values for property
	 * is allowed.
	 */
	public static final String PROPERTY_ALLOW_UPDATE = "allow_update";

	/**
	 * @see IPropertyTester#test(Object, String, Object[], Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver == null) {
			return false;
		}

		if (property.equals(PROPERTY_ALLOW_UPDATE) && receiver instanceof EntityDefinition) {
			return testAllowUpdate((PropertyEntityDefinition) receiver);
		}

		return false;
	}

	/**
	 * Tests if for the given property the occurring values may be determined.
	 * 
	 * @param property the property entity definition
	 * @return if determining the occurring values is allowed for this property
	 */
	private boolean testAllowUpdate(PropertyEntityDefinition property) {
		return OccurringValuesUtil.supportsOccurringValues(property);
	}

}
