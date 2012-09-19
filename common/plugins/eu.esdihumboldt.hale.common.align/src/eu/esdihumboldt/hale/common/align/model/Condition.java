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

package eu.esdihumboldt.hale.common.align.model;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.instance.model.Filter;

/**
 * Property condition.
 * 
 * @author Simon Templer
 */
@Immutable
public class Condition {

	private final Filter filter;

	/*
	 * TODO condition scope This class is intended to hold additional
	 * information on the filter - how and to what to apply it. This could be
	 * e.g. a type (the containing main entity type) or value (the property
	 * value) scope. Currently it seems to make more sense to implement the
	 * value scope, this will be used as the only scope for now.
	 */

	/**
	 * Create a property condition.
	 * 
	 * @param filter the property filter
	 */
	public Condition(Filter filter) {
		super();
		this.filter = filter;
	}

	/**
	 * @return the filter
	 */
	public Filter getFilter() {
		return filter;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filter == null) ? 0 : filter.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Condition other = (Condition) obj;
		if (filter == null) {
			if (other.filter != null)
				return false;
		}
		else if (!filter.equals(other.filter))
			return false;
		return true;
	}

}
