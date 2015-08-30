/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.jdbc.extension;

import javax.annotation.Nullable;

import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import eu.esdihumboldt.hale.io.jdbc.JDBCSchemaReader;

/**
 * Advisor for the behavior of a {@link JDBCSchemaReader}.
 * 
 * @author Simon Templer
 */
public interface JDBCSchemaReaderAdvisor {

	/**
	 * Adapt the options passed to SchemaCrawler to retrieve the database
	 * schema.
	 * 
	 * @param options the schema crawler options
	 */
	void configureSchemaCrawler(SchemaCrawlerOptions options);

	/**
	 * Determine the path that should be included in the namespace for table
	 * types.
	 * 
	 * @param path the JDBC URI path
	 * @return the string to use for the namespace in addition to the protocol,
	 *         e.g. the database name
	 */
	@Nullable
	String adaptPathForNamespace(String path);

}
