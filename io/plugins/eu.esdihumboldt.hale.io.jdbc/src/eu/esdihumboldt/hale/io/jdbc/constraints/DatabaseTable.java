/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.jdbc.constraints;

import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.jdbc.JDBCUtil;

/**
 * Constraint specifying schema and table name for a database table.
 * 
 * @author Simon Templer
 */
@Constraint(mutable = false)
public class DatabaseTable implements TypeConstraint {

	private final String schemaName;

	private final String tableName;

	// for usage quotation in {@link #getFullTableName}. Added for MsAccess
	// Database support.
	private final boolean useQuote;

	/**
	 * Create a default constraint. The default table name is the given type
	 * local name.
	 * 
	 * @param type the type definition the table is associated to
	 */
	public DatabaseTable(TypeDefinition type) {
		this(null, type.getName().getLocalPart());
	}

	/**
	 * Create a constraint with the given schema and table names
	 * 
	 * @param schemaName the schema name, may be <code>null</code>
	 * @param tableName the table name
	 */
	public DatabaseTable(String schemaName, String tableName) {
		this(schemaName, tableName, false);
	}

	/**
	 * Create a constraint with the given schema and table names and quotation
	 * usage decision as boolean value
	 * 
	 * @param schemaName the schema name, may be <code>null</code>
	 * @param tableName the table name
	 * @param useQuote true if quotation needed in {@link #getFullTableName},
	 *            else false
	 */
	public DatabaseTable(String schemaName, String tableName, boolean useQuote) {
		super();
		this.schemaName = schemaName;
		this.tableName = tableName;
		this.useQuote = useQuote;
	}

	/**
	 * @return the schema name of the database table, may be <code>null</code>
	 *         or empty
	 */
	public String getSchemaName() {
		return schemaName;
	}

	/**
	 * @return the table name of the database table
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * Get the full table name to use in queries.
	 * 
	 * @return the full table name
	 */
	public String getFullTableName() {
		if (schemaName == null || schemaName.isEmpty()) {
			return getQuotedValue(tableName);
		}
		else {
			return getQuotedValue(schemaName) + '.' + getQuotedValue(tableName);
		}
	}

	/**
	 * Get quoted value by deciding on {@link #useQuote} parameter.
	 * 
	 * @param value String
	 * @return quoted or unquoted string
	 */
	private String getQuotedValue(String value) {
		if (useQuote) {
			value = JDBCUtil.quote(value);
		}
		return value;
	}

	@Override
	public boolean isInheritable() {
		return false;
	}

}
