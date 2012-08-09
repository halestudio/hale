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
