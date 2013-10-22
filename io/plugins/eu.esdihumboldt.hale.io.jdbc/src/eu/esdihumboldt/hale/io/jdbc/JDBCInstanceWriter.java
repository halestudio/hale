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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;
import eu.esdihumboldt.hale.io.jdbc.constraints.DefaultValue;
import eu.esdihumboldt.hale.io.jdbc.constraints.SQLType;
import eu.esdihumboldt.hale.io.jdbc.constraints.internal.GeometryAdvisorConstraint;

/**
 * Writes instance to a database through a JDBC connection.
 * 
 * @author Simon Templer
 */
public class JDBCInstanceWriter extends AbstractInstanceWriter implements JDBCConstants {

	/**
	 * Default constructor.
	 */
	public JDBCInstanceWriter() {
		super();

		addSupportedParameter(PARAM_USER);
		addSupportedParameter(PARAM_PASSWORD);
	}

	@Override
	public boolean isCancelable() {
		return true;
	}

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
	 * Write instances to a database connection
	 * 
	 * @param connection the database connection
	 * @param instances the instances to write
	 * @param progress the progress indicator
	 * @param reporter the reporter
	 * @throws Exception if saving the instances fails
	 */
	private void writeInstances(Connection connection, InstanceCollection instances,
			ProgressIndicator progress, IOReporter reporter) throws Exception {
		connection.setAutoCommit(false);

		boolean trackProgress = instances.hasSize();
		progress.begin("Write instances to database", (trackProgress) ? (instances.size())
				: (ProgressIndicator.UNKNOWN));

		// maps type definitions to prepared statements
		Map<TypeDefinition, Map<Set<QName>, PreparedStatement>> typeStatements = new HashMap<TypeDefinition, Map<Set<QName>, PreparedStatement>>();
		Map<TypeDefinition, Map<Set<QName>, Integer>> typeCount = new HashMap<TypeDefinition, Map<Set<QName>, Integer>>();

		// TODO some kind of ordering needed?! because of IDs and constraints!

		ResourceIterator<Instance> it = instances.iterator();
		try {
			while (it.hasNext() && !progress.isCanceled()) {
				Instance instance = it.next();
				TypeDefinition type = instance.getDefinition();

				Set<QName> properties = new HashSet<QName>();
				for (QName property : instance.getPropertyNames())
					properties.add(property);
				filterInsertProperties(type, properties);

				// per type count
				Map<Set<QName>, Integer> typeCountMap = typeCount.get(type);
				if (typeCountMap == null) {
					typeCountMap = new HashMap<Set<QName>, Integer>();
					typeCount.put(type, typeCountMap);
				}
				Integer count = typeCountMap.get(properties);
				if (count == null)
					count = 0;
				typeCountMap.put(properties, count + 1);

				// get prepared statement for instance type
				PreparedStatement statement = getInsertStatement(type, properties, typeStatements,
						connection);

				// populate insert statement with values
				populateInsertStatement(statement, properties, instance, reporter);

				statement.addBatch();

				if (count % 100 == 0) {
					statement.executeBatch();
					// TODO statement.getGeneratedKeys() / does not work with
					// batches for PostgreSQL
				}

				if (trackProgress) {
					progress.advance(1);
				}
			}

			// execute remaining batches
			for (Map<Set<QName>, PreparedStatement> typeSpecificMap : typeStatements.values()) {
				if (progress.isCanceled())
					break;
				for (PreparedStatement statement : typeSpecificMap.values()) {
					if (progress.isCanceled())
						break;
					statement.executeBatch();
				}
			}

			if (!progress.isCanceled())
				connection.commit();
			else
				connection.rollback();
		} catch (Exception e) {
			connection.rollback();
			throw e;
		} finally {
			// close iterator
			it.close();

			// close statements
			for (Map<Set<QName>, PreparedStatement> typeSpecificMap : typeStatements.values())
				for (PreparedStatement statement : typeSpecificMap.values())
					statement.close();
		}

		// right now cancel => rollback. Otherwise this would have to be in
		// front of it.close()...
//		if (progress.isCanceled() && it.hasNext()) {
//			reporter.error(new IOMessageImpl("Saving to database was canceled, not all instances were saved.", null));
//		}
	}

	/**
	 * Filters the set of properties to only contain properties that can be used
	 * for inserting (e. g. no groups).
	 * 
	 * @param type the type definition
	 * @param properties the available properties
	 */
	private void filterInsertProperties(TypeDefinition type, Set<QName> properties) {
		for (ChildDefinition<?> child : type.getChildren()) {
			if (properties.contains(child.getName())) {
				if (child.asProperty() == null) {
					// remove it since it is a group and no property
					// XXX warn?
					properties.remove(child.getName());
				}
			}
		}
	}

