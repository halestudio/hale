/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.service.values;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AugmentedValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;

/**
 * Utility methods related to the {@link OccurringValuesService}.
 * 
 * @author Simon Templer
 */
public class OccurringValuesUtil {

	/**
	 * Tests if for the given property the occurring values may be determined.
	 * 
	 * @param property the property entity definition
	 * @return if determining the occurring values is allowed for this property
	 */
	public static boolean supportsOccurringValues(PropertyEntityDefinition property) {
		// must have a value
		boolean value = property.getDefinition().getPropertyType()
				.getConstraint(HasValueFlag.class).isEnabled()
				|| property.getDefinition().getPropertyType()
						.getConstraint(AugmentedValueFlag.class).isEnabled();
		if (!value)
			return false;
		// mustn't be a geometry
		if (property.getDefinition().getPropertyType().getConstraint(GeometryType.class)
				.isGeometry()) {
			return false;
		}
		// XXX only allow specific bindings? e.g. convertable to String?

		return true;
	}

}
