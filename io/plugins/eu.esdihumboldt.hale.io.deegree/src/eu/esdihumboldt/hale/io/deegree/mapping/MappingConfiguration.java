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

package eu.esdihumboldt.hale.io.deegree.mapping;

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

	// TODO validate?

}
