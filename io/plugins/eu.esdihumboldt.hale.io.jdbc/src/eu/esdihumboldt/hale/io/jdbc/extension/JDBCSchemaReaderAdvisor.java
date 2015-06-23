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
	 * Specifies if the path in the JDBC URI should be included in the namespace
	 * for the table types.
	 * 
	 * @return <code>true</code> if the path should be included in the
	 *         namespace, <code>false</code> otherwise
	 */
	boolean includePathInNamespace();

}
