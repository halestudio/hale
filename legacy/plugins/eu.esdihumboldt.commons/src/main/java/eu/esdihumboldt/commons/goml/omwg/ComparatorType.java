/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.commons.goml.omwg;

/**
 * This class represents the <xs:simpleType name="comparatorEnumType">.
 * 
 * TODO: 'oneOf' and 'between' were added by mdv, might have to go elsewhere
 * 
 * @author Thorsten Reitz, Marian de Vries
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @partner 08 / Delft University of Technology
 * @version $Id$
 */
public enum ComparatorType {

	EQUAL("equal"), NOT_EQUAL("not-equal"), LESS_THAN("less-than"), LESS_THAN_OR_EQUAL(
			"less-than-or-equal"), GREATER_THAN("greater-than"), GREATER_THAN_OR_EQUAL(
			"greater-than-or-equal"), CONTAINS("contains"), STARTS_WITH(
			"starts-with"), ENDS_WITH("ends-with"), MATCHES("matches"), COLLECTION_CONTAINS(
			"collection-contains"), INCLUDES("includes"), INCLUDES_STRICTLY(
			"includes-strictly"), EMPTY("empty"), ONE_OF("oneOf"), OTHERWISE(
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
