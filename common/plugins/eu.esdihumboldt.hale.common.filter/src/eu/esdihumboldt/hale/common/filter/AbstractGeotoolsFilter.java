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

package eu.esdihumboldt.hale.common.filter;

import org.geotools.filter.text.cql2.CQLException;
import org.opengis.filter.Filter;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Geotools based filter. Two filters are seen as equal if they are based on the
 * same filter expression.
 * 
 * @author Sebastian Reinhardt
 * @author Simon Templer
 */
public abstract class AbstractGeotoolsFilter implements
		eu.esdihumboldt.hale.common.instance.model.Filter {

	private static final ALogger log = ALoggerFactory.getLogger(AbstractGeotoolsFilter.class);

	private final String filterTerm;
	private final Filter internFilter;

	/**
	 * Create a Geotools based filter.
	 * 
	 * @param filterTerm the filter expression
	 * @throws CQLException if parsing the filter expression fails
	 */
	public AbstractGeotoolsFilter(String filterTerm) throws CQLException {
		this.filterTerm = filterTerm;

		internFilter = createFilter(this.filterTerm);
		if (internFilter == Filter.EXCLUDE) {
			log.warn("Parsed filter will not match any instance");
		}
	}

	/**
	 * Create the fitler from the filter term.
	 * 
	 * @param filterTerm the filter term
	 * @return the filter
	 * @throws CQLException if an error occurs on filter creation
	 */
	protected abstract Filter createFilter(String filterTerm) throws CQLException;

	@Override
	public boolean match(Instance instance) {
		PropertyResolver.isLastQueryPathUnique(); // reset the information on
													// the last query
		try {
			return internFilter.evaluate(instance);
		} finally {
			if (!PropertyResolver.isLastQueryPathUnique()) {
				log.warn("Evaluated filter with non-unique definition path: " + filterTerm);
			}
		}
	}

	/**
	 * Get the ECQL expression the filter is based on.
	 * 
	 * @return the ECQL expression
	 */
	public String getFilterTerm() {
		return filterTerm;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filterTerm == null) ? 0 : filterTerm.hashCode());
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
		AbstractGeotoolsFilter other = (AbstractGeotoolsFilter) obj;
		if (filterTerm == null) {
			if (other.filterTerm != null)
				return false;
		}
		else if (!filterTerm.equals(other.filterTerm))
			return false;
		return true;
	}

}
