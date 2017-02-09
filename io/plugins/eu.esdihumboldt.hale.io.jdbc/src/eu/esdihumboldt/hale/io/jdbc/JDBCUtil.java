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
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;

import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.core.io.project.ProjectInfoService;
import eu.esdihumboldt.hale.common.core.io.project.ProjectVariableReplacer;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.io.jdbc.extension.JDBCSchemaReaderAdvisor;

/**
 * JDBC utility methods.
 * 
 * @author Kai Schwierczek
 * @author Simon Templer
 */
public class JDBCUtil {

	/**
	 * Removes one pair of leading/trailing quotes ("x" or 'x' or `x` becomes
	 * x).
	 * 
	 * @param s the string to remove quotes from
	 * @return the string with one pair of quotes less if possible
	 */
	public static String unquote(String s) {
		if (s == null) {
			return null;
		}

		char startChar = s.charAt(0);
		char endChar = s.charAt(s.length() - 1);
		if ((startChar == '\'' || startChar == '"' || startChar == '`') && startChar == endChar)
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
	public static String quote(String s) {
		if (s == null) {
			return null;
		}

		char startChar = s.charAt(0);
		char endChar = s.charAt(s.length() - 1);
		if ((startChar == '\'' || startChar == '"') && startChar == endChar)
			return s; // already quoted
		else
			return '"' + s + '"';
	}

	/**
	 * Determine the identifier quote string for a given JDBC connection.
	 * 
	 * @param connection the JDBC connection
	 * @return the quote string
	 */
	public static String determineQuoteString(Connection connection) {
		String quotes = "\"";
		try {
			quotes = connection.getMetaData().getIdentifierQuoteString();
			if (quotes.trim().isEmpty()) {
				quotes = "";
			}
		} catch (SQLException e) {
			// can't do anything about that
		}
		return quotes;
	}

	/**
	 * Determine the namespace for a JDBC source-
	 * 
	 * @param jdbcURI the JDBC connection URI
	 * @param advisor the schema reader advisor, if applicable
	 * @return the namespace
	 */
	public static String determineNamespace(URI jdbcURI,
			@Nullable JDBCSchemaReaderAdvisor advisor) {
		URI specificURI;
		try {
			specificURI = URI.create(jdbcURI.getRawSchemeSpecificPart());
		} catch (Exception e) {
			specificURI = jdbcURI;
		}
		StringBuilder ns = new StringBuilder();
		if (specificURI.getScheme() != null) {
			if (!specificURI.getScheme().equals("jdbc")) {
				ns.append("jdbc:");
			}
			ns.append(specificURI.getScheme());
		}
		if (specificURI.getPath() != null) {
			String path = null;
			if (advisor != null) {
				path = advisor.adaptPathForNamespace(specificURI.getPath());
			}
			else {
				// default handling
				path = specificURI.getPath();
				if (path.startsWith("/")) {
					path = path.substring(1);
				}
			}

			if (path != null && !path.isEmpty()) {
				if (ns.length() > 0) {
					ns.append(':');
				}

				ns.append(path);
			}
		}
		String overallNamespace = ns.toString();
		if (overallNamespace == null) {
			overallNamespace = "";
		}
		return overallNamespace;
	}

	/**
	 * Replace variables in an SQL query.
	 * 
	 * @param query the query
	 * @param services the service provider
	 * @return the query with variables replaced
	 */
	public static String replaceVariables(String query, ServiceProvider services) {
		if (services != null) {
			ProjectInfoService projectInfo = services.getService(ProjectInfoService.class);
			if (projectInfo != null) {
				ProjectVariableReplacer replacer = new ProjectVariableReplacer(projectInfo);
				return replacer.replaceVariables(query, true);
			}
		}
		return query;
	}

	/**
	 * Create a statement for a default read-only iteration query.
	 * 
	 * @param connection the JDBC connection
	 * @param fetchSize the batch fetch size, should be greater than zero
	 * @return the statement
	 * @throws SQLException if the statement cannot be created
	 */
	public static Statement createReadStatement(Connection connection, int fetchSize)
			throws SQLException {
		Statement st;
		try {
			st = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
					ResultSet.CLOSE_CURSORS_AT_COMMIT);
		} catch (SQLFeatureNotSupportedException e) {
			// Oracle Database supports only HOLD_CURSORS_OVER_COMMIT
			st = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
					ResultSet.HOLD_CURSORS_OVER_COMMIT);
		}
		st.setFetchSize(fetchSize);
		return st;
	}

}
