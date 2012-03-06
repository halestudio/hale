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

import eu.esdihumboldt.hale.common.filter.internal.AbstractGeotoolsFilter;

/**
 * CQL Filter. Two CQL filters are seen as equal if they are based on the same
 * CQL expression.
 * @author Sebastian Reinhardt
 * @author Simon Templer
 */
public class FilterGeoCqlImpl extends AbstractGeotoolsFilter {
	
	/**
	 * Create a CQL filter.
	 * @param filterTerm the CQL expression
	 * @throws CQLException if parsing the CQL fails
	 */
	public FilterGeoCqlImpl(String filterTerm) throws CQLException {
		super(filterTerm);
	}

	@Override
	protected Filter createFilter(String filterTerm) throws CQLException {
		return CQL.toFilter(filterTerm);
	}

}