	/**
	 * Create a prepared insert statement, based on the given type definition.
	 * 
	 * @param type the type definition
	 * @param properties the set properties of the instance for which this
	 *            statement is
	 * @param typeStatements the already created statements
	 * @param connection the database connection
	 * @return the insert statement
	 * @throws SQLException if creating the prepared statement fails
	 */
	private PreparedStatement getInsertStatement(TypeDefinition type, Set<QName> properties,
			Map<TypeDefinition, Map<Set<QName>, PreparedStatement>> typeStatements,
			Connection connection) throws SQLException {
		Map<Set<QName>, PreparedStatement> typeSpecificMap = typeStatements.get(type);
		if (typeSpecificMap == null) {
			typeSpecificMap = new HashMap<Set<QName>, PreparedStatement>();
			typeStatements.put(type, typeSpecificMap);
		}

		PreparedStatement result = typeSpecificMap.get(properties);

		if (result == null) {
			String tableName = type.getName().getLocalPart();

			// create prepared statement SQL
			StringBuffer pSql = new StringBuffer();

			pSql.append("INSERT INTO \"");
			pSql.append(tableName);
			pSql.append("\" (");

			StringBuffer valuesSql = new StringBuffer();

			boolean first = true;
			for (QName property : properties) {
				if (first)
					first = false;
				else {
					pSql.append(", ");
					valuesSql.append(",");
				}

				pSql.append('"').append(property.getLocalPart()).append('"');
				valuesSql.append('?');
			}

			pSql.append(") VALUES (");
			pSql.append(valuesSql);
			pSql.append(")");

			// XXX Actually we don't necessarily need the auto generated keys,
			// we need the primary key!
			// XXX , Statement.RETURN_GENERATED_KEYS does not work with batches
			// in PostgreSQL
			result = connection.prepareStatement(pSql.toString());
			typeSpecificMap.put(properties, result);
		}

		return result;
	}

	/**
	 * Populate a prepared insert statement with values from the given instance.
	 * 
	 * @param statement the insert statement
	 * @param properties the properties to fill the statement with
	 * @param instance the instance
	 * @param reporter the reporter
	 * @throws SQLException if configuring the statement fails
	 */
	private void populateInsertStatement(PreparedStatement statement, Set<QName> properties,
			Instance instance, IOReporter reporter) throws SQLException {
		TypeDefinition type = instance.getDefinition();

		int index = 1;
		for (QName propertyName : properties) {
			PropertyDefinition property = (PropertyDefinition) type.getChild(propertyName);
			Object[] values = instance.getProperty(propertyName);
			SQLType sqlType = property.getPropertyType().getConstraint(SQLType.class);
			if (!sqlType.isSet()) {
				reporter.error(new IOMessageImpl(
						"SQL type not set. Please only export to schemas read from a database.",
						null));
				statement.setObject(index, null);
				continue;
			}
			if (values != null && values.length > 1)
				reporter.warn(new IOMessageImpl(
						"Multiple values for a property. Only exporting first.", null));
			Object value = (values == null || values.length == 0) ? null : values[0];

			if (values == null || values.length == 0) {
				// XXX The default value could be a function call.
				// Better would be to leave the column out of the insert
				// statement, or set it to the SQL keyword "DEFAULT".
				DefaultValue defaultValue = property.getConstraint(DefaultValue.class);
				if (defaultValue.isSet())
					statement.setObject(index, defaultValue.getValue(), sqlType.getType());
				else if (property.getConstraint(NillableFlag.class).isEnabled())
					statement.setNull(index, sqlType.getType());
				else {
					// no default, not nillable, will not work...
					// set it to null here and let query fail (probably)
					// XXX maybe skip this insert?
					statement.setNull(index, sqlType.getType());
					reporter.warn(new IOMessageImpl(
							"Property no value, not nillable, no default value, insert will probably fail.",
							null));
				}
			}
			else if (value == null)
				statement.setNull(index, sqlType.getType());

			else
				setStatementParameter(statement, index, value, property, sqlType.getType(),
						reporter);

			index++;
		}
	}

	/**
	 * Set a prepared statement parameter value.
	 * 
	 * @param statement the prepared statement
	 * @param index the parameter index
	 * @param value the value, not <code>null</code>
	 * @param propertyDef the associated property definition
	 * @param sqlType the SQL type
	 * @param reporter the reporter
	 * @throws SQLException if setting the parameter fails
	 */
	private void setStatementParameter(PreparedStatement statement, int index, Object value,
			PropertyDefinition propertyDef, int sqlType, IOReporter reporter) throws SQLException {
		if (propertyDef.getPropertyType().getConstraint(GeometryType.class).isGeometry()) {
			// is a geometry column

			// get the geometry advisor
			GeometryAdvisor<?> advisor = propertyDef.getPropertyType()
					.getConstraint(GeometryAdvisorConstraint.class).getAdvisor();
			if (advisor != null) {
				// use the advisor to convert the geometry
				if (value instanceof GeometryProperty<?>) {
					// XXX JTS geometry conversion needed beforehand?
					try {
						value = advisor.convertGeometry((GeometryProperty<?>) value,
								propertyDef.getPropertyType());
					} catch (Exception e) {
						reporter.error(new IOMessageImpl("Something went wrong during conversion",
								e));
					}
				}
				else {
					reporter.error(new IOMessageImpl(
							"Geometry value is not of type GeometryProperty and could thus not be converted for the database",
							null));
				}
			}
		}

		// TODO handling of other types?

		// set the value
		statement.setObject(index, value, sqlType);
	}

	@Override
	protected String getDefaultTypeName() {
		return "Database";
	}
}
