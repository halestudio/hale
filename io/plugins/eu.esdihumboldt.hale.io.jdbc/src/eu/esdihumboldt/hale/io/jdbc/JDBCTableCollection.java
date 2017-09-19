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

package eu.esdihumboldt.hale.io.jdbc;

import java.net.URI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.report.LogAware;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.instance.geometry.CRSProvider;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.ext.InstanceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.FilteredInstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.PseudoInstanceReference;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.jdbc.constraints.DatabaseTable;
import eu.esdihumboldt.hale.io.jdbc.constraints.SQLQuery;

/**
 * Instance collection for instances belonging to a specific database table or a
 * schema type with an associated SQL query.
 * 
 * @author Simon Templer
 */
public class JDBCTableCollection implements InstanceCollection, LogAware {

	private static final ALogger logger = ALoggerFactory.getLogger(JDBCTableCollection.class);

	/**
	 * Iterator other a JDBC table.
	 */
	private class JDBCTableIterator implements InstanceIterator {

		private final TableInstanceBuilder builder;

//		private static final int ROW_LIMIT = 100;

		private final Connection connection;

		private ResultSet currentResults;

//		private final int currentOffset = 0;

		/**
		 * States if the row at the current cursor position was already
		 * consumed.
		 * 
		 * Initially the result set points to the row before the first row,
		 * which by definition is consumed.
		 */
		private boolean consumed = true;

		private boolean hasNext = false;

		private boolean done = false;

		/**
		 * Default constructor.
		 * 
		 * @param crsProvider the CRS provider
		 */
		public JDBCTableIterator(CRSProvider crsProvider) {
			super();
			builder = new TableInstanceBuilder(crsProvider);
			try {
				connection = createConnection();
			} catch (SQLException e) {
				throw new IllegalStateException("Could not create database connection", e);
			}
		}

		@Override
		public TypeDefinition typePeek() {
			if (hasNext()) {
				// always the same type returned in this iterator
				return type;
			}
			return null;
		}

		@Override
		public boolean supportsTypePeek() {
			return true;
		}

		@Override
		public boolean hasNext() {
			proceedToNext();

			return hasNext;
		}

		/**
		 * Proceed to the next result
		 */
		private void proceedToNext() {
			if (done) {
				return;
			}

			try {
				if (currentResults != null) {
					// move cursor if necessary
					if (consumed) {
						hasNext = currentResults.next();
						consumed = false;
					}
				}

				if (currentResults != null && !hasNext) {
					// currentResults has been completely processed

					// set iterator to done
					close();
				}
				else if (currentResults == null) {
					// retrieve result set
					connection.setAutoCommit(false);
					Statement st = JDBCUtil.createReadStatement(connection, 500);
					currentResults = st.executeQuery(sqlQuery);

					proceedToNext();
				}
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
				close();
			}
		}

		@Override
		public Instance next() {
			proceedToNext();

			if (hasNext) {
				// create instance from current cursor
				Instance instance = builder.createInstance(type, currentResults, connection);

				consumed = true;

				return instance;
			}
			else {
				throw new IllegalStateException();
			}
		}

		@Override
		public void skip() {
			proceedToNext();

			if (hasNext) {
				// mark as consumed
				consumed = true;
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void close() {
			if (currentResults != null) {
				try {
					currentResults.close();
				} catch (SQLException e) {
					// ignore
				}
			}
			currentResults = null;
			done = true;
			hasNext = false;
			try {
				connection.close();
			} catch (SQLException e) {
				// ignore
			}
		}

	}

	private final URI jdbcURI;
	private final String user;
	private final String password;
	private final TypeDefinition type;

	private final String sqlQuery;
	private final String countQuery;
	private final CRSProvider crsProvider;
	private SimpleLog log = SimpleLog.fromLogger(logger);

	/**
	 * Constructor.
	 * 
	 * @param type the type definition associated to the table
	 * @param jdbcURI the JDBC URI to access the database
	 * @param user the database user
	 * @param password the user's password
	 * @param crsProvider crs provider
	 * @param services the service provider
	 */
	public JDBCTableCollection(TypeDefinition type, URI jdbcURI, String user, String password,
			CRSProvider crsProvider, ServiceProvider services) {
		this.type = type;
		this.jdbcURI = jdbcURI;
		this.user = user;
		this.password = password;
		this.crsProvider = crsProvider;

		String query = type.getConstraint(SQLQuery.class).getQuery();

		if (type.getConstraint(DatabaseTable.class).isTable() || query == null) {
			// database table queries

			String fullTableName = type.getConstraint(DatabaseTable.class).getFullTableName();
			query = "SELECT * FROM " + fullTableName;
			this.countQuery = "SELECT COUNT(*) FROM " + fullTableName;
		}
		else {
			// custom queries (not a database table)

			// support project variables
			query = JDBCUtil.replaceVariables(query, services);

			// this.countQuery = null;
			// countQuery = null caused to return UNKNOWN_SIZE in size() with
			// causes isEmpty()
			// to return false and that causes problems on iterating with
			// MultiInstanceCollection
			this.countQuery = "SELECT COUNT(*) FROM (\n" + query + "\n) tmp";
			// note 1: this sub query is not supported in all SQL dialects
			// note 2: the line breaks '\n' prevent from problems using comments
			// in the embedded query
		}
		this.sqlQuery = query;
	}

	/**
	 * Create a connection to the database.
	 * 
	 * @return the database connection
	 * @throws SQLException if opening the connection fails
	 */
	protected Connection createConnection() throws SQLException {
		return JDBCConnection.getConnection(jdbcURI, user, password);
	}

	@Override
	public InstanceReference getReference(Instance instance) {
		// TODO create a database backed reference instead?
		return new PseudoInstanceReference(instance);
	}

	@Override
	public Instance getInstance(InstanceReference reference) {
		if (reference instanceof PseudoInstanceReference) {
			return ((PseudoInstanceReference) reference).getInstance();
		}
		return null;
	}

	@Override
	public ResourceIterator<Instance> iterator() {
		return new JDBCTableIterator(crsProvider);
	}

	@Override
	public boolean hasSize() {
		return countQuery != null;
	}

	@Override
	public int size() {
		if (countQuery == null) {
			return UNKNOWN_SIZE;
		}

		try (Connection connection = createConnection()) {
			Statement st = connection.createStatement();

			ResultSet res = st.executeQuery(countQuery);
			int count = 0;
			if (res.next()) {
				count = res.getInt(1);
			}

			return count;
		} catch (SQLException e) {
			log.warn("Could not determine query size by count query");
			try (Connection connection = createConnection()) {
				Statement st = connection.createStatement();
				st.setMaxRows(1);
				ResultSet res = st.executeQuery(sqlQuery);
				if (res.next()) {
					return UNKNOWN_SIZE;
				}
				else {
					return 0;
				}
			} catch (SQLException e2) {
				log.error("Could not determine query size by counting query result", e2);
				// we can't query the database, so we won't be able to yield
				// instances
				return 0;
			}
		}
	}

	@Override
	public boolean isEmpty() {
		int size = size();
		return size == 0;
	}

	@Override
	public InstanceCollection select(Filter filter) {
		// TODO apply filter to query instead!
		return FilteredInstanceCollection.applyFilter(this, filter);
	}

	@Override
	public void setLog(SimpleLog log) {
		if (log != null) {
			this.log = log;
		}
		else {
			this.log = SimpleLog.fromLogger(logger);
		}
	}

}
