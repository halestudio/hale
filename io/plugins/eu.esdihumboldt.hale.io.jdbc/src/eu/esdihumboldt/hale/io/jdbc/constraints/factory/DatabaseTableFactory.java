/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.jdbc.constraints.factory;

import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.ValueProperties;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ClassResolver;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ValueConstraintFactory;
import eu.esdihumboldt.hale.io.jdbc.constraints.DatabaseTable;

/**
 * Value conversion for {@link DatabaseTable} constraint.
 * 
 * @author Simon Templer
 */
public class DatabaseTableFactory implements ValueConstraintFactory<DatabaseTable> {

	private static final String NAME_SCHEMA = "schema";
	private static final String NAME_TABLE = "table";
	private static final String USE_QUOTE = "usequote";

	@Override
	public Value store(DatabaseTable constraint, Map<TypeDefinition, String> typeIndex)
			throws Exception {
		ValueProperties props = new ValueProperties();

		String schema = constraint.getSchemaName();
		if (schema != null) {
			props.put(NAME_SCHEMA, Value.of(schema));
		}

		String table = constraint.getTableName();
		if (table != null) {
			props.put(NAME_TABLE, Value.of(table));
		}

		boolean useQuote = constraint.useQuote();
		props.put(USE_QUOTE, Value.of(useQuote));

		return props.toValue();
	}

	@Override
	public DatabaseTable restore(Value value, Definition<?> definition,
			Map<String, TypeDefinition> typeIndex, ClassResolver resolver) throws Exception {
		String schema = null;
		String table = null;
		boolean useQuote = true;

		ValueProperties props = value.as(ValueProperties.class);
		if (props != null) {
			schema = props.getSafe(NAME_SCHEMA).as(String.class);
			table = props.getSafe(NAME_TABLE).as(String.class);
			useQuote = props.getSafe(USE_QUOTE).as(Boolean.class);
		}

		if (table == null) {
			throw new IllegalStateException("Database table constraint w/o table name");
		}
		return new DatabaseTable(schema, table, useQuote);
	}

}
