/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.inspire.schemas

import groovy.transform.Immutable


/**
 * TODO Type description
 * 
 * @author Simon Templer
 */
@Immutable
class SchemaInfo {
	/** the location of the XSD file */
	URI location
	/** the schema namespace */
	String namespace
	/** application schema identifier in the registry */
	String appSchemaId
	/** the schema version */
	String version
	/** the schema name */
	String name
}
