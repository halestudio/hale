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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceWriter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Writes instance to a database through a JDBC connection.
 * @author Simon Templer
 */
public class JDBCInstanceWriter extends AbstractInstanceWriter implements JDBCConstants {

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return true;
	}

	/**
	 * @see AbstractIOProvider#execute(ProgressIndicator, IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		InstanceCollection instances = getInstances();
		
		Connection connection = null;
		try {
			// connect to the database
			try {
				connection = JDBCConnection.getConnection(this);
			} catch (Exception e) {
				reporter.error(new IOMessageImpl(e.getLocalizedMessage(), e));
				reporter.setSuccess(false);
				reporter.setSummary("Failed to connect to database.");
				return reporter;
			}
			
//			URI jdbcURI = getTarget().getLocation();
			
			writeInstances(connection, instances, progress, reporter);
			
			reporter.setSuccess(true);
		} catch (Exception e) {
			reporter.error(new IOMessageImpl(e.getLocalizedMessage(), e));
			reporter.setSuccess(false);
			reporter.setSummary("Saving instances to database failed.");
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
	 * Write instances to a database connection
	 * @param connection the database connection
	 * @param instances the instances to write
	 * @param progress the progress indicator
	 * @param reporter the reporter
	 * @throws Exception if saving the instances fails
	 */
	private void writeInstances(Connection connection,
			InstanceCollection instances, ProgressIndicator progress,
			IOReporter reporter) throws Exception {
		connection.setAutoCommit(false);
		
		boolean trackProgress = instances.hasSize();
		progress.begin("Write instances to database",
				(trackProgress) ? (instances.size())
						: (ProgressIndicator.UNKNOWN));
		
		// maps type definitions to prepared statements
		Map<TypeDefinition, PreparedStatement> typeStatements = new HashMap<TypeDefinition, PreparedStatement>();
		Map<TypeDefinition, Integer> typeCount = new HashMap<TypeDefinition, Integer>();
		
		//TODO some kind of ordering needed?! because of IDs and constraints!
		
		ResourceIterator<Instance> it = instances.iterator();
		try {
			while (it.hasNext() && !progress.isCanceled()) { //XXX should cancel result in a rollback?
				Instance instance = it.next();
				TypeDefinition type = instance.getDefinition();
				// per type count
				int count;
				if (!typeCount.containsKey(type)) {
					// first of that type
					count = 1;
				}
				else {
					// increase count
					count = typeCount.get(type) + 1;
				}
				typeCount.put(type, count);
				
				
				// get prepared statement for instance type
				PreparedStatement statement = getInsertStatement(type, 
						typeStatements, connection);
				
				// populate insert statement with values
				populateInsertStatement(statement, instance);
				
				statement.addBatch();
				
				if (count % 100 == 0) {
					statement.executeBatch();
				}
				
				if (trackProgress) {
					progress.advance(1);
				}
			}
			
			// execute remaining batches
			for (PreparedStatement statement : typeStatements.values()) {
				statement.executeBatch();
			}
			
			connection.commit();
		} catch (Exception e) {
			connection.rollback();
			throw e;
		} finally {
			// close iterator
			it.close();
			
			// close statements
			for (PreparedStatement statement : typeStatements.values()) {
				statement.close();
			}
		}
		
		if (progress.isCanceled() && it.hasNext()) {
			reporter.error(new IOMessageImpl("Saving to database was canceled, not all instances were saved.", null));
		}
	}

	private Iterable<? extends PropertyDefinition> getInsertProperties(TypeDefinition type) {
		List<PropertyDefinition> result = new ArrayList<PropertyDefinition>();
		for (ChildDefinition<?> child : type.getChildren()) {
			if (child.asProperty() != null) {
				result.add(child.asProperty());
			}
			// groups are not supported and ignored
		}
		return result;
	}

	/**
	 * Create a prepared insert statement, based on the given type definition.
	 * @param type the type definition
	 * @param typeStatements the already created statements 
	 * @param connection the database connection
	 * @return the insert statement
	 * @throws SQLException if creating the prepared statement fails
	 */
	private PreparedStatement getInsertStatement(TypeDefinition type,
			Map<TypeDefinition, PreparedStatement> typeStatements, Connection connection) throws SQLException {
		PreparedStatement result = typeStatements.get(type);
		
		if (result == null) {
			String tableName = type.getName().getLocalPart();
			
			// create prepared statement SQL
			StringBuffer pSql = new StringBuffer();
			
			pSql.append("INSERT INTO \"");
			pSql.append(tableName);
			pSql.append("\" (");
			
			StringBuffer valuesSql = new StringBuffer();
			
			boolean first = true;
			for (PropertyDefinition property : getInsertProperties(type)) {
				//XXX what about auto-generated keys?!
				
				if (first) {
					first = false;
				}
				else {
					pSql.append(", ");
					valuesSql.append(",");
				}
				
				pSql.append(property.getName().getLocalPart());
				valuesSql.append('?');
			}
			
			pSql.append(") VALUES (");
			pSql.append(valuesSql);
			pSql.append(")");
			
			result = connection.prepareStatement(pSql.toString());
			typeStatements.put(type, result);
		}
		
		return result;
	}
	
	/**
	 * Populate a prepared insert statement with values from the given instance.
	 * @param statement the insert statement
	 * @param instance the instance
	 * @throws SQLException if configuring the statement fails
	 */
	private void populateInsertStatement(PreparedStatement statement,
			Instance instance) throws SQLException {
		TypeDefinition type = instance.getDefinition();
		
		int index = 1;
		for (PropertyDefinition property : getInsertProperties(type)) {
			Object[] values = instance.getProperty(property.getName());
			Object value = (values == null || values.length == 0)?(null):(values[0]); //TODO warn multiple values being ignored
			
			if (value == null) {
				// null value
//				statement.setNull(parameterIndex, sqlType);
				statement.setObject(index, null);
			}
			else {
				// non-null value
				setStatementParameter(statement, index, value, 
						property);
			}
			
			index++;
		}
	}

	/**
	 * Set a prepared statement parameter value.
	 * @param statement the prepared statement
	 * @param index the parameter index
	 * @param value the value
	 * @param propertyDef the associated property definition
	 * @throws SQLException if setting the parameter fails
	 */
	private void setStatementParameter(PreparedStatement statement, int index,
			Object value, PropertyDefinition propertyDef) throws SQLException {
		statement.setObject(index, value);
		
		//TODO handling of geometries, other types?
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "Database";
	}

}
