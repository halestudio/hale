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
import org.geotools.filter.text.ecql.ECQL;
import org.opengis.filter.Filter;


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
