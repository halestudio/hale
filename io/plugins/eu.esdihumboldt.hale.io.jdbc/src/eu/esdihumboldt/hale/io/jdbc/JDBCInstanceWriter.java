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

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceWriter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;

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
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator, eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		InstanceCollection instances = getInstances();
		
		boolean trackProgress = instances.hasSize();
		progress.begin("Write instances to database",
				(trackProgress) ? (instances.size())
						: (ProgressIndicator.UNKNOWN));
		
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
			
			ResourceIterator<Instance> it = instances.iterator();
			try {
				while (it.hasNext() && !progress.isCanceled()) {
					Instance instance = it.next();
					
					try {
						writeInstance(instance, connection);
					} catch (Exception e) {
						reporter.error(new IOMessageImpl("Error saving an instance to the database", e));
					}
					
					if (trackProgress) {
						progress.advance(1);
					}
				}
			} finally {
				it.close();
			}
			
			if (progress.isCanceled() && it.hasNext()) {
				reporter.error(new IOMessageImpl("Saving to database was canceled, not all instances were saved.", null));
			}
			
			reporter.setSuccess(true);
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
	 * Write an instance to the database.
	 * @param instance the instance
	 * @param connection the database connection
	 * @throws SQLException if writing the instance fails
	 */
	private void writeInstance(Instance instance, Connection connection) throws SQLException {
		//TODO transaction?!
		
		String tableName = instance.getDefinition().getName().getLocalPart();
		
		// create prepared statement SQL
		StringBuffer pSql = new StringBuffer();
		
		pSql.append("INSERT INTO \"");
		pSql.append(tableName);
		pSql.append("\" (");
		
		StringBuffer valuesSql = new StringBuffer();
		
		boolean first = true;
		for (QName column : instance.getPropertyNames()) {
			if (first) {
				first = false;
			}
			else {
				pSql.append(", ");
				valuesSql.append(",");
			}
			
			pSql.append(column.getLocalPart());
			valuesSql.append('?');
		}
		
		pSql.append(") VALUES (");
		pSql.append(valuesSql);
		pSql.append(")");
		
		// create prepared statement
		PreparedStatement statement = connection.prepareStatement(pSql.toString()); //XXX what about auto-generated keys
		//TODO statement reuse?!
		try {
			int index = 1;
			for (QName column : instance.getPropertyNames()) {
				Object[] values = instance.getProperty(column);
				Object value = (values == null || values.length == 0)?(null):(values[0]); //TODO warn multiple values being ignored
				
				if (value == null) {
//					statement.setNull(parameterIndex, sqlType);
					statement.setObject(index, null);
				}
				else {
					setStatementParameter(statement, index, value, 
							instance.getDefinition().getChild(column).asProperty()); //XXX there should be a check first - maybe building a map in the first iteration?
				}
				
				index++;
			}
			
//			statement.executeQuery();
			statement.execute();
		}
		finally {
			statement.close();
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
