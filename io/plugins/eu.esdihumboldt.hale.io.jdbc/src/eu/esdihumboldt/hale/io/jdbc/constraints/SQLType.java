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

package eu.esdihumboldt.hale.io.jdbc.constraints;

import java.util.HashMap;
import java.util.Map;

import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;

/**
 * Constraint for the SQL type.
 * @author Kai Schwierczek
 */
@Constraint(mutable = false)
public class SQLType implements TypeConstraint {
	private static final Map<Integer, SQLType> types = new HashMap<Integer, SQLType>();

	private final int type;

	/**
	 * Returns the SQLType constraint for the given type.
	 *
	 * @see java.sql.Types
	 * @param type the SQL type
	 * @return the constraint SQLType
	 */
	public static SQLType get(int type) {
		SQLType sqlType = types.get(type);
		if (sqlType == null)
			types.put(type, sqlType = new SQLType(type));
		return sqlType;
	}

	/**
	 * Constructor with the given type.
	 *
	 * @see java.sql.Types
	 * @param type the SQL type
	 */
	private SQLType(int type) {
		this.type = type;
	}

	/**
	 * Default constructor. type is set to {@link Integer#MIN_VALUE}.
	 */
	public SQLType() {
		type = Integer.MIN_VALUE;
	}

	/**
	 * Returns the set SQL type. If no type was set it is
	 * {@link Integer#MIN_VALUE}.
	 *
	 * @see java.sql.Types
	 * @return the set SQL type or {@link Integer#MIN_VALUE}
	 */
	public int getType() {
		return type;
	}

	/**
	 * Returns whether the SQL type was actually set.
	 *
	 * @return whether the SQL type was actually set
	 */
	public boolean isSet() {
		return type != Integer.MIN_VALUE;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.schema.model.TypeConstraint#isInheritable()
	 */
	@Override
	public boolean isInheritable() {
		return true; // XXX not completely sure about this...
	}
}
