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

package eu.esdihumboldt.hale.io.jdbc;

import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;

import javax.xml.namespace.QName;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Database;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.utility.SchemaCrawlerUtility;
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
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchema;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.io.jdbc.extension.internal.GeometryTypeExtension;
import eu.esdihumboldt.hale.io.jdbc.extension.internal.GeometryTypeInfo;

/**
 * Reads a database schema through JDBC.
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
			SchemaInfoLevel level = SchemaInfoLevel.minimum();
			level.setRetrieveProcedures(false);
			level.setRetrieveColumnDataTypes(true);
			level.setRetrieveUserDefinedColumnDataTypes(true);
		    level.setRetrieveTableColumns(true);
		    level.setRetrieveForeignKeys(true);
//		    level.setRetrieveIndices(true);
		    level.setTag("hale");
			options.setSchemaInfoLevel(level);
			
			final Database database = SchemaCrawlerUtility.getDatabase(connection, options);
			
			String overallNamespace = jdbcURI.toString();
			if (jdbcURI.getRawFragment() != null) {
				overallNamespace = overallNamespace.substring(0,
						overallNamespace.length()
								- jdbcURI.getRawFragment().length());
			}
			
			// create the type index
			typeIndex = new DefaultSchema(overallNamespace, jdbcURI);
			
			for (final schemacrawler.schema.Schema schema : database.getSchemas()) {
				// each schema represents a namespace
				String namespace = overallNamespace + "/" + schema.getName();
				
				for (final Table table : schema.getTables()) {
					// each table is a type
					
					// create the type definition
					DefaultTypeDefinition type = new DefaultTypeDefinition(
							new QName(namespace, table.getName()));
					if (table.getRemarks() != null && !table.getRemarks().isEmpty()) {
						type.setDescription(table.getRemarks());
					}
					
					for (final Column column : table.getColumns()) {
						// each column is a property
						
						// determine the column type
						TypeDefinition columnType = getOrCreateColumnType(
								column.getType(), overallNamespace, typeIndex, reporter,
								connection);
						
						// create the property
						DefaultPropertyDefinition property = new DefaultPropertyDefinition(
								new QName(column.getName()), type, columnType);
						
						// configure property
						if (column.getRemarks() != null && !column.getRemarks().isEmpty()) {
							property.setDescription(column.getRemarks());
						}
						property.setConstraint(NillableFlag.get(column.isNullable()));
						//XXX if default value present cardinality = optional?
						property.setConstraint(Cardinality.CC_EXACTLY_ONCE);
					}
					
					// configure type
					type.setConstraint(MappableFlag.ENABLED);
					type.setConstraint(MappingRelevantFlag.ENABLED);
					type.setConstraint(HasValueFlag.DISABLED);
					
					typeIndex.addType(type);
				}
			}
			
			reporter.setSuccess(true);
		} catch (SchemaCrawlerException e) {
			throw new IOProviderConfigurationException("Failed to read database schema", e);
		}
		finally {
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
	 * Get or create the type definition for the given column type.
	 * @param columnType the column type
	 * @param namespace the database namespace
	 * @param types the type index
	 * @param reporter the reporter
	 * @param connection the database connection
	 * @return the type definition for the column type
	 */
	protected TypeDefinition getOrCreateColumnType(ColumnDataType columnType, final String namespace, 
			DefaultSchema types, IOReporter reporter, Connection connection) {
		//XXX what about shared types?
		
		String localName = columnType.getName();
		
		QName typeName = new QName(namespace, localName);
		
		TypeDefinition existing = types.getType(typeName);
		if (existing != null) {
			// return existing type
			return existing;
		}
		
		// create new type
		DefaultTypeDefinition type = new DefaultTypeDefinition(typeName);
		
		// check for geometry type
		GeometryTypeInfo geomType = GeometryTypeExtension.getInstance().getTypeInfo(
				columnType.getName(), connection);
		
		if (geomType != null) {
			// configure geometry type
			type.setConstraint(GeometryType.get(geomType.getGeometryType()));
			type.setConstraint(Binding.get(GeometryProperty.class)); // always a single geometry
			
			type.setConstraint(HasValueFlag.ENABLED);
		}
		else {
			// configure type
			try {
				Class<?> binding = Class.forName(columnType.getTypeClassName()); //TODO more sophisticated class loading?
				type.setConstraint(Binding.get(binding));
				
				type.setConstraint(HasValueFlag.ENABLED);
			} catch (ClassNotFoundException e) {
				reporter.error(new IOMessageImpl("Could not create property type binding", e));
			}
		}
		
		//TODO validation constraint
		
		if (columnType.getRemarks() != null && !columnType.getRemarks().isEmpty()) {
			type.setDescription(columnType.getRemarks());
		}
		
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
