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

package eu.esdihumboldt.hale.io.jdbc.extension.internal;

import java.sql.Connection;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.io.jdbc.extension.JDBCSchemaReaderAdvisor;

/**
 * Descriptor for a {@link JDBCSchemaReaderAdvisor} extension.
 * 
 * @author Simon Templer
 */
public interface SchemaReaderAdvisorDescriptor extends
		ExtensionObjectFactory<JDBCSchemaReaderAdvisor> {

	/**
	 * Determines if the advisor applies to a database with the given
	 * connection.
	 * 
	 * @param connection the database connection
	 * @return if the advisor should be used for the database
	 */
	public boolean applies(Connection connection);

}
