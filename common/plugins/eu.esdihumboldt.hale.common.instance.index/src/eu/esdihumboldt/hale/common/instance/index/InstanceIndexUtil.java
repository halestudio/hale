/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.instance.index;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.Collection;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Reference;

/**
 * Utility class for {@link InstanceIndex}es
 * 
 * @author Florian Esser
 */
public abstract class InstanceIndexUtil {

	/**
	 * Test if two collections contain the same elements (independent of
	 * ordering)
	 * 
	 * @param c1 First collection
	 * @param c2 Second collection
	 * @return true if the collections contain the same elements
	 */
	public static boolean collectionEquals(Collection<?> c1, Collection<?> c2) {
		return c1.containsAll(c2) && c2.containsAll(c1);
	}

	/**
	 * Process a value of a property
	 * 
	 * @param value the value
	 * @param property the entity definition the value is associated to
	 * @return the processed value, possibly wrapped or replaced through a
	 *         different representation
	 */
	public static Object processValue(Object value, PropertyEntityDefinition property) {
		// extract the identifier from a reference
		value = property.getDefinition().getConstraint(Reference.class).extractId(value);

		/*
		 * This is done so values will be classified as equal even if they are
		 * of different types, e.g. Long and Integer or Integer and String.
		 */

		/*
		 * Use string representation for numbers.
		 */
		if (value instanceof Number) {
			if (value instanceof BigInteger || value instanceof Long || value instanceof Integer
					|| value instanceof Byte || value instanceof Short) {
				// use string representation for integer numbers
				value = value.toString();
			}
			else if (value instanceof BigDecimal) {
				BigDecimal v = (BigDecimal) value;
				if (v.scale() <= 0) {
					// use string representation for integer big decimal
					value = v.toBigInteger().toString();
				}
			}
		}

		/*
		 * Use string representation for URIs and URLs.
		 */
		if (value instanceof URI || value instanceof URL) {
			value = value.toString();
		}

		return value;
	}

}
