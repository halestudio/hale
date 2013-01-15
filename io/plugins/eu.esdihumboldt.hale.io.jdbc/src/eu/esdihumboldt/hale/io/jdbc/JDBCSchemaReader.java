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

import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;

import javax.xml.namespace.QName;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Database;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.ResultsColumn;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.utility.SchemaCrawlerUtility;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.io.impl.AbstractSchemaReader;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Reference;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchema;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.io.jdbc.constraints.AutoIncrementFlag;
import eu.esdihumboldt.hale.io.jdbc.constraints.DefaultValue;
import eu.esdihumboldt.hale.io.jdbc.constraints.SQLType;
import eu.esdihumboldt.hale.io.jdbc.extension.internal.GeometryTypeExtension;
import eu.esdihumboldt.hale.io.jdbc.extension.internal.GeometryTypeInfo;

/**
 * Reads a database schema through JDBC.
 * 
 * @author Simon Templer
 */
public class JDBCSchemaReader extends AbstractSchemaReader implements JDBCConstants {

	private DefaultSchema typeIndex;

	/**
	 * Default constructor
	 */
	public JDBCSchemaReader() {
		super();

		addSupportedParameter(PARAM_USER);
		addSupportedParameter(PARAM_PASSWORD);
	}

	/**
	 * @see SchemaReader#getSchema()
	 */
	@Override
	public Schema getSchema() {
		return typeIndex;
	}

	/**
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see AbstractIOProvider#execute(ProgressIndicator, IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		typeIndex = null;

		progress.begin("Read database schema", ProgressIndicator.UNKNOWN);
		Connection connection = null;
		try {
			// connect to the database
			try {
				connection = JDBCConnection.getConnection(this);
				connection.setReadOnly(true);
			} catch (Exception e) {
				reporter.error(new IOMessageImpl(e.getLocalizedMessage(), e));
				reporter.setSuccess(false);
				reporter.setSummary("Failed to connect to database.");
				return reporter;
			}

			URI jdbcURI = getSource().getLocation();

			final SchemaCrawlerOptions options = new SchemaCrawlerOptions();
			SchemaInfoLevel level = new SchemaInfoLevel();
			level.setTag("hale");
			// these are enabled by default, we don't need them (yet)
			level.setRetrieveSchemaCrawlerInfo(false);
			level.setRetrieveJdbcDriverInfo(false);
			level.setRetrieveDatabaseInfo(false);

			// set what we need
			level.setRetrieveTables(true);
			level.setRetrieveColumnDataTypes(true);
			level.setRetrieveUserDefinedColumnDataTypes(true);
			level.setRetrieveTableColumns(true); // to get table columns
													// information, also
													// includes primary key
			level.setRetrieveForeignKeys(true); // to get linking information
//			level.setRetrieveIndices(true); // to get info about UNIQUE indices for validation
			// XXX For some advanced info / DBMS specific info we'll need a
			// properties file. See Config & InformationSchemaViews.
			level.setTag("hale");
			options.setSchemaInfoLevel(level);

			final Database database = SchemaCrawlerUtility.getDatabase(connection, options);
			String quotes = "\"";
			try {
				quotes = connection.getMetaData().getIdentifierQuoteString();
				if (quotes.trim().isEmpty())
					quotes = "";
			} catch (SQLException e) {
				// can't do anything about that
			}

			String overallNamespace = jdbcURI.toString();
			if (jdbcURI.getRawFragment() != null) {
				overallNamespace = overallNamespace.substring(0, overallNamespace.length()
						- jdbcURI.getRawFragment().length());
			}

			// create the type index
			typeIndex = new DefaultSchema(overallNamespace, jdbcURI);

			for (final schemacrawler.schema.Schema schema : database.getSchemas()) {
				// each schema represents a namespace
				String namespace = overallNamespace + "/" + unquote(schema.getName());

				for (final Table table : schema.getTables()) {
					// each table is a type

					// get the type definition
					TypeDefinition type = getOrCreateTableType(table, overallNamespace, namespace,
							typeIndex, connection, reporter);

					// get ResultSetMetaData for extra info about columns (e. g.
					// auto increment)
					ResultsColumns additionalInfo = null;
					Statement stmt = null;
					try {
						stmt = connection.createStatement();
						ResultSet rs = stmt.executeQuery("SELECT * FROM " + quote(schema.getName())
								+ "." + quote(table.getName()) + " WHERE 1 = 0");
						additionalInfo = SchemaCrawlerUtility.getResultColumns(rs);
					} catch (SQLException sqle) {
						reporter.warn(new IOMessageImpl(
								"Couldn't retrieve additional column meta data.", sqle));
					} finally {
						if (stmt != null)
							try {
								stmt.close();
							} catch (SQLException e) {
								// ignore
							}
					}

					// create property definitions for each column
					for (final Column column : table.getColumns()) {
						DefaultPropertyDefinition property = getOrCreateProperty(type, column,
								overallNamespace, namespace, typeIndex, connection, reporter);

						// Set auto increment flag if meta data says so.
						// Not sure, whether that covers every case of
						// "auto increment" for every DBMS (probably not).
						// But there is nothing else we can do in general
						// without lots of DBMS specific code or asking the user
						// for input.
						// XXX does not work for example for PostgreSQL
						if (additionalInfo != null) {
							// ResultColumns does not quote the column namen in
							// contrast to every other place
							ResultsColumn rc = additionalInfo.getColumn(unquote(column.getName()));
							if (rc.isAutoIncrement())
								property.setConstraint(AutoIncrementFlag.get(true));
						}
					}
				}
			}

			reporter.setSuccess(true);
		} catch (SchemaCrawlerException e) {
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

		return reporter;
	}

	/**
	 * Removes one pair of leading/trailing quotes ("x" or 'x' becomes x).
	 * 
	 * @param s the string to remove quotes from
	 * @return the string with one pair of quotes less if possible
	 */
	private String unquote(String s) {
		char startChar = s.charAt(0);
		char endChar = s.charAt(s.length() - 1);
		if ((startChar == '\'' || startChar == '"') && startChar == endChar)
			return s.substring(1, s.length() - 1);
		else
			return s;
	}

