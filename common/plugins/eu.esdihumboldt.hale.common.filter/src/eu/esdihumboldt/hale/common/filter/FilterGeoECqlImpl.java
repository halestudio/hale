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

import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.opengis.filter.Filter;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Extended CQL Filter. Two ECQL filters are seen as equal if they are based on
 * the same ECQL expression.
 * @author Sebastian Reinhardt
 * @author Simon Templer
 */
public class FilterGeoECqlImpl implements
		eu.esdihumboldt.hale.common.instance.model.Filter {
	
	private static final ALogger log = ALoggerFactory.getLogger(FilterGeoECqlImpl.class);

	private final String filterTerm;
	private final Filter internFilter;

	/**
	 * Create a ECQL filter.
	 * @param filterTerm the ECQL expression
	 * @throws CQLException if parsing the ECQL fails
	 */
	public FilterGeoECqlImpl(String filterTerm) throws CQLException {
		this.filterTerm = filterTerm;

		internFilter = ECQL.toFilter(this.filterTerm);
		if (internFilter == Filter.EXCLUDE) {
			log.warn("Parsed filter will not match any instance");
		}

	}

	@Override
	public boolean match(Instance instance) {
		return internFilter.evaluate(instance);
	}
	
	/**
	 * Get the ECQL expression the filter is based on.
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
		FilterGeoECqlImpl other = (FilterGeoECqlImpl) obj;
		if (filterTerm == null) {
			if (other.filterTerm != null)
				return false;
		} else if (!filterTerm.equals(other.filterTerm))
			return false;
		return true;
	}

}
