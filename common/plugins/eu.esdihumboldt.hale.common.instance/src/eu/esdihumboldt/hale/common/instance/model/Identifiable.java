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

package eu.esdihumboldt.hale.common.instance.model;

/**
 * Interface for instance or instance reference classes that provide a way to
 * uniquely identify individual instances.
 * 
 * @author Florian Esser
 */
public interface Identifiable {

	/**
	 * @return the instance identifier
	 */
	Object getId();

	/**
	 * @return true if the instance has an identifier
	 */
	default boolean hasId() {
		return getId() != null;
	}

	/**
	 * Test if an object is identifiable
	 * 
	 * @param o Test subject
	 * @return true if <code>o</code> implements {@link Identifiable} and
	 *         actually has an ID
	 */
	static boolean is(Object o) {
		return (o instanceof Identifiable && ((Identifiable) o).hasId());
	}

	/**
	 * Return the ID of the given object
	 * 
	 * @param o Object to retrieve ID from
	 * @return The object's ID or <code>null</code> if <code>o</code> is either
	 *         not {@link Identifiable} or doesn't have an ID
	 */
	static Object getId(Object o) {
		if (is(o)) {
			return ((Identifiable) o).getId();
		}

		return null;
	}
}