	/**
	 * Adds a pair of quotes ("x") if no quotes (" or ') are present.
	 * 
	 * @param s the string to quote
	 * @return the quoted string
	 */
	private String quote(String s) {
		char startChar = s.charAt(0);
		char endChar = s.charAt(s.length() - 1);
		if ((startChar == '\'' || startChar == '"') && startChar == endChar)
			return s; // already quoted
		else
			return '"' + s + '"';
	}

	/**
	 * Gets or creates a property definition for the given column. Its type
	 * definition is created, too, if necessary.
	 * 
	 * @param tableType the type definition of the parent table this column
	 *            belongs too
	 * @param column the column to get or create a property definition for
	 * @param overallNamespace the database namespace
	 * @param namespace the schema namespace
	 * @param typeIndex the type index
	 * @param connection the database connection
	 * @param reporter the reporter
	 * @return the property definition for the given column
	 */
	private DefaultPropertyDefinition getOrCreateProperty(TypeDefinition tableType, Column column,
			String overallNamespace, String namespace, DefaultSchema typeIndex,
			Connection connection, IOReporter reporter) {
		QName name = new QName(unquote(column.getName()));

		// check for existing property definition
		ChildDefinition<?> existing = tableType.getChild(name);
		if (existing != null)
			return (DefaultPropertyDefinition) existing;

		// create new one
		// determine the column type
		TypeDefinition columnType = getOrCreateColumnType(column, overallNamespace, typeIndex,
				connection, tableType, reporter);

		// create the property
		DefaultPropertyDefinition property = new DefaultPropertyDefinition(name, tableType,
				columnType);

		// configure property
		if (column.getRemarks() != null && !column.getRemarks().isEmpty()) {
			property.setDescription(column.getRemarks());
		}
		property.setConstraint(NillableFlag.get(column.isNullable()));
		// XXX Default value is read as string from the meta data.
		// This is probably not really a problem, but should be noted!
		// XXX In particular the default value can be a function call like for
		// example GETDATE().
		String defaultValue = column.getDefaultValue();
		if (defaultValue != null) {
			property.setConstraint(new DefaultValue(defaultValue));
			property.setConstraint(Cardinality.CC_OPTIONAL);
		}
		else
			property.setConstraint(Cardinality.CC_EXACTLY_ONCE);

		// XXX constraint for column.isPartOfPrimaryKey(),
		// column.isPartOfUniqueIndex()
		// XXX what if the foreign key consists of multiple columns?
		// those indices/foreign keys should maybe belong to the table type,
		// since they can have multiple columns
		if (column.isPartOfForeignKey()) {
			Column referenced = column.getReferencedColumn();
			property.setConstraint(new Reference(getOrCreateTableType(referenced.getParent(),
					overallNamespace, namespace, typeIndex, connection, reporter)));
		}

		return property;
	}

