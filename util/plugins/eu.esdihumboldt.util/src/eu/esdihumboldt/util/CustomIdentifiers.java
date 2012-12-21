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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.util;

/**
 * Identifiers that allows custom identifiers to be used through
 * {@link #getId(Object, String)}.
 * 
 * @author Simon Templer
 * @param <T> the type of objects that shall be identified
 */
public class CustomIdentifiers<T> extends Identifiers<T> {

	/**
	 * @see Identifiers#Identifiers(Class, boolean)
	 */
	public CustomIdentifiers(Class<T> clazz, boolean useEquals) {
		super(clazz, useEquals);
	}

	/**
	 * @see Identifiers#Identifiers(String, boolean, int)
	 */
	public CustomIdentifiers(String prefix, boolean useEquals, int startCounter) {
		super(prefix, useEquals, startCounter);
	}

	/**
	 * @see Identifiers#Identifiers(String, boolean)
	 */
	public CustomIdentifiers(String prefix, boolean useEquals) {
		super(prefix, useEquals);
	}

	/**
	 * Get the identifier for the given object. If not already present, a new
	 * identifier will be assigned, if possible the given desired identifier is
	 * used.
	 * 
	 * @param object the object to identify
	 * @param desiredId the desired identifier
	 * @return the object identifier, if the identifier was newly assigned this
	 *         is either the desiredId or the desiredId with a suffix
	 */
	public String getId(T object, final String desiredId) {
		String currentId = fetchId(object);
		if (currentId != null) {
			return currentId;
		}

		String id = desiredId;
		int num = 2;
		while (getObject(id) != null) {
			// change id
			id = desiredId + "_" + num++;
		}
		putObjectIdentifier(object, id);
		return id;
	}

}
