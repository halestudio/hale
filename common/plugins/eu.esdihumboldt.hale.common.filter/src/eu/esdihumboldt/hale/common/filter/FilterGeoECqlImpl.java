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

import eu.esdihumboldt.hale.common.filter.internal.AbstractGeotoolsFilter;

/**
 * Extended CQL Filter. Two ECQL filters are seen as equal if they are based on
 * the same ECQL expression.
 * 
 * @author Sebastian Reinhardt
 * @author Simon Templer
 */
public class FilterGeoECqlImpl extends AbstractGeotoolsFilter {

	/**
	 * Create a ECQL filter.
	 * 
	 * @param filterTerm the ECQL expression
	 * @throws CQLException if parsing the ECQL fails
	 */
	public FilterGeoECqlImpl(String filterTerm) throws CQLException {
		super(filterTerm);
	}

	@Override
	protected Filter createFilter(String filterTerm) throws CQLException {
		return ECQL.toFilter(filterTerm);
	}

}
