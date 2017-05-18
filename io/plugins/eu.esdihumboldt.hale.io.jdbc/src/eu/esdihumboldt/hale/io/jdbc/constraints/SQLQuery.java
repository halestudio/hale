/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.jdbc.constraints;

import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;

/**
 * Constraint for types representing SQL queries.
 * 
 * @author Simon Templer
 */
@Constraint(mutable = false)
public class SQLQuery implements TypeConstraint {

	/**
	 * Shared instance w/o query.
	 */
	public static final SQLQuery NO_QUERY = new SQLQuery();

	@Nullable
	private final String query;

	/**
	 * Constructor with the given query.
	 *
	 * @param query the SQL query
	 */
	public SQLQuery(String query) {
		this.query = query;
	}

	/**
	 * Default constructor.
	 */
	public SQLQuery() {
		this.query = null;
	}

	/**
	 * @return the SQL query associated to the type or <code>null</code>
	 */
	@Nullable
	public String getQuery() {
		return query;
	}

	/**
	 * @return states if the constraint has an associated SQL query
	 */
	public boolean hasQuery() {
		return query != null;
	}

	@Override
	public boolean isInheritable() {
		return false;
	}
}
