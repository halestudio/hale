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
import java.util.Map;

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
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.io.impl.AbstractSchemaReader;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchema;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;

/**
 * Reads a database schema through JDBC.
 * @author Simon Templer
 */
public class JDBCSchemaReader extends AbstractSchemaReader {
	
	/**
	 * Parameter name for the user name
	 */
	public static final String PARAM_USER = "jdbc.user";
	
	/**
	 * Parameter name for the user password
	 */
	public static final String PARAM_PASSWORD = "jdbc.password";
	
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
		try {
			URI jdbcURI = getSource().getLocation();
			if (jdbcURI == null) {
				fail("JDBC URI needed");
				return null; // to suppress warning
			}
			if (!jdbcURI.toString().startsWith("jdbc:")) {
				fail("Invalid JDBC URI");
			}
			
			String user = getParameter(PARAM_USER);
			String password = getParameter(PARAM_PASSWORD);
			
			// connect to the database
			Connection connection = JDBCConnection.getConnection(jdbcURI, user, password);
			
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
					
					for (final Column column : table.getColumns()) {
						// each column is a property
						
						// determine the column type
						TypeDefinition columnType = getOrCreateColumnType(
								column.getType(), overallNamespace, typeIndex, reporter);
						
						// create the property
						new DefaultPropertyDefinition(new QName(
								column.getName()), type, columnType);
					}
					
					// configure type
					type.setConstraint(MappableFlag.ENABLED);
					type.setConstraint(MappingRelevantFlag.ENABLED);
					type.setConstraint(HasValueFlag.DISABLED);
					
					typeIndex.addType(type);
				}
			}
			
			reporter.setSuccess(true);
		} catch (SQLException e) {
			throw new IOProviderConfigurationException("Failed to connect to database", e);
		} catch (SchemaCrawlerException e) {
			throw new IOProviderConfigurationException("Failed to read database schema", e);
		}
		finally {
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
	 * @return the type definition for the column type
	 */
	protected TypeDefinition getOrCreateColumnType(ColumnDataType columnType, final String namespace, 
			DefaultSchema types, IOReporter reporter) {
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
		
		// configure type
		try {
			Class<?> binding = Class.forName(columnType.getTypeClassName()); //TODO more sophisticated class loading?
			type.setConstraint(Binding.get(binding));
			
			type.setConstraint(HasValueFlag.ENABLED);
		} catch (ClassNotFoundException e) {
			reporter.error(new IOMessageImpl("Could not create property type binding", e));
		}
		
		types.addType(type);
		return type;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider#storeConfiguration(java.util.Map)
	 */
	@Override
	public void storeConfiguration(Map<String, String> configuration) {
		// TODO Auto-generated method stub
		super.storeConfiguration(configuration);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider#setParameter(java.lang.String, java.lang.String)
	 */
	@Override
	public void setParameter(String name, String value) {
		// TODO Auto-generated method stub
		super.setParameter(name, value);
	}

	/**
	 * @see AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "Database";
	}

}
