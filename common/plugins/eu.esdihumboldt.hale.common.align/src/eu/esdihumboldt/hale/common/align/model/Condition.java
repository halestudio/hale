/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.align.model;

import eu.esdihumboldt.hale.common.instance.model.Filter;
import net.jcip.annotations.Immutable;

/**
 * Property condition.
 * @author Simon Templer
 */
@Immutable
public class Condition {

	private final Filter filter;
	
	/*
	 * TODO condition scope
	 * This class is intended to hold additional information on the filter -
	 * how and to what to apply it.
	 * This could be e.g. a type (the containing main entity type) or value
	 * (the property value) scope.
	 * Currently it seems to make more sense to implement the value scope, this
	 * will be used as the only scope for now.
	 */

	/**
	 * Create a property condition.
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
		} else if (!filter.equals(other.filter))
			return false;
		return true;
	}
	
}
