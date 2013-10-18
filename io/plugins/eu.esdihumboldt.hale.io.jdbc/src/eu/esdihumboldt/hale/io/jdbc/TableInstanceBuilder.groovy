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

package eu.esdihumboldt.hale.io.jdbc

import java.sql.ResultSet
import java.sql.SQLException

import de.cs3d.util.logging.ALogger
import de.cs3d.util.logging.ALoggerFactory
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import groovy.transform.CompileStatic


/**
 * Creates instances from {@link ResultSet}s rows. Not thread safe as a shared
 * {@link InstanceBuilder} instance is used.
 * 
 * @author Simon Templer
 */
@CompileStatic
class TableInstanceBuilder {

	private static final ALogger log = ALoggerFactory.getLogger(TableInstanceBuilder)

	private final InstanceBuilder builder = new InstanceBuilder()

	/**
	 * Create an instance with the given type from a row in a SQL result set.
	 * 
	 * @param type the instance type
	 * @param row the result set with the cursor at the row to create the
	 *   instance from
	 * @return the created instance
	 */
	Instance createInstance(TypeDefinition type, ResultSet row) {
		// create instance
		builder.createInstance(type) {
			// create properties
			DefinitionUtil.getAllProperties(type).each { PropertyDefinition property ->
				// get property value
				try {
					Object value = row.getObject(property.name.localPart)

					// create property
					if (value != null) {
						builder.createProperty(property.name.localPart, value)
					}
				} catch (SQLException e) {
					// value could not be retrieved
					log.error("Could not retrieve value for column $property.name.localPart", e)
				}
			}
		}
	}
}
