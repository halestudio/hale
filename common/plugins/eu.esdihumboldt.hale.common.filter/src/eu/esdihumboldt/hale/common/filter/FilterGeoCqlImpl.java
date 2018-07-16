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

import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.filter.Filter;

/**
 * CQL Filter. Two CQL filters are seen as equal if they are based on the same
 * CQL expression.
 * 
 * @author Sebastian Reinhardt
 * @author Simon Templer
 */
public class FilterGeoCqlImpl extends AbstractGeotoolsFilter {

	/**
	 * Create a CQL filter.
	 * 
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

	@Override
	protected String toFilterTerm(Filter filter) throws CQLException {
		return CQL.toCQL(filter);
	}

	@Override
	protected FilterGeoCqlImpl buildFilter(String filterTerm) throws CQLException {
		return new FilterGeoCqlImpl(filterTerm);
	}

}
