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

package eu.esdihumboldt.hale.common.inspire.codelists

import javax.annotation.concurrent.Immutable


/**
 * A reference to a remote code list.
 *  
 * @author Simon Templer
 */
@Immutable
class CodeListRef {
	/** the location of the code list */
	URI location

	/** the code list name */
	String name

	/** the code list description */
	String description

	/** the code list definition */
	String definition

	/** the name of the schema the code list is associated to */
	String schemaName

	/** the schema ID and registry URL */
	String schemaId

	/** the name of the theme the code list is associated to */
	String themeName
}
