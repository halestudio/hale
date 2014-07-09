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

package eu.esdihumboldt.hale.common.align.extension.category;

import net.jcip.annotations.Immutable;
import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension.Identifiable;

/**
 * Represents a function category. Usually provided through the corresponding
 * extension point.
 * 
 * @author Simon Templer
 */
@Immutable
public final class Category implements Identifiable, Comparable<Category> {

	private final String id;
	private final String name;
	private final String description;

	/**
	 * Create a function category
	 * 
	 * @param id the category id
	 * @param name the category name
	 * @param description the category description
	 */
	public Category(String id, String name, String description) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
	}

	/**
	 * @return the category id
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @return the category name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the category description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Category o) {
		int result = getName().compareToIgnoreCase(o.getName());

		if (result == 0) {
			result = getId().compareToIgnoreCase(o.getId());
		}

		return result;
	}

}
