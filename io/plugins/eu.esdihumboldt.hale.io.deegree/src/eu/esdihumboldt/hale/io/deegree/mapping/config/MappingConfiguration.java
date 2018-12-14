/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.deegree.mapping.config;

import java.util.Optional;

import org.deegree.feature.persistence.sql.GeometryStorageParams;
import org.deegree.sqldialect.SQLDialect;

/**
 * Interface for mapping configuration.
 * 
 * @author Simon Templer
 */
public interface MappingConfiguration {

	/**
	 * Get the mapping mode to be used.
	 * 
	 * @return the mapping mode
	 */
	MappingMode getMode();

	/**
	 * Get the mode for generating ID prefixes that should be used (for
	 * relational mapping).
	 * 
	 * @return the mode for generating ID prefixes
	 */
	IDPrefixMode getIDPrefixMode();

	/**
	 * Get the mode for determining primitive links.
	 * 
	 * @return the mode for determining primitive links
	 */
	PrimitiveLinkMode getPrimitiveLinkMode();

	/**
	 * Get the deegree SQL dialect.
	 * 
	 * @return the SQL dialect for the target database
	 */
	SQLDialect getSQLDialect();

	/**
	 * Get the custom maximum name length if set.
	 * 
	 * @return the maximum length for table and column names, if present
	 */
	Optional<Integer> getMaxNameLength();

	/**
	 * Get the connection ID for the deegree JDBC connection.
	 * 
	 * @return the JDBC connection ID
	 */
	String getJDBCConnectionId();

	/**
	 * Get the geometry storage parameters.
	 * 
	 * @return the geometry storage parameters
	 */
	GeometryStorageParams getGeometryStorageParameters();

	/**
	 * Specifies if namespace prefixes should be used for table names.
	 * 
	 * @return <code>true</code> if namespace prefixes should be used for table
	 *         names, <code>false</code> otherwise
	 */
	boolean useNamespacePrefixForTableNames();

	/**
	 * Specifies if integer identifiers should be used in the database. Usually
	 * implies that existing IDs cannot be reused.
	 * 
	 * @return <code>true</code> if integer identifiers should be used,
	 *         <code>false</code> otherwise
	 */
	boolean useIntegerIDs();

	/**
	 * Validate the configuration.
	 * 
	 * @throws Exception on a validation error
	 */
	default void validate() throws Exception {
		String id = getJDBCConnectionId();
		if (id == null || id.isEmpty()) {
			throw new IllegalStateException("JDBC connection ID must be provided");
		}

		if (getMode() == null) {
			throw new IllegalStateException("Mapping mode must be configured");
		}

		if (getSQLDialect() == null) {
			throw new IllegalStateException("Database type must be configured");
		}

		if (getGeometryStorageParameters() == null) {
			throw new IllegalStateException("FeatureStore CRS must be configured");
		}
	}

}
