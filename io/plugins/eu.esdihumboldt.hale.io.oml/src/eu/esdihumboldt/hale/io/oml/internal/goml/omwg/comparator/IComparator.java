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
package eu.esdihumboldt.hale.io.oml.internal.goml.omwg.comparator;

import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.Restriction;

/**
 * This interface represents the <xs:simpleType name="comparatorEnumType">.
 * 
 * @author Mark Doyle (Logica)
 */
public interface IComparator {

	/**
	 * Evaluates the source property against a Restriction. What this means in
	 * detail depends upon the concrete IComparator implementations; this simply
	 * provides the interface for clients to call.
	 * 
	 * @param sourceRestriction source java.util.List of Restriction.
	 * @param sourceProp the source property
	 * @return true or false depending upon evaluation defined by the concrete
	 *         IComparator implementation.
	 * @see Restriction
	 */
	public boolean evaluate(Restriction sourceRestriction, org.opengis.feature.Property sourceProp);

}
