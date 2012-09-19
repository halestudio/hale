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
package eu.esdihumboldt.hale.io.oml.internal.goml.omwg;

/**
 * This class represents the <xs:simpleType name="comparatorEnumType">.
 * 
 * TODO: 'oneOf' and 'between' were added by mdv, might have to go elsewhere
 * 
 * @author Thorsten Reitz, Marian de Vries
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @partner 08 / Delft University of Technology
 */
@SuppressWarnings("javadoc")
public enum ComparatorType {

	EQUAL("equal"), NOT_EQUAL("not-equal"), LESS_THAN("less-than"), LESS_THAN_OR_EQUAL(
			"less-than-or-equal"), GREATER_THAN("greater-than"), GREATER_THAN_OR_EQUAL(
			"greater-than-or-equal"), CONTAINS("contains"), STARTS_WITH("starts-with"), ENDS_WITH(
			"ends-with"), MATCHES("matches"), COLLECTION_CONTAINS("collection-contains"), INCLUDES(
			"includes"), INCLUDES_STRICTLY("includes-strictly"), EMPTY("empty"), ONE_OF("oneOf"), OTHERWISE(
			"otherwise"), BETWEEN("between");

	private final String value;

	ComparatorType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static ComparatorType fromValue(String v) {
		for (ComparatorType c : ComparatorType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		// throw new IllegalArgumentException(v); TODO clean up
		return null;
	}

}
