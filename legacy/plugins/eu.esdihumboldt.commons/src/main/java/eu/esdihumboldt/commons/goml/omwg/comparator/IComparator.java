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
package eu.esdihumboldt.commons.goml.omwg.comparator;

import eu.esdihumboldt.commons.goml.omwg.Restriction;

/**
 * This interface represents the <xs:simpleType name="comparatorEnumType">.
 * 
 * @author Mark Doyle (Logica)
 * 
 * 
 */
public interface IComparator {

	/**
	 * Evaluates the source property against a Restriction. What this means in
	 * detail depends upon the concrete IComparator implementations; this simply
	 * provides the interface for clients to call.
	 * 
	 * @param sourceRestriction
	 *            source java.util.List of Restriction.
	 * @return true or false depending upon evaluation defined by the concrete
	 *         IComparator implementation.
	 * @see Restriction
	 */
	public boolean evaluate(Restriction sourceRestriction,
			org.opengis.feature.Property sourceProp);

}
