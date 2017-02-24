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

package eu.esdihumboldt.hale.io.jdbc;

import static eu.esdihumboldt.hale.io.jdbc.JDBCUtil.determineNamespace;
import static eu.esdihumboldt.hale.io.jdbc.JDBCUtil.determineQuoteString;
import static eu.esdihumboldt.hale.io.jdbc.JDBCUtil.unquote;

import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Text;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchema;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.common.schema.persist.AbstractCachedSchemaReader;
import eu.esdihumboldt.hale.io.jdbc.constraints.SQLArray;
import eu.esdihumboldt.hale.io.jdbc.constraints.SQLQuery;
import eu.esdihumboldt.hale.io.jdbc.extension.JDBCSchemaReaderAdvisor;
import eu.esdihumboldt.hale.io.jdbc.extension.internal.SchemaReaderAdvisorExtension;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.ResultsColumn;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.utility.SchemaCrawlerUtility;

/**
 * Reads the schema of an SQL query via JDBC.
 * 
 * @author Simon Templer
 */
public class SQLSchemaReader extends AbstractCachedSchemaReader
		implements JDBCConstants, JDBCProvider {

	/**
	 * Name of the parameter specifying the SQL query.
	 */
	public static final String PARAM_SQL = "sql";

	/**
	 * Name of the parameter specifying the type name to use for the SQL query.
	 */
	public static final String PARAM_TYPE_NAME = "typename";

	/**
	 * Fixed namespace for SQL types.
	 */
	public static final String NAMESPACE = "jdbc:sql";

	/**
	 * Default constructor
	 */
	public SQLSchemaReader() {
		super();

		addSupportedParameter(PARAM_USER);
		addSupportedParameter(PARAM_PASSWORD);
	}

	@Override
	protected boolean useCache(Value cache) {
		// check if a connection can be established
		try {
			Connection conn = JDBCConnection.getConnection(this);
			conn.close();
			return false;
		} catch (Exception e) {
			// on error, use cache
			return true;
		}
	}

	/**
	 * To get Connection. Override this to load the customized connection
	 * 
	 * @return Connection object after loading driver.
	 * @throws SQLException if connection could not be made.
	 */
	@Override
	public Connection getConnection() throws SQLException {
		return JDBCConnection.getConnection(this);
	}

	@Override
	protected Schema loadFromSource(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		DefaultSchema typeIndex = null;

		String query = null;
		Text text = getParameter(PARAM_SQL).as(Text.class);
		if (text != null) {
			query = text.getText();
		}
		if (query == null) {
			query = getParameter(PARAM_SQL).as(String.class);
		}
		if (query == null) {
			reporter.setSuccess(false);
			reporter.setSummary("No SQL query specified");
			return null;
		}

		String typename = getParameter(PARAM_TYPE_NAME).as(String.class);
		if (typename == null) {
			reporter.setSuccess(false);
			reporter.setSummary(
					"Name of the type that the SQL query should be represented as must be specified");
			return null;
		}

		progress.begin("Read SQL query schema", ProgressIndicator.UNKNOWN);
		Connection connection = null;
		try {
			// connect to the database
			try {
				connection = getConnection();
			} catch (Exception e) {
				reporter.error(new IOMessageImpl(e.getLocalizedMessage(), e));
				reporter.setSuccess(false);
				reporter.setSummary("Failed to connect to database.");
				return null;
			}

			// don't fail if Connection.setReadOnly() throws an exception (e.g.
			// SQLite JDBC driver does not allow changing the flag after the
			// connection has been created), report a warning message instead
			try {
				connection.setReadOnly(true);
			} catch (SQLException e) {
				// ignore
//				reporter.warn(new IOMessageImpl(e.getLocalizedMessage(), e));
			}

			connection.setAutoCommit(false);

			// get advisor
			JDBCSchemaReaderAdvisor advisor = SchemaReaderAdvisorExtension.getInstance()
					.getAdvisor(connection);

			// determine quotes character
			@SuppressWarnings("unused")
			String quotes = determineQuoteString(connection);
			// FIXME not actually used here or in JDBC schema reader

			URI jdbcURI = getSource().getLocation();
			String dbNamespace = determineNamespace(jdbcURI, advisor);
			String namespace = NAMESPACE;

			SchemaCrawlerOptions options = new SchemaCrawlerOptions();
			SchemaInfoLevel level = new SchemaInfoLevel();
			level.setTag("hale");
			// these are enabled by default, we don't need them (yet)
			level.setRetrieveSchemaCrawlerInfo(false);
			level.setRetrieveJdbcDriverInfo(false);
			level.setRetrieveDatabaseInfo(false);
			level.setRetrieveTables(false);
			level.setRetrieveTableColumns(false);
			level.setRetrieveForeignKeys(false);
			// set what we need
			level.setRetrieveColumnDataTypes(true);
			level.setRetrieveUserDefinedColumnDataTypes(true);
			options.setSchemaInfoLevel(level);
			if (advisor != null) {
				advisor.configureSchemaCrawler(options);
			}
			final Catalog database = SchemaCrawlerUtility.getCatalog(connection, options);

			// create the type index
			typeIndex = new DefaultSchema(dbNamespace, jdbcURI);

			Statement st = null;
			try {
				st = JDBCUtil.createReadStatement(connection, 1);

				// support project variables
				String processedQuery = JDBCUtil.replaceVariables(query, getServiceProvider());

				ResultSet result = st.executeQuery(processedQuery);

				// the query represents a type

				// get the type definition
				TypeDefinition type = addTableType(query, namespace, typeIndex, connection,
						reporter, typename);

				ResultsColumns additionalInfo = SchemaCrawlerUtility.getResultColumns(result);
				for (final ResultsColumn column : additionalInfo.getColumns()) {
					getOrCreateProperty(type, column, namespace, typeIndex, connection, reporter,
							database);
				}

			} finally {
				if (st != null) {
					st.close();
				}
			}

			reporter.setSuccess(true);
		} catch (Exception e) {
			throw new IOProviderConfigurationException("Failed to read database schema", e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// ignore
				}
			}
			progress.end();
		}

		return typeIndex;
	}

	/**
	 * Gets or creates a property definition for the given column. Its type
	 * definition is created, too, if necessary.
	 * 
	 * @param tableType the type definition of the parent table this column
	 *            belongs too
	 * @param column the column to get or create a property definition for
	 * @param namespace the schema namespace
	 * @param typeIndex the type index
	 * @param connection the database connection
	 * @param reporter the reporter
	 * @param catalog the database information
	 * @return the property definition for the given column
	 */
	private DefaultPropertyDefinition getOrCreateProperty(TypeDefinition tableType,
			ResultsColumn column, String namespace, DefaultSchema typeIndex, Connection connection,
			IOReporter reporter, Catalog catalog) {
		QName name = new QName(unquote(column.getName()));

		// check for existing property definition
		ChildDefinition<?> existing = tableType.getChild(name);
		if (existing != null)
			return (DefaultPropertyDefinition) existing;

		// create new one
		// determine the column type
		TypeDefinition columnType = JDBCSchemaReader.getOrCreateColumnType(column, namespace,
				typeIndex, connection, tableType, reporter, catalog);

		SQLArray arrayInfo = columnType.getConstraint(SQLArray.class);

		// create the property
		DefaultPropertyDefinition property = new DefaultPropertyDefinition(name, tableType,
				columnType);

		// configure property
		if (column.getRemarks() != null && !column.getRemarks().isEmpty()) {
			property.setDescription(column.getRemarks());
		}
		property.setConstraint(NillableFlag.get(column.isNullable()));
		if (arrayInfo.isArray() && arrayInfo.getDimension() <= 1) {
			// XXX for now, use multiple occurrence representation also if
			// dimension is not known (0)
			if (!arrayInfo.hasSize(0)) {
				property.setConstraint(Cardinality.CC_ANY_NUMBER);
			}
			else {
				long min = 0; // XXX what is appropriate as minimum?
				long max = arrayInfo.getSize(0);
				property.setConstraint(Cardinality.get(min, max));
			}
		}
		else {
			property.setConstraint(Cardinality.CC_EXACTLY_ONCE);
		}

		return property;
	}

	/**
	 * Create the type definition for a query.
	 * 
	 * @param query the SQL query
	 * @param namespace the namespace for the type
	 * @param types the schema to add the type to
	 * @param connection the database connection
	 * @param reporter the reporter
	 * @param typename the name to use for the type
	 * 
	 * @return the type definition for the given table
	 */
	private TypeDefinition addTableType(String query, String namespace, DefaultSchema types,
			Connection connection, IOReporter reporter, String typename) {
		QName typeName = new QName(namespace, unquote(typename));

		// check for existing type
		TypeDefinition existingType = types.getType(typeName);
		if (existingType != null)
			return existingType;

		// create new type
		DefaultTypeDefinition type = new DefaultTypeDefinition(typeName);

		// set SQL query as description
		type.setDescription(query);

		type.setConstraint(new SQLQuery(query));

		// configure type
		type.setConstraint(MappableFlag.ENABLED);
		type.setConstraint(MappingRelevantFlag.ENABLED);
		type.setConstraint(HasValueFlag.DISABLED);

		types.addType(type);
		return type;
	}

	@Override
	protected String getDefaultTypeName() {
		return "Database";
	}

}
