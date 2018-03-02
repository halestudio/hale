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

import org.geotools.filter.IsEqualsToImpl;
import org.geotools.filter.LiteralExpressionImpl;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;

/**
 * Extended CQL Filter. Two ECQL filters are seen as equal if they are based on
 * the same ECQL expression.
 * 
 * @author Sebastian Reinhardt
 * @author Simon Templer
 */
public class FilterGeoECqlImpl extends AbstractGeotoolsFilter {

	private class EqualsTrue extends IsEqualsToImpl {

		public EqualsTrue(Expression expression) {
			super(expression, new LiteralExpressionImpl(true), MatchAction.ANY);
		}
	}

	/**
	 * Create an ECQL filter.
	 * 
	 * @param filterTerm the ECQL expression
	 * @throws CQLException if parsing the ECQL fails
	 */
	public FilterGeoECqlImpl(String filterTerm) throws CQLException {
		super(filterTerm);
	}

	@Override
	protected Filter createFilter(String filterTerm) throws CQLException {
		CQLException filterException = null;
		try {
			return ECQL.toFilter(filterTerm);
		} catch (CQLException e) {
			filterException = e;
		}

		// Try if filterTerm can be evaluated as an Expression and if so,
		// use it in a PropertyIsEqualTo filter. This is the same as if
		// "= true" were added to the filter term.
		Expression expr;
		try {
			expr = ECQL.toExpression(filterTerm);
			return new EqualsTrue(expr);
		} catch (CQLException e) {
			throw filterException;
		}

	}

}
