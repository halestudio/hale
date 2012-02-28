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

package eu.esdihumboldt.hale.common.filter;

import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.filter.Filter;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * CQL Filter. Two CQL filters are seen as equal if they are based on the same
 * CQL expression.
 * @author Sebastian Reinhardt
 * @author Simon Templer
 */
public class FilterGeoCqlImpl implements
		eu.esdihumboldt.hale.common.instance.model.Filter {
	
	private static final ALogger log = ALoggerFactory.getLogger(FilterGeoCqlImpl.class);

	private final String filterTerm;
	private final Filter internFilter;

	/**
	 * Create a CQL filter.
	 * @param filterTerm the CQL expression
	 * @throws CQLException if parsing the CQL fails
	 */
	public FilterGeoCqlImpl(String filterTerm) throws CQLException {
		this.filterTerm = filterTerm;

		internFilter = CQL.toFilter(this.filterTerm);
		if (internFilter == Filter.EXCLUDE) {
			log.warn("Parsed filter will not match any instance");
		}
	}

	@Override
	public boolean match(Instance instance) {
		return internFilter.evaluate(instance);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((filterTerm == null) ? 0 : filterTerm.hashCode());
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
		FilterGeoCqlImpl other = (FilterGeoCqlImpl) obj;
		if (filterTerm == null) {
			if (other.filterTerm != null)
				return false;
		} else if (!filterTerm.equals(other.filterTerm))
			return false;
		return true;
	}

}
