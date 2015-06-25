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

package eu.esdihumboldt.util;

/**
 * Stores identifiers for certain objects
 * 
 * @author Simon Templer
 * @param <T> the type of objects that shall be identified
 */
public class Identifiers<T> extends IdentifiersBase<T> {

	private int num = 0;
	private String prefix;

	/**
	 * Creates Identifiers with type name prefix
	 * 
	 * @param clazz the object type
	 * @param useEquals if the objects shall be compared using equals instead of
	 *            the == operator
	 */
	public Identifiers(Class<T> clazz, boolean useEquals) {
		this(clazz.getSimpleName() + "_", useEquals);
	}

	/**
	 * Creates Identifiers with the given prefix
	 * 
	 * @param prefix the identifier prefix
	 * @param useEquals if the objects shall be compared using equals instead of
	 *            the == operator
	 */
	public Identifiers(String prefix, boolean useEquals) {
		this(prefix, useEquals, 0);
	}

	/**
	 * Creates Identifiers with the given prefix
	 * 
	 * @param prefix the identifier prefix
	 * @param useEquals if the objects shall be compared using equals instead of
	 *            the == operator
	 * @param startCounter number given to first identifier
	 */
	public Identifiers(String prefix, boolean useEquals, int startCounter) {
		super(useEquals);
		this.prefix = prefix;
		this.num = startCounter;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Get the id of the given object.
	 * 
	 * @param object the object
	 * @return the id of the object
	 */
	@Override
	public final String getId(T object) {
		String id = ids.get(object);

		if (id == null) {
			while (id == null || objects.containsKey(id)) {
				// make sure the ID was not already added through other means
				id = prefix + num++;
			}
			putObjectIdentifier(object, id);
			onInsertion(num - 1, id, object);
		}

		return id;
	}

	/**
	 * @param num the number used to generate the id
	 * @param id the id corresponding num
	 * @param object the object just
	 */
	protected void onInsertion(int num, String id, T object) {
		/* do nothing */
	}
}