	/**
	 * Get or create the type definition for the given column.
	 * 
	 * @param column the column
	 * @param overallNamespace the database namespace
	 * @param types the type index
	 * @param connection the database connection
	 * @param tableType the type definition of the table the column is part of
	 * @param reporter the reporter
	 * @return the type definition for the column type
	 */
	private TypeDefinition getOrCreateColumnType(Column column, final String overallNamespace,
			DefaultSchema types, Connection connection, TypeDefinition tableType,
			IOReporter reporter) {
		// XXX what about shared types?
		// TODO the size/width info (VARCHAR(_30_)) is in column, the
		// columntype/-name is not sufficient

		ColumnDataType columnType = column.getType();
		String localName = columnType.getName();

		QName typeName = new QName(overallNamespace, localName);

		// check for geometry type
		GeometryTypeInfo geomType = GeometryTypeExtension.getInstance().getTypeInfo(
				columnType.getName(), connection);
		@SuppressWarnings("rawtypes")
		GeometryAdvisor geomAdvisor = null;
		if (geomType != null) {
			geomAdvisor = geomType.getGeometryAdvisor();

			// determine if a type specifically for this column is needed
			if (!geomAdvisor.isFixedType(columnType)) {
				// must use a specific type definition for this column
				// -> use a type name based on the column

				// new namespace is the table and column name
				String ns = tableType.getName().getNamespaceURI() + '/'
						+ tableType.getName().getLocalPart() + '/' + unquote(column.getName());

				typeName = new QName(ns, localName);
			}
		}

		// check for existing type
		TypeDefinition existing = types.getType(typeName);
		if (existing != null)
			return existing;

		// create new type
		DefaultTypeDefinition type = new DefaultTypeDefinition(typeName);
		type.setConstraint(HasValueFlag.ENABLED);
		type.setConstraint(SQLType.get(columnType.getType()));

		if (geomType != null && geomAdvisor != null) {
			// configure geometry type
			@SuppressWarnings("unchecked")
			Class<? extends Geometry> geomClass = geomAdvisor.configureGeometryColumnType(
					connection, column, type);
			type.setConstraint(GeometryType.get(geomClass));
			// always a single geometry
			type.setConstraint(Binding.get(GeometryProperty.class));
			// remember advisor for type (used in instance writer)
			type.setConstraint(geomType.getConstraint());
		}
		else {
			// configure type
			try {
				// XXX more sophisticated class loading?
				Class<?> binding = Class.forName(column.getType().getTypeClassName());
				type.setConstraint(Binding.get(binding));

				type.setConstraint(HasValueFlag.ENABLED);
			} catch (ClassNotFoundException e) {
				reporter.error(new IOMessageImpl("Could not create property type binding", e));
			}
		}

		// TODO validation constraint

		if (columnType.getRemarks() != null && !columnType.getRemarks().isEmpty())
			type.setDescription(columnType.getRemarks());

		types.addType(type);
		return type;
	}

	/**
	 * Get or create the type definition for the given table.
	 * 
	 * @param table the table
	 * @param overallNamespace the database namespace
	 * @param namespace the schema namespace
	 * @param types the type index
	 * @param connection the database connection
	 * @param reporter the reporter
	 * @return the type definition for the given table
	 */
	private TypeDefinition getOrCreateTableType(Table table, String overallNamespace,
			String namespace, DefaultSchema types, Connection connection, IOReporter reporter) {
		QName typeName = new QName(namespace, unquote(table.getName()));

		// check for existing type
		TypeDefinition existingType = types.getType(typeName);
		if (existingType != null)
			return existingType;

		// create new type
		DefaultTypeDefinition type = new DefaultTypeDefinition(typeName);

		// set description if available
		if (table.getRemarks() != null && !table.getRemarks().isEmpty())
			type.setDescription(table.getRemarks());

		// configure type
		type.setConstraint(MappableFlag.ENABLED);
		type.setConstraint(MappingRelevantFlag.ENABLED);
		type.setConstraint(HasValueFlag.DISABLED);

		// set primary key if possible
		PrimaryKey key = table.getPrimaryKey();
		if (key != null) {
			IndexColumn[] columns = key.getColumns();
			if (columns.length > 1) {
				reporter.warn(new IOMessageImpl(
						"Primary keys over multiple columns are not yet supported.", null));
			}
			else if (columns.length == 1) {
				// create constraint, get property definition for original table
				// column (maybe could use index column, too)
				type.setConstraint(new eu.esdihumboldt.hale.common.schema.model.constraint.type.PrimaryKey(
						Collections.<ChildDefinition<?>> singletonList(getOrCreateProperty(type,
								table.getColumn(columns[0].getName()), overallNamespace, namespace,
								types, connection, reporter))));
			}
		}

		// TODO validation of constraints? Most are about several instances (i.
		// e. UNIQUE)

		types.addType(type);
		return type;
	}

	/**
	 * @see AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "Database";
	}
}
